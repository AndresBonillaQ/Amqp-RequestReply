# ----------------------------------------------------------------------------------
# Dockerfile
# ----------------------------------------------------------------------------------
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY target/*.jar app.jar

ENV SPRING_RABBITMQ_HOST=rabbitmq-host
ENV SPRING_RABBITMQ_PORT=5672
ENV SPRING_RABBITMQ_USERNAME=guest
ENV SPRING_RABBITMQ_PASSWORD=guest

ENTRYPOINT ["java", "-jar", "app.jar"]

# Instructions
# 1. Create the jar: mvn clean package -DskipTests
# 2. Build the image: docker build -t subscriber:latest .
# 3. Run the container: docker run subscriber:latest
