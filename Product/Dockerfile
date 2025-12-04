# --- Etapa 1: Build ---
# Usamos una imagen con Maven y Java 21 para compilar la aplicación
FROM maven:3.9.6-eclipse-temurin-21 AS builder

# Establecemos el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiamos solo el pom.xml para aprovechar la caché de Docker
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiamos el resto del código fuente
COPY src ./src

# Compilamos la aplicación y empaquetamos el JAR, saltando los tests
RUN mvn package -DskipTests

# --- Etapa 2: Runtime ---
# Usamos una imagen ligera solo con el entorno de ejecución de Java (JRE)
# ESTA LÍNEA HA SIDO CORREGIDA
FROM eclipse-temurin:21-jre

# Establecemos el directorio de trabajo
WORKDIR /app

# Copiamos el JAR compilado desde la etapa 'builder' a la imagen final
COPY --from=builder /app/target/*.jar app.jar

# Exponemos el puerto en el que correrá la aplicación
EXPOSE 8080

# Comando para ejecutar la aplicación cuando se inicie el contenedor
ENTRYPOINT ["java", "-jar", "app.jar"]
