# Use a lightweight JDK runtime as the base image
FROM openjdk:21-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the JAR file into the container
COPY build/libs/auth-service.jar /app/auth-service.jar

# Expose the application port
EXPOSE 8084

# Run the application with the prod profile
CMD ["java", "-jar", "auth-service.jar", "--spring.profiles.active=prod"]


