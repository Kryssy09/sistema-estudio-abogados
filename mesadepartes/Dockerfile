# ============================================================
# Sistema Mesa de Partes - Dockerfile
# ============================================================

# ─── Etapa 1: Construcción ───────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build

WORKDIR /app

# Copiar pom.xml primero para aprovechar la caché de capas de Docker
# (las dependencias solo se re-descargan si pom.xml cambia)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar el código fuente y compilar
COPY src ./src
RUN mvn clean package -DskipTests -B

# ─── Etapa 2: Imagen de ejecución (más ligera) ───────────────
FROM eclipse-temurin:21-jre-alpine

# Crear un usuario no-root por seguridad
RUN addgroup -S mesapartes && adduser -S mesapartes -G mesapartes

WORKDIR /app

# Copiar el JAR generado desde la etapa de construcción
# El finalName en pom.xml es ${project.artifactId} → mesadepartes.jar
COPY --from=build /app/target/mesadepartes.jar app.jar

# Crear directorio para archivos subidos y asignar permisos
RUN mkdir -p /root/mesadepartes/uploads && \
    chown -R mesapartes:mesapartes /app /root/mesadepartes

# Cambiar al usuario no-root
USER mesapartes

# Puerto de la aplicación (server.port=8081 en application.properties)
EXPOSE 8081

# Opciones de JVM optimizadas para contenedores
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseContainerSupport"

# Ejecutar la aplicación
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
