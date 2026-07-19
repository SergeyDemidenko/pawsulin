# Deployment Guide

## Overview

Pawsulin is deployed as three runtime components:

- PostgreSQL database
- Spring Boot backend
- Nginx-served React frontend

The repository includes Dockerfiles for the backend and frontend plus a root `docker-compose.yml` for container orchestration.

## Required environment variables

### Database

- `DB_USERNAME`
- `DB_PASSWORD`

### Backend

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `JWT_SECRET`
- `CORS_ALLOWED_ORIGINS`
- `SPRING_PROFILES_ACTIVE=prod`

## Quick deployment

A convenience script is provided for a clean build and deployment:

```bash
chmod +x deploy.sh
./deploy.sh
```

This script will:
1. Stop the application if it's running.
2. Clean locally built docker images.
3. Perform a clean Gradle build of the backend.
4. Rebuild the Docker images.
5. Start all services in detached mode.

## Container deployment

1. Build the backend artifact from the repository root:

   ```bash
   ./gradlew :backend:bootJar
   ```

2. Build images from the repository root:

   ```bash
   docker compose build
   ```

3. Start services:

   ```bash
   docker compose up -d
   ```

4. Verify health:

   - Backend health: `http://<host>:8080/actuator/health`
   - Swagger UI: `http://<host>:8080/swagger-ui.html`
   - Frontend: `http://<host>/`

## Production profile behavior

The production profile:

- reads datasource settings from environment variables
- uses Flyway for schema migrations
- expects a JWT secret from the environment
- reads allowed CORS origins from the environment

Configuration is defined in `/backend/src/main/resources/application-prod.properties`.

## Database migrations

Flyway migrations are applied on backend startup from:

- `/backend/src/main/resources/db/migration`

Deployments should preserve the database volume and allow the backend to run migrations before accepting traffic.

## Reverse proxy behavior

The frontend container serves static assets with Nginx and proxies:

- `/api/` to the backend REST API
- `/ws/` to the backend WebSocket endpoint

This configuration is defined in `/frontend/nginx.conf`.

## Deployment checklist

1. Provide strong values for `DB_PASSWORD` and `JWT_SECRET`.
2. Set `CORS_ALLOWED_ORIGINS` to the real frontend origin.
3. Ensure PostgreSQL persistent storage is enabled.
4. Confirm `/actuator/health` reports `UP`.
5. Confirm Swagger UI and the frontend are reachable after deployment.
