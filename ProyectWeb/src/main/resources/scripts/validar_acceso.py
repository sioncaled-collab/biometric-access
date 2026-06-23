import cv2
import face_recognition
import numpy as np
import os
import sys

# === Configuración ===
DATA_DIR = os.path.join(os.path.dirname(__file__), "rostros")

# Cargar rostros conocidos
rostros_conocidos = []
nombres = []

for archivo in os.listdir(DATA_DIR):
    if archivo.endswith(".jpg") or archivo.endswith(".png"):
        imagen = face_recognition.load_image_file(os.path.join(DATA_DIR, archivo))
        encoding = face_recognition.face_encodings(imagen)
        if encoding:
            rostros_conocidos.append(encoding[0])
            nombres.append(os.path.splitext(archivo)[0])

# Iniciar cámara
video = cv2.VideoCapture(0)
if not video.isOpened():
    print("⚠️ No se pudo acceder a la cámara.")
    sys.exit(1)

print("📸 Escaneando rostro... Acércate a la cámara")

while True:
    ret, frame = video.read()
    if not ret:
        break

    # Redimensionar para acelerar procesamiento
    small_frame = cv2.resize(frame, (0, 0), fx=0.25, fy=0.25)
    rgb_small_frame = small_frame[:, :, ::-1]

    # Detección y comparación
    caras_en_frame = face_recognition.face_locations(rgb_small_frame)
    encodings_en_frame = face_recognition.face_encodings(rgb_small_frame, caras_en_frame)

    for encoding, ubicacion in zip(encodings_en_frame, caras_en_frame):
        coincidencias = face_recognition.compare_faces(rostros_conocidos, encoding)
        nombre = "Desconocido"
        color = (0, 0, 255)  # rojo (denegado)

        if True in coincidencias:
            idx = coincidencias.index(True)
            nombre = nombres[idx]
            color = (0, 255, 0)  # verde (autorizado)

        # Dibujar cuadro
        y1, x2, y2, x1 = [v * 4 for v in ubicacion]
        cv2.rectangle(frame, (x1, y1), (x2, y2), color, 2)
        cv2.putText(frame, nombre, (x1, y2 + 25), cv2.FONT_HERSHEY_SIMPLEX, 1, color, 2)

        if color == (0, 255, 0):
            print(f"✅ Acceso autorizado: {nombre}")
            video.release()
            cv2.destroyAllWindows()
            sys.exit(0)

    cv2.imshow("Validación de acceso", frame)

    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

print("❌ Acceso denegado.")
video.release()
cv2.destroyAllWindows()
sys.exit(1)
