# ═══════════════════════════════════════════════════════════
# MultiLab — Dockerfile (multi-stage build)
# ═══════════════════════════════════════════════════════════

# --- Stage 1 : Build ---
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Copier Maven wrapper + pom.xml d'abord (cache des dépendances)
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Copier le code source et builder
COPY src src
RUN ./mvnw clean package -DskipTests

# --- Stage 2 : Run ---
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copier le jar buildé
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
