FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
# Copy the built JAR into the container (adjust path if needed)
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]