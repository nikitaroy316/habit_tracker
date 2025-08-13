# Use the official Java 17 image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy Maven build artifact into image
COPY target/habittracker-0.0.1-SNAPSHOT.jar app.jar

# Expose port (optional, for clarity)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
