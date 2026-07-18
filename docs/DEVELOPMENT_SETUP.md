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

## Running with Docker Compose

From the repository root:

```bash
export DB_PASSWORD=change-me
export JWT_SECRET=replace-with-a-long-random-secret
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
