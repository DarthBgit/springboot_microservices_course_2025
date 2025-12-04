# --- Stage 1: Build ---
# We use an image with Maven and Java 21 to compile the application
FROM maven:3.9.6-eclipse-temurin-21 AS builder

# Set the working directory inside the container
WORKDIR /app

# Copy only the pom.xml to leverage Docker's cache
# If dependencies haven't changed, they won't be downloaded again
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the rest of the source code
COPY src ./src

# Compile the application and package the JAR, skipping tests
RUN mvn package -DskipTests

# --- Stage 2: Runtime ---
# We use a lightweight image with only the Java Runtime Environment (JRE)
# THIS LINE HAS BEEN CORRECTED
FROM eclipse-temurin:21-jre

# Set the working directory
WORKDIR /app

# Copy the compiled JAR from the 'builder' stage to the final image
COPY --from=builder /app/target/*.jar app.jar

# Expose the port on which the application will run
EXPOSE 8081

# Command to run the application when the container starts
ENTRYPOINT ["java", "-jar", "app.jar"]
