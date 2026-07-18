# Pawsulin Project Structure

## Overview
Pawsulin is a full-stack application for tracking glucose and insulin levels in pets with diabetes. The project is organized into backend (Java/Spring) and frontend (React/TypeScript) components, containerized with Docker for consistent local development.

## Directory Structure

```
pawsulin/
├── backend/                          # Spring Boot backend application
│   ├── src/
│   │   ├── main/java/
│   │   │   └── com/pawsulin/
│   │   │       ├── controller/      # REST API controllers
│   │   │       ├── service/         # Business logic services
│   │   │       ├── repository/      # Data access layer (Spring Data JPA)
│   │   │       ├── entity/          # JPA entities
│   │   │       ├── dto/             # Data transfer objects
│   │   │       ├── exception/       # Custom exceptions
│   │   │       ├── config/          # Spring configuration classes
│   │   │       ├── util/            # Utility classes
│   │   │       └── PawsulinApplication.java  # Main Spring Boot entry point
│   │   ├── main/resources/
│   │   │   ├── application.properties       # Application configuration
│   │   │   ├── application-dev.properties   # Development profile
│   │   │   ├── application-prod.properties  # Production profile
│   │   │   └── db/migration/        # Flyway database migrations
│   │   │       └── V1__Initial_schema.sql
│   │   └── test/
│   │       ├── java/
│   │       │   └── com/pawsulin/
│   │       │       ├── controller/
│   │       │       ├── service/
│   │       │       └── repository/
│   │       └── resources/
│   │           ├── features/         # Cucumber feature files
│   │           │   ├── pet.feature
│   │           │   ├── glucose.feature
│   │           │   └── insulin.feature
│   │           └── application-test.properties
│   ├── build.gradle                 # Gradle build configuration
│   ├── settings.gradle              # Gradle settings
│   └── Dockerfile                   # Docker image definition
│
├── frontend/                        # React + TypeScript frontend application
│   ├── public/
│   │   ├── index.html
│   │   └── favicon.ico
│   ├── src/
│   │   ├── components/              # Reusable React components
│   │   │   ├── common/              # Common UI components (Button, Card, etc.)
│   │   │   ├── layout/              # Layout components (Header, Sidebar, etc.)
│   │   │   ├── pet/                 # Pet-related components
│   │   │   │   ├── PetList.tsx
│   │   │   │   ├── PetForm.tsx
│   │   │   │   └── PetDetail.tsx
│   │   │   ├── glucose/             # Glucose tracking components
│   │   │   │   ├── GlucoseLog.tsx
│   │   │   │   ├── GlucoseChart.tsx
│   │   │   │   └── GlucoseForm.tsx
│   │   │   └── insulin/             # Insulin tracking components
│   │   │       ├── InsulinLog.tsx
│   │   │       ├── InsulinChart.tsx
│   │   │       └── InsulinForm.tsx
│   │   ├── pages/                   # Page-level components (routes)
│   │   │   ├── HomePage.tsx
│   │   │   ├── DashboardPage.tsx
│   │   │   ├── PetsPage.tsx
│   │   │   ├── GlucoseTrackerPage.tsx
│   │   │   └── InsulinTrackerPage.tsx
│   │   ├── services/                # API client services
│   │   │   ├── apiClient.ts         # Base API client configuration
│   │   │   ├── petService.ts
│   │   │   ├── glucoseService.ts
│   │   │   └── insulinService.ts
│   │   ├── hooks/                   # Custom React hooks
│   │   │   ├── usePets.ts
│   │   │   ├── useGlucoseData.ts
│   │   │   └── useInsulinData.ts
│   │   ├── context/                 # React Context for state management
│   │   │   └── AppContext.tsx
│   │   ├── types/                   # TypeScript type definitions
│   │   │   ├── Pet.ts
│   │   │   ├── GlucoseEntry.ts
│   │   │   └── InsulinEntry.ts
│   │   ├── styles/                  # Global and shared styles
│   │   │   └── App.css
│   │   ├── utils/                   # Utility functions
│   │   │   ├── dateUtils.ts
│   │   │   ├── formatters.ts
│   │   │   └── validators.ts
│   │   ├── App.tsx                  # Main App component
│   │   ├── index.tsx                # React entry point
│   │   └── setupTests.ts            # Test configuration
│   ├── package.json
│   ├── tsconfig.json                # TypeScript configuration
│   ├── .env.example                 # Example environment variables
│   ├── .env.local                   # Local environment variables (git ignored)
│   ├── Dockerfile                   # Docker image definition
│   └── .dockerignore
│
├── docker/                          # Docker configuration
│   ├── docker-compose.yml           # Local development environment
│   ├── docker-compose.prod.yml      # Production environment (if applicable)
│   └── nginx.conf                   # Nginx configuration (if using reverse proxy)
│
├── docs/                            # Project documentation
│   ├── API.md                       # API documentation
│   ├── SETUP.md                     # Setup and installation guide
│   ├── ARCHITECTURE.md              # System architecture overview
│   ├── DATABASE.md                  # Database schema and design
│   └── DEPLOYMENT.md                # Deployment instructions
│
├── .github/
│   ├── copilot-prompts.md           # Copilot configuration and coding standards
│   └── workflows/                   # GitHub Actions CI/CD workflows
│       ├── backend-tests.yml
│       ├── frontend-tests.yml
│       └── build-deploy.yml
│
├── .gitignore
├── README.md                        # Project overview and quick start
├── docker-compose.yml               # Root level compose file (for development)
├── gradle.properties                # Gradle properties
├── settings.gradle                  # Root Gradle settings
└── build.gradle                     # Root build file (if using multi-project build)
```

## Key Components

### Backend (Java/Spring)
- **Controllers**: REST API endpoints for pets, glucose, and insulin tracking
- **Services**: Business logic for data processing and calculations
- **Repositories**: Database access layer using Spring Data JPA
- **Entities**: JPA mapped classes for Pet, GlucoseEntry, InsulinEntry
- **DTOs**: Data transfer objects for request/response payloads
- **Migrations**: Flyway SQL scripts for schema versioning

### Frontend (React/TypeScript)
- **Components**: Modular, reusable UI components organized by feature
- **Pages**: Route-based page components
- **Services**: API client for backend communication
- **Hooks**: Custom React hooks for data fetching and state management
- **Types**: TypeScript definitions for type safety
- **Utils**: Helper functions for dates, formatting, and validation

### Database
- **PostgreSQL**: Primary data store
- **Flyway Migrations**: Version-controlled schema changes
- **Migration Location**: `backend/src/main/resources/db/migration/`

### Docker
- **Multi-container setup**: Backend, Frontend, PostgreSQL, optionally Redis/Nginx
- **docker-compose.yml**: Local development environment
- **Dockerfile**: Individual service definitions for containerization

## Development Guidelines

### Adding a New Feature
1. Create feature branch: `git checkout -b feature/my-feature`
2. Backend implementation:
   - Create entity in `entity/`
   - Create repository in `repository/`
   - Create service in `service/`
   - Create controller in `controller/`
   - Add Flyway migration for schema changes
   - Write unit tests and Cucumber scenarios
3. Frontend implementation:
   - Create types in `types/`
   - Create service in `services/`
   - Create components in appropriate feature folder
   - Write tests for components
4. Test integration: Run all tests locally
5. Commit and push to develop branch

### Database Migrations
- All schema changes go through Flyway
- Naming: `V{version}__{description}.sql`
- Example: `V2__Add_pet_medical_history_table.sql`
- Migrations are automatically applied on application startup

### Running Locally
```bash
# Start all services
docker-compose up

# Backend: http://localhost:8080
# Frontend: http://localhost:3000
# Database: PostgreSQL on localhost:5432
```

## Branch Strategy
- **main**: Production-ready code
- **develop**: Development and integration branch
- **feature/**: Feature development branches
- **bugfix/**: Bug fix branches
- **hotfix/**: Production hotfixes
