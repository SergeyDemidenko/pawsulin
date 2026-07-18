# Copilot Prompts Settings

## Project Overview
**Pawsulin** - Glucose and Insulin tracker for pets with diabetes

## Technology Stack

### Backend
- **Language**: Java
- **Framework**: Spring (Spring Boot, Spring Data JPA, Spring Web)
- **Build Tool**: Gradle
- **Testing**: JUnit 5
- **Integration Testing**: Cucumber

### Frontend
- **Language**: TypeScript
- **Framework**: React
- **Build Tool**: Gradle (via Node.js integration)

### Data & Deployment
- **Database**: PostgreSQL
- **Database Versioning**: Flyway
- **Containerization**: Docker
- **Primary Deployment Target**: Docker containers for local development

## Coding Standards & Conventions

### Java/Spring Backend
- Follow Spring Boot best practices
- Use dependency injection and annotations
- Structure: Controllers → Services → Repositories
- Package naming: `com.pawsulin.{domain}.{layer}`
- Use Java 11+ features (records, var, etc. where appropriate)
- Implement proper exception handling and logging
- Write unit tests with JUnit 5
- Write integration tests with Cucumber for BDD scenarios

### TypeScript/React Frontend
- Use functional components with hooks
- Strict TypeScript configuration enabled
- Component naming: PascalCase for components
- File naming: camelCase for utilities, constants
- Use proper type definitions (no `any`)
- Implement error boundaries and loading states
- Follow React best practices for performance

### Database
- Use Flyway for schema versioning
- Naming convention: `V{version}__{description}.sql`
- All migrations must be idempotent
- Document schema changes in migration files

### Docker
- Multi-stage builds for optimization
- Use docker-compose for local development
- Document all environment variables
- Keep images minimal and secure

## Project Structure
See `PROJECT_STRUCTURE.md` for detailed directory organization.

## Development Workflow
1. Create feature branches from `develop`
2. Write tests (unit and integration) alongside implementation
3. Ensure all tests pass locally before pushing
4. Submit PR to `develop` for review
5. Merge to `main` for releases

## Key Directories
- `/backend` - Spring Boot application
- `/frontend` - React + TypeScript application
- `/docker` - Docker configuration files
- `/docs` - Project documentation
