# Сборка приложения
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY gradle gradle
COPY build.gradle settings.gradle gradlew ./
COPY src src
RUN chmod +x gradlew
RUN ./gradlew build -x test

# Финальный легковесный образ
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
