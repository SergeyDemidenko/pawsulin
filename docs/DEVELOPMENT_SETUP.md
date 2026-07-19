# Development Setup Guide

## Prerequisites

- Java 17
- Node.js 20+
- npm
- Docker and Docker Compose

## Clone and install

```bash
git clone <repository-url>
cd pawsulin
```

### Backend

The backend is a Spring Boot application built with Gradle from the repository root.

```bash
./gradlew test
./gradlew :backend:bootRun
```

The backend starts on `http://localhost:8080`.

### Frontend

Install frontend dependencies before linting or building:

```bash
cd frontend
npm ci
npm run lint
npm run build
npm run dev
```

The frontend dev server starts on `http://localhost:5173`.

## Local configuration

### Environment variables

For local development, especially when running the application with the `prod` profile or using Docker Compose, you need to set several environment variables. 

Convenience scripts are provided to set default values:
- **Windows**: `call set-env.bat`
- **Linux/macOS**: `source ./set-env.sh`

### Seeding data

For testing purposes, you can populate the database with a default user, pet, and historical insulin data:

- **Windows**: `.\seed-data.bat`
- **Linux/macOS**: `./seed-data.sh`

This will create:
- User: `sd@s.d` / `12345678`
- Pet: `pupochka`
- 5 days of realistic insulin injection logs and aligned glucose readings.

### Backend

The default active profile is `dev`. Local configuration is defined in:

- `/backend/src/main/resources/application.properties`
- `/backend/src/main/resources/application-dev.properties`

Important local settings:

- PostgreSQL database: `jdbc:postgresql://localhost:5432/pawsulin_db`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

### Frontend

Use `/frontend/.env.example` as the starting point for local environment variables.

Available variables:

- `VITE_API_BASE_URL` - defaults to `http://localhost:8080/api`
- `VITE_WS_BASE_URL` - defaults to `ws://localhost:8080`
- `VITE_GOOGLE_CLIENT_ID` - Google OAuth client ID used by the Google sign-in button
- `GOOGLE_CLIENT_ID` - backend audience check for Google ID tokens; reuse the same client ID as the frontend

## Running with Docker Compose

From the repository root:

```bash
export DB_PASSWORD=change-me
export JWT_SECRET=replace-with-a-long-random-secret
export GOOGLE_CLIENT_ID=your-google-oauth-client-id
export VITE_GOOGLE_CLIENT_ID=$GOOGLE_CLIENT_ID
docker compose up --build
```

Services:

- Frontend: `http://localhost`
- Backend: `http://localhost:8080`
- PostgreSQL: `localhost:5432`

## Recommended developer workflow

1. Start PostgreSQL with Docker Compose or a local instance.
2. Run `./gradlew test` from the repository root before backend changes.
3. Run `npm ci && npm run lint && npm run build` in `/frontend` before frontend changes.
4. Use Swagger UI to inspect and test API contracts during development.
