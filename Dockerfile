# =========================================================
#  Stage 1: build — compila el JAR con Maven y JDK 21
# =========================================================
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

# Cache de dependencias: copiamos solo el pom primero
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

# Ahora sí, copiamos el código y empaquetamos
COPY src ./src
RUN mvn -B -q clean package -DskipTests

# =========================================================
#  Stage 2: runtime — sólo JRE 21, imagen final liviana
# =========================================================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# JAR name from pom.xml: artifactId=api, version=0.0.1-SNAPSHOT
COPY --from=build /workspace/target/api-0.0.1-SNAPSHOT.jar app.jar

# Render asigna el puerto en la variable PORT en runtime;
# EXPOSE es informativo, no fija el puerto real.
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
