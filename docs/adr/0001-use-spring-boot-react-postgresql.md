# ADR 0001: Use Spring Boot, React, and PostgreSQL

## Status

Accepted

## Context

Pawsulin needs a maintainable full-stack architecture for authenticated CRUD workflows, analytics, and real-time notifications for pet diabetes management.

## Decision

Use:

- Spring Boot for the backend API
- React with Vite for the frontend
- PostgreSQL as the primary relational datastore

## Consequences

- Backend and frontend can evolve independently while sharing a stable HTTP API.
- PostgreSQL supports transactional domain data and works cleanly with JPA and Flyway.
- The stack is well-supported for containerized local development and deployment.
