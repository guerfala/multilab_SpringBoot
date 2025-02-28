# Use the official MySQL 8 image
FROM mysql:8

# Set environment variables for MySQL
ENV MYSQL_ROOT_PASSWORD=multilab
ENV MYSQL_DATABASE=multilab
ENV MYSQL_USER=multilab
ENV MYSQL_PASSWORD=multilab

# Expose the MySQL port
EXPOSE 3306