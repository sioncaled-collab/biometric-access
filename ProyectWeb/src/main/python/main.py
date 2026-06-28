from fastapi import FastAPI
from pydantic import BaseModel
import base64
import numpy as np
import cv2
import os
from deepface import DeepFace
from fastapi.middleware.cors import CORSMiddleware

app = FastAPI()

# ======== CORS =========
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Carpeta de usuarios
CARPETA_USUARIOS = "usuarios"
os.makedirs(CARPETA_USUARIOS, exist_ok=True)

USUARIOS = []


def cargar_usuarios():
    """
    Carga/recalcula la lista de rostros registrados desde la carpeta usuarios.
    """
    global USUARIOS

    USUARIOS = []

    if not os.path.exists(CARPETA_USUARIOS):
        os.makedirs(CARPETA_USUARIOS, exist_ok=True)

    for archivo in os.listdir(CARPETA_USUARIOS):
        if archivo.lower().endswith((".jpg", ".jpeg", ".png")):
            path = os.path.join(CARPETA_USUARIOS, archivo)

            USUARIOS.append({
                "nombre": os.path.splitext(archivo)[0],
                "ruta": path
            })

            print(f"[OK] Usuario cargado: {archivo}")

    print(f"[INFO] Total usuarios cargados: {len(USUARIOS)}")


# Cargar usuarios al iniciar
cargar_usuarios()


class ImagenValidacion(BaseModel):
    imagen: str


class ImagenRegistro(BaseModel):
    imagen: str
    nombre: str


@app.get("/")
def health():
    return {
        "estado": "Servidor biométrico activo",
        "usuarios_cargados": len(USUARIOS)
    }


@app.get("/usuarios_cargados")
def usuarios_cargados():
    return {
        "total": len(USUARIOS),
        "usuarios": USUARIOS
    }


@app.post("/recargar_usuarios")
def recargar_usuarios():
    cargar_usuarios()
    return {
        "resultado": "recargado",
        "total": len(USUARIOS)
    }


@app.post("/validar")
async def validar(data: ImagenValidacion):

    if not USUARIOS:
        cargar_usuarios()

    try:
        base64_img = data.imagen.split(",")[1]
        img_bytes = base64.b64decode(base64_img)
        frame = cv2.imdecode(np.frombuffer(img_bytes, np.uint8), cv2.IMREAD_COLOR)

        if frame is None:
            return {
                "resultado": "denegado",
                "mensaje": "No se pudo leer la imagen enviada"
            }

    except Exception as e:
        print("[ERROR] Error procesando imagen recibida:", e)
        return {
            "resultado": "denegado",
            "mensaje": "Imagen inválida"
        }

    for usuario in USUARIOS:
        try:
            result = DeepFace.verify(
                img1_path=frame,
                img2_path=usuario["ruta"],
                model_name="VGG-Face",
                enforce_detection=False
            )

            print(f"[INFO] Comparando con {usuario['nombre']}: {result}")

            if result.get("verified"):
                return {
                    "resultado": "acceso",
                    "persona": usuario["nombre"],
                    "distancia": float(result["distance"])
                }

        except Exception as e:
            print(f"[ERROR] Comparando con {usuario['nombre']}:", e)

    return {
        "resultado": "denegado"
    }


@app.post("/guardar_rostro")
def guardar_rostro(data: ImagenRegistro):

    try:
        base64_img = data.imagen.split(",")[1]
        img_bytes = base64.b64decode(base64_img)
        np_arr = np.frombuffer(img_bytes, np.uint8)
        img = cv2.imdecode(np_arr, cv2.IMREAD_COLOR)

        if img is None:
            return {
                "resultado": "error",
                "mensaje": "No se pudo procesar la imagen"
            }

        nombre_limpio = data.nombre.strip().replace(" ", "_")
        ruta = os.path.join(CARPETA_USUARIOS, f"{nombre_limpio}.jpg")

        cv2.imwrite(ruta, img)

        # Recargar usuarios después de guardar
        cargar_usuarios()

        return {
            "resultado": "guardado",
            "archivo": ruta,
            "usuario": nombre_limpio,
            "total_usuarios_cargados": len(USUARIOS)
        }

    except Exception as e:
        print("[ERROR] Guardando rostro:", e)

        return {
            "resultado": "error",
            "mensaje": str(e)
        }


if __name__ == "__main__":
    import uvicorn

    host = os.getenv("PYTHON_HOST", "127.0.0.1")
    port = int(os.getenv("PYTHON_PORT", "8000"))

    uvicorn.run(app, host=host, port=port)
