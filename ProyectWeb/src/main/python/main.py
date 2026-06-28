from fastapi import FastAPI
from pydantic import BaseModel
from typing import Optional
import base64
import binascii
import numpy as np
import cv2
import os
from deepface import DeepFace
from fastapi.middleware.cors import CORSMiddleware

from db_queries import (
    obtener_usuarios_con_rostro,
    obtener_usuario_por_identificacion,
    actualizar_ruta_rostro
)

app = FastAPI()

# =====================================================
# CONFIGURACIÓN GENERAL
# =====================================================

# Ruta absoluta del directorio donde está este main.py
BASE_DIR = os.path.dirname(os.path.abspath(__file__))

# Carpeta real donde viven las imágenes de usuarios
# En Railway quedará algo como:
# /app/src/main/python/usuarios
CARPETA_USUARIOS = os.path.join(BASE_DIR, "usuarios")

# Crear carpeta si no existe
os.makedirs(CARPETA_USUARIOS, exist_ok=True)

# Umbral de acceso.
# Si niega demasiado, puedes subirlo a 0.30 o 0.35.
# Si permite demasiado, bájalo a 0.22 o 0.20.
UMBRAL_ACCESO = 0.25

# Margen entre el mejor usuario y el segundo mejor.
# Evita accesos ambiguos.
MARGEN_SEGURIDAD = 0.10

# Máximo de fotos permitidas por usuario
MAX_FOTOS_POR_USUARIO = 5

# Modelo de reconocimiento
MODELO_RECONOCIMIENTO = "Facenet512"

# Detector
DETECTOR_BACKEND = "opencv"

# Métrica
DISTANCE_METRIC = "cosine"


# =====================================================
# CORS
# =====================================================

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


# =====================================================
# MODELOS
# =====================================================

class ImagenValidacion(BaseModel):
    imagen: str


class ImagenRegistro(BaseModel):
    imagen: str
    identificacion: str
    numero_foto: Optional[int] = 1


# =====================================================
# FUNCIONES AUXILIARES
# =====================================================

def convertir_base64_a_frame(base64_string: str):
    """
    Convierte una imagen base64 a frame OpenCV.
    """

    if not base64_string or not isinstance(base64_string, str):
        return None

    try:
        if "," in base64_string:
            base64_string = base64_string.split(",", 1)[1]

        img_bytes = base64.b64decode(base64_string, validate=True)
        np_arr = np.frombuffer(img_bytes, np.uint8)
        frame = cv2.imdecode(np_arr, cv2.IMREAD_COLOR)

        return frame

    except (binascii.Error, ValueError, Exception) as e:
        print(f"[ERROR] No se pudo convertir la imagen base64: {e}")
        return None


def normalizar_ruta(ruta_archivo):
    """
    Normaliza rutas para que funcionen tanto en Windows como en Railway/Linux.

    Soporta:
    - usuarios\\1032388086
    - usuarios/1032388086
    - usuarios/1032388086.jpg
    - /app/src/main/python/usuarios/1032388086
    """

    if not ruta_archivo:
        return None

    ruta = str(ruta_archivo).strip()

    # Cambiar backslash de Windows por slash compatible
    ruta = ruta.replace("\\", "/")

    # Si ya es absoluta, se usa tal cual
    if os.path.isabs(ruta):
        return ruta

    # Si viene como usuarios/1032388086, se une a BASE_DIR
    return os.path.join(BASE_DIR, ruta)


def validar_calidad_imagen(img):
    """
    Valida calidad básica de la imagen:
    - Tamaño mínimo.
    - Brillo aceptable.
    - Nitidez aceptable.
    """

    if img is None:
        return False, "Imagen inválida o no se pudo procesar"

    alto, ancho = img.shape[:2]

    if ancho < 300 or alto < 300:
        return False, "La imagen es muy pequeña. Acércate más a la cámara"

    gris = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)

    brillo = np.mean(gris)

    if brillo < 40:
        return False, "La imagen está muy oscura"

    if brillo > 245:
        return False, "La imagen está demasiado iluminada"

    nitidez = cv2.Laplacian(gris, cv2.CV_64F).var()

    if nitidez < 25:
        return False, "La imagen está borrosa. Intenta nuevamente"

    return True, "Calidad de imagen válida"


def validar_rostro_en_imagen(img):
    """
    Valida que la imagen tenga un rostro detectable.
    """

    if img is None:
        return False, "Imagen inválida o no se pudo procesar"

    calidad_valida, mensaje_calidad = validar_calidad_imagen(img)

    if not calidad_valida:
        return False, mensaje_calidad

    try:
        rostros = DeepFace.extract_faces(
            img_path=img,
            detector_backend=DETECTOR_BACKEND,
            enforce_detection=True,
            align=True
        )

        if not rostros:
            return False, "No se detectó ningún rostro en la imagen"

        if len(rostros) > 1:
            return False, "Se detectó más de un rostro en la imagen"

        return True, "Rostro válido"

    except Exception as e:
        print(f"[ERROR] Validación de rostro fallida: {e}")
        return False, "No se detectó un rostro válido en la imagen"


def obtener_carpeta_usuario(identificacion: str):
    """
    Retorna la carpeta donde se guardan las imágenes del usuario.

    Ejemplo:
    /app/src/main/python/usuarios/1032388086
    """

    identificacion_limpia = str(identificacion).strip()
    carpeta_usuario = os.path.join(CARPETA_USUARIOS, identificacion_limpia)

    os.makedirs(carpeta_usuario, exist_ok=True)

    return carpeta_usuario


def obtener_ruta_rostro(identificacion: str, numero_foto: int = 1):
    """
    Genera la ruta de una foto específica del usuario.

    Ejemplo:
    /app/src/main/python/usuarios/1032388086/rostro_1.jpg
    """

    if numero_foto < 1:
        numero_foto = 1

    if numero_foto > MAX_FOTOS_POR_USUARIO:
        numero_foto = MAX_FOTOS_POR_USUARIO

    carpeta_usuario = obtener_carpeta_usuario(identificacion)
    nombre_archivo = f"rostro_{numero_foto}.jpg"

    return os.path.join(carpeta_usuario, nombre_archivo)


def obtener_imagenes_usuario(ruta_archivo):
    """
    Obtiene todas las imágenes válidas de un usuario.

    Soporta:
    1. Ruta antigua: usuarios/1032388086.jpg
    2. Ruta nueva: usuarios/1032388086/rostro_1.jpg
    3. Ruta nueva por carpeta: usuarios/1032388086/
    4. Rutas absolutas de Railway.
    5. Rutas con backslash de Windows.
    """

    imagenes = []

    if not ruta_archivo:
        return imagenes

    ruta_normalizada = normalizar_ruta(ruta_archivo)

    print(f"[INFO] Ruta original BD: {ruta_archivo}")
    print(f"[INFO] Ruta normalizada: {ruta_normalizada}")

    extensiones_validas = (".jpg", ".jpeg", ".png")

    # Caso 1: la ruta es un archivo específico
    if ruta_normalizada and os.path.isfile(ruta_normalizada):
        if ruta_normalizada.lower().endswith(extensiones_validas):
            imagenes.append(ruta_normalizada)

        return imagenes

    # Caso 2: la ruta es una carpeta de usuario
    if ruta_normalizada and os.path.isdir(ruta_normalizada):
        for archivo in os.listdir(ruta_normalizada):
            ruta_completa = os.path.join(ruta_normalizada, archivo)

            if os.path.isfile(ruta_completa) and archivo.lower().endswith(extensiones_validas):
                imagenes.append(ruta_completa)

        return imagenes

    # Caso 3: si la BD tiene usuarios/1032388086 pero existe como carpeta en CARPETA_USUARIOS
    posible_identificacion = os.path.basename(str(ruta_archivo).replace("\\", "/"))
    posible_carpeta = os.path.join(CARPETA_USUARIOS, posible_identificacion)

    if os.path.isdir(posible_carpeta):
        for archivo in os.listdir(posible_carpeta):
            ruta_completa = os.path.join(posible_carpeta, archivo)

            if os.path.isfile(ruta_completa) and archivo.lower().endswith(extensiones_validas):
                imagenes.append(ruta_completa)

        return imagenes

    # Caso 4: si la BD apunta a usuarios/1032388086 y existe usuarios/1032388086.jpg
    posible_archivo = os.path.join(CARPETA_USUARIOS, f"{posible_identificacion}.jpg")

    if os.path.isfile(posible_archivo):
        imagenes.append(posible_archivo)

    return imagenes


def comparar_con_usuario(frame, usuario):
    """
    Compara la imagen capturada contra todas las fotos registradas de un usuario.
    Retorna la menor distancia encontrada.
    """

    ruta_archivo = usuario.get("archivo")
    imagenes_usuario = obtener_imagenes_usuario(ruta_archivo)

    if not imagenes_usuario:
        print(
            f"[WARN] No hay imágenes válidas para usuario "
            f"{usuario.get('identificacion')}: {ruta_archivo}"
        )
        return None

    mejor_distancia_usuario = 999

    for imagen_registrada in imagenes_usuario:
        try:
            result = DeepFace.verify(
                img1_path=frame,
                img2_path=imagen_registrada,
                model_name=MODELO_RECONOCIMIENTO,
                detector_backend=DETECTOR_BACKEND,
                distance_metric=DISTANCE_METRIC,
                enforce_detection=True,
                align=True
            )

            distancia = float(result.get("distance", 999))

            print(
                f"[VALIDACION] Usuario: {usuario.get('identificacion')} | "
                f"Imagen: {imagen_registrada} | "
                f"Distancia: {distancia} | "
                f"Verified DeepFace: {result.get('verified')}"
            )

            if distancia < mejor_distancia_usuario:
                mejor_distancia_usuario = distancia

        except Exception as e:
            print(
                f"[ERROR] Comparando usuario {usuario.get('identificacion')} "
                f"con imagen {imagen_registrada}: {e}"
            )

    if mejor_distancia_usuario == 999:
        return None

    return mejor_distancia_usuario


# =====================================================
# ENDPOINTS DE DIAGNÓSTICO
# =====================================================

@app.get("/")
def health():
    return {
        "estado": "Servidor biométrico activo",
        "base_dir": BASE_DIR,
        "carpeta_usuarios": CARPETA_USUARIOS,
        "existe_carpeta_usuarios": os.path.exists(CARPETA_USUARIOS)
    }


@app.get("/debug/usuarios")
def debug_usuarios():
    """
    Permite revisar qué carpetas y archivos existen en Railway.
    """

    resultado = {
        "base_dir": BASE_DIR,
        "carpeta_usuarios": CARPETA_USUARIOS,
        "existe_carpeta_usuarios": os.path.exists(CARPETA_USUARIOS),
        "contenido": []
    }

    if os.path.exists(CARPETA_USUARIOS):
        for item in os.listdir(CARPETA_USUARIOS):
            ruta_item = os.path.join(CARPETA_USUARIOS, item)

            if os.path.isdir(ruta_item):
                resultado["contenido"].append({
                    "tipo": "carpeta",
                    "nombre": item,
                    "ruta": ruta_item,
                    "archivos": os.listdir(ruta_item)
                })
            else:
                resultado["contenido"].append({
                    "tipo": "archivo",
                    "nombre": item,
                    "ruta": ruta_item
                })

    return resultado


# =====================================================
# ENDPOINT VALIDAR
# =====================================================

@app.post("/validar")
async def validar(imagen: ImagenValidacion):

    print("[INFO] Iniciando validación biométrica")

    frame = convertir_base64_a_frame(imagen.imagen)

    if frame is None:
        return {
            "resultado": "denegado",
            "mensaje": "Imagen inválida o no se pudo procesar"
        }

    rostro_valido, mensaje_rostro = validar_rostro_en_imagen(frame)

    if not rostro_valido:
        return {
            "resultado": "denegado",
            "mensaje": mensaje_rostro
        }

    usuarios = obtener_usuarios_con_rostro()

    print(f"[INFO] Usuarios con rostro desde BD: {len(usuarios) if usuarios else 0}")

    if not usuarios:
        return {
            "resultado": "denegado",
            "mensaje": "No hay usuarios con rostro registrado en la base de datos"
        }

    resultados = []

    for usuario in usuarios:
        distancia_usuario = comparar_con_usuario(frame, usuario)

        if distancia_usuario is not None:
            resultados.append({
                "usuario": usuario,
                "distancia": distancia_usuario
            })

    if not resultados:
        return {
            "resultado": "denegado",
            "mensaje": "No se pudo comparar contra usuarios registrados",
            "umbral": UMBRAL_ACCESO
        }

    resultados.sort(key=lambda item: item["distancia"])

    mejor = resultados[0]
    segunda_mejor = resultados[1] if len(resultados) > 1 else None

    mejor_usuario = mejor["usuario"]
    mejor_distancia = mejor["distancia"]

    segunda_distancia = segunda_mejor["distancia"] if segunda_mejor else None

    if segunda_distancia is not None:
        diferencia_seguridad = segunda_distancia - mejor_distancia
    else:
        diferencia_seguridad = 999

    print(
        f"[RESULTADO FINAL] Mejor usuario: {mejor_usuario.get('identificacion')} | "
        f"Mejor distancia: {mejor_distancia} | "
        f"Segunda distancia: {segunda_distancia} | "
        f"Diferencia seguridad: {diferencia_seguridad}"
    )

    if mejor_distancia <= UMBRAL_ACCESO and diferencia_seguridad >= MARGEN_SEGURIDAD:
        return {
            "resultado": "acceso",
            "persona": f"{mejor_usuario.get('nombre')} {mejor_usuario.get('apellido')}",
            "identificacion": mejor_usuario.get("identificacion"),
            "rol": mejor_usuario.get("rol"),
            "telefono": mejor_usuario.get("telefono"),
            "numero_ficha": mejor_usuario.get("numero_ficha"),
            "distancia": mejor_distancia,
            "segunda_distancia": segunda_distancia,
            "diferencia_seguridad": diferencia_seguridad,
            "umbral": UMBRAL_ACCESO,
            "margen_seguridad": MARGEN_SEGURIDAD
        }

    if mejor_distancia > UMBRAL_ACCESO:
        mensaje = "No se encontró coincidencia facial suficientemente segura"
    else:
        mensaje = "Coincidencia ambigua: el rostro se parece a más de un usuario"

    return {
        "resultado": "denegado",
        "mensaje": mensaje,
        "distancia_minima": mejor_distancia,
        "segunda_distancia": segunda_distancia,
        "diferencia_seguridad": diferencia_seguridad,
        "umbral": UMBRAL_ACCESO,
        "margen_seguridad": MARGEN_SEGURIDAD
    }


# =====================================================
# ENDPOINT GUARDAR ROSTRO
# =====================================================

@app.post("/guardar_rostro")
def guardar_rostro(data: ImagenRegistro):

    print(f"[INFO] Guardando rostro para identificación: {data.identificacion}")

    usuario = obtener_usuario_por_identificacion(data.identificacion)

    if not usuario:
        return {
            "resultado": "error",
            "mensaje": "No existe un usuario con esa identificación"
        }

    img = convertir_base64_a_frame(data.imagen)

    if img is None:
        return {
            "resultado": "error",
            "mensaje": "Imagen inválida o no se pudo procesar"
        }

    rostro_valido, mensaje_rostro = validar_rostro_en_imagen(img)

    if not rostro_valido:
        return {
            "resultado": "error",
            "mensaje": mensaje_rostro
        }

    numero_foto = data.numero_foto if data.numero_foto else 1

    if numero_foto < 1 or numero_foto > MAX_FOTOS_POR_USUARIO:
        return {
            "resultado": "error",
            "mensaje": f"El número de foto debe estar entre 1 y {MAX_FOTOS_POR_USUARIO}"
        }

    ruta = obtener_ruta_rostro(data.identificacion, numero_foto)

    guardado = cv2.imwrite(ruta, img)

    if not guardado:
        return {
            "resultado": "error",
            "mensaje": "No fue posible guardar la imagen"
        }

    carpeta_usuario = obtener_carpeta_usuario(data.identificacion)

    actualizado = actualizar_ruta_rostro(data.identificacion, carpeta_usuario)

    if not actualizado:
        return {
            "resultado": "error",
            "mensaje": "La imagen se guardó, pero no fue posible actualizar la base de datos"
        }

    return {
        "resultado": "guardado",
        "archivo": ruta,
        "carpeta_usuario": carpeta_usuario,
        "identificacion": data.identificacion,
        "numero_foto": numero_foto,
        "usuario": f"{usuario.get('nombre')} {usuario.get('apellido')}"
    }


# =====================================================
# ARRANQUE LOCAL / RAILWAY
# =====================================================

if __name__ == "__main__":
    import uvicorn

    host = os.getenv("PYTHON_HOST", "127.0.0.1")
    port = int(os.getenv("PYTHON_PORT", "8000"))

    uvicorn.run(app, host=host, port=port)
