# ==========================
# ETAPA 1: COMPILAR SPRING
# ==========================
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

COPY . .

RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests


# ==========================
# ETAPA 2: EJECUTAR JAVA + PYTHON
# ==========================
FROM eclipse-temurin:21-jre

WORKDIR /app

# Instalar Python y librerías necesarias para OpenCV/FastAPI si las usas
RUN apt-get update && apt-get install -y \
    python3 \
    python3-venv \
    libgl1 \
    libglib2.0-0 \
    && rm -rf /var/lib/apt/lists/*

# Crear entorno virtual Python
RUN python3 -m venv /opt/venv
ENV PATH="/opt/venv/bin:$PATH"

# Copiar JAR compilado
COPY --from=build /app/target/*.jar /app/app.jar

# Copiar carpeta Python al contenedor
COPY --from=build /app/src/main/python /app/src/main/python

# Instalar dependencias Python si existe requirements.txt
RUN if [ -f /app/src/main/python/requirements.txt ]; then \
    pip install --no-cache-dir -r /app/src/main/python/requirements.txt; \
    fi

# Variables para que Java sepa dónde está Python
ENV PYTHON_EXECUTABLE=/opt/venv/bin/python
ENV PYTHON_WORKDIR=/app/src/main/python
ENV PYTHON_SCRIPT=main.py
ENV PYTHON_HOST=127.0.0.1
ENV PYTHON_PORT=8000

EXPOSE 8080

CMD ["java", "-jar", "/app/app.jar"]
