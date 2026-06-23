from fastapi import FastAPI
from pydantic import BaseModel
import base64
import numpy as np
import cv2
import os
from deepface import DeepFace
from fastapi.middleware.cors import CORSMiddleware

app = FastAPI()

# ======== CORS (OBLIGATORIO) =========
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"], 
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# =========== Cargar varios rostros registrados ==============
USUARIOS = []

ruta_usuarios = "usuarios"   # Carpeta con imágenes permitidas

for archivo in os.listdir(ruta_usuarios):
    if archivo.lower().endswith((".jpg", ".jpeg", ".png")):
        path = f"{ruta_usuarios}/{archivo}"
        USUARIOS.append({
            "nombre": os.path.splitext(archivo)[0],
            "ruta": path
        })
        print(f"[OK] Usuario cargado: {archivo}")

class Imagen(BaseModel):
    imagen: str

# ============== Endpoint de validación ==================
@app.post("/validar")
async def validar(imagen: Imagen):

    # Convertir base64 -> OpenCV
    data = imagen.imagen.split(",")[1]
    img_bytes = base64.b64decode(data)
    frame = cv2.imdecode(np.frombuffer(img_bytes, np.uint8), cv2.IMREAD_COLOR)

    # Probar todos los usuarios cargados
    for usuario in USUARIOS:
        try:
            result = DeepFace.verify(
                img1_path=frame,
                img2_path=usuario["ruta"],
                model_name="VGG-Face",
                enforce_detection=False
            )

            if result["verified"]:
                return {
                    "resultado": "acceso",
                    "persona": usuario["nombre"],
                    "distancia": float(result["distance"])
                }

        except Exception as e:
            print(f"[Error comparando con {usuario['nombre']}]:", e)

    return {"resultado": "denegado"}

class Imagen(BaseModel):
    imagen: str
    nombre: str


@app.post("/guardar_rostro")
def guardar_rostro(data: Imagen):

    base64_img = data.imagen.split(",")[1]

    img_bytes = base64.b64decode(base64_img)

    np_arr = np.frombuffer(img_bytes, np.uint8)

    img = cv2.imdecode(np_arr, cv2.IMREAD_COLOR)

    carpeta = "usuarios"

    os.makedirs(carpeta, exist_ok=True)

    ruta = f"{carpeta}/{data.nombre}.jpg"

    cv2.imwrite(ruta, img)

    return {
        "resultado": "guardado",
        "archivo": ruta
    }
