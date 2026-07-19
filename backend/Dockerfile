# --- Build stage ---
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# Copy gradle wrapper and project-wide config
COPY gradlew .
COPY gradle/ gradle/
COPY build.gradle .
COPY settings.gradle .
COPY gradle.properties .

# Copy backend specific config
COPY backend/build.gradle backend/

# Copy backend source
COPY backend/src backend/src

# Build backend
RUN chmod +x gradlew && ./gradlew :backend:bootJar --no-daemon

# --- Runtime stage ---
FROM eclipse-temurin:17-jre
WORKDIR /app

RUN apt-get update \
    && apt-get install -y curl \
    && rm -rf /var/lib/apt/lists/* \
    && groupadd --system spring \
    && useradd --system --gid spring spring

COPY --from=build /app/backend/build/libs/backend.jar /app/app.jar

RUN chown spring:spring /app/app.jar
USER spring

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
