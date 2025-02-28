# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven wrapper and pom.xml first (for caching dependencies)
COPY mvnw pom.xml ./
COPY .mvn .mvn

# Copy the full project source code
COPY src ./src

# Give execute permissions to Maven wrapper
RUN chmod +x mvnw

# Build the Spring Boot application inside the container
RUN ./mvnw clean package

# Copy the generated JAR file to the final container image
COPY target/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
