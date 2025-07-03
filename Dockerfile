FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Install Maven
RUN apk add --no-cache maven

COPY . .

# Build your app using the system Maven
RUN mvn clean package -DskipTests

EXPOSE 8080

CMD ["java", "-jar", "target/user-service-0.0.1-SNAPSHOT.jar"]
