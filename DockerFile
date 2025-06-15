# Use an official lightweight OpenJDK 17 image
FROM eclipse-temurin:17-jdk-alpine

# Set working directory inside the container
WORKDIR /app

# Copy your app source code
COPY . .

# Build your app (skip tests for faster build)
RUN ./mvnw clean package -DskipTests

# Expose port (change if your app runs on a different port)
EXPOSE 8080

# Run the jar
CMD ["java", "-jar", "target/user-service-0.0.1-SNAPSHOT.jar"]
