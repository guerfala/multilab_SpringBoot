# Utiliser l'image de base OpenJDK
FROM openjdk:11-jre-slim

# Définir le répertoire de travail dans le conteneur
WORKDIR /app

# Copier le fichier JAR généré dans l'image Docker
COPY target/multilab-0.0.1-SNAPSHOT.jar multilab.jar

# Exposer le port sur lequel l'application écoutera (par défaut Spring Boot utilise le port 8080)
EXPOSE 8080

# Lancer l'application Spring Boot
ENTRYPOINT ["java", "-jar", "multilab.jar"]