# ADR 0002: Use JWT for stateless authentication

## Status

Accepted

## Context

The application exposes a REST API and a WebSocket endpoint that both need authenticated access without relying on server-side HTTP session state.

## Decision

Use JWT bearer tokens for authentication and authorize protected endpoints through Spring Security.

## Consequences

- The API remains stateless and easier to scale horizontally.
- Frontend clients can reuse the same token for REST requests and WebSocket connection setup.
- Token issuance and rotation must be handled carefully in client and deployment configuration.
