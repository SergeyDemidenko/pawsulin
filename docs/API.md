# API Documentation

Pawsulin exposes a versioned REST API under `/api/v1` and publishes an OpenAPI 3 specification through Springdoc.

## OpenAPI and Swagger

- OpenAPI JSON: `/v3/api-docs`
- Swagger UI: `/swagger-ui.html`
- Authentication: JWT bearer token for all protected endpoints

The Swagger UI is enabled by default and documents request/response schemas generated from the backend controllers and DTOs.

## Main API Areas

### Authentication

- `POST /api/v1/auth/register`
- `POST /api/v1/auth/login`
- `GET /api/v1/auth/profile`
- `PUT /api/v1/auth/profile`
- `POST /api/v1/auth/logout`

### Pets

- `POST /api/v1/pets`
- `GET /api/v1/pets/{petId}`
- `GET /api/v1/pets`
- `GET /api/v1/pets/all`
- `PUT /api/v1/pets/{petId}`
- `DELETE /api/v1/pets/{petId}`

### Glucose readings

- `POST /api/v1/pets/{petId}/glucose`
- `GET /api/v1/pets/{petId}/glucose/{readingId}`
- `GET /api/v1/pets/{petId}/glucose`
- `GET /api/v1/pets/{petId}/glucose/range`
- `GET /api/v1/pets/{petId}/glucose/analytics`
- `PUT /api/v1/pets/{petId}/glucose/{readingId}`
- `DELETE /api/v1/pets/{petId}/glucose/{readingId}`

### Insulin logs

- `POST /api/v1/pets/{petId}/insulin`
- `GET /api/v1/pets/{petId}/insulin/{logId}`
- `GET /api/v1/pets/{petId}/insulin`
- `GET /api/v1/pets/{petId}/insulin/range`
- `PUT /api/v1/pets/{petId}/insulin/{logId}`
- `DELETE /api/v1/pets/{petId}/insulin/{logId}`

### Glucose alerts

- `POST /api/v1/pets/{petId}/alerts`
- `GET /api/v1/pets/{petId}/alerts/{alertId}`
- `GET /api/v1/pets/{petId}/alerts`
- `GET /api/v1/alerts`
- `PUT /api/v1/pets/{petId}/alerts/{alertId}`
- `DELETE /api/v1/pets/{petId}/alerts/{alertId}`

### Shared pet access

- `POST /api/v1/pets/{petId}/access`
- `GET /api/v1/pets/{petId}/access`
- `GET /api/v1/pets/{petId}/access/{accessId}`
- `PUT /api/v1/pets/{petId}/access/{accessId}`
- `DELETE /api/v1/pets/{petId}/access/{targetUserId}`
- `GET /api/v1/user/pets/access`

## Real-time notifications

The backend also exposes a WebSocket endpoint at `/ws/notifications`. Clients connect with a JWT token in the query string:

- `/ws/notifications?token=<jwt>`

This channel is used for glucose alert notifications.

## Authorization model

- Public routes: authentication endpoints, Swagger UI, OpenAPI docs, actuator health, and WebSocket handshake path
- Protected routes: all business endpoints require an authenticated user with `PET_OWNER` or `VETERINARIAN`

## Local usage

1. Start the backend application.
2. Open `http://localhost:8080/swagger-ui.html`.
3. Authenticate with `/api/v1/auth/login`.
4. Paste the returned bearer token into Swagger UI's **Authorize** dialog.
