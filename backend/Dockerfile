# --- Build stage ---
FROM gradle:8.5-jdk17 AS build
WORKDIR /app

# Copy Gradle wrapper and build files from repo root (multi-module project)
COPY gradlew gradlew.bat settings.gradle gradle.properties ./
COPY gradle ./gradle
COPY backend/build.gradle ./backend/build.gradle
COPY backend/src ./backend/src

RUN ./gradlew :backend:bootJar --no-daemon -x test

# --- Runtime stage ---
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN apk add --no-cache curl \
    && addgroup -S spring && adduser -S spring -G spring

RUN find /app/backend/build/libs -name "*.jar" ! -name "*-plain.jar" \
    | xargs -I{} cp {} app.jar

RUN chown spring:spring app.jar
USER spring

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
