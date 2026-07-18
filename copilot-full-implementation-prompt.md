# Pawsulin - Full Implementation Prompt

## Project Overview
Pawsulin is a comprehensive full-stack web application for tracking glucose and insulin levels in pets with diabetes. The system provides real-time monitoring, historical tracking, analytics, and reports to help pet owners and veterinarians manage pet diabetes effectively.

## Core Implementation Requirements

### System Architecture
- **Backend**: Spring Boot microservice providing REST APIs
- **Frontend**: React SPA with TypeScript for type safety
- **Database**: PostgreSQL with Flyway for schema versioning
- **Deployment**: Docker containerization for consistent environments
- **Build**: Gradle for backend and npm for frontend

### Key Features to Implement

#### 1. Pet Management
- Create, read, update, delete (CRUD) operations for pet profiles
- Store pet information: name, species, breed, age, weight, diabetes type
- Track multiple pets per user
- Medical history and notes per pet
- Photo/avatar support

#### 2. Glucose Tracking
- Log glucose readings with timestamp and value (mg/dL)
- Batch entry support for multiple readings
- Glucose level categorization (Low, Normal, High, Critical)
- Historical data visualization (charts, graphs)
- Trend analysis and alerts for abnormal readings

#### 3. Insulin Administration
- Log insulin injections with type, amount, time
- Track insulin batches and expiration dates
- Insulin schedule management
- Correlation analysis between insulin and glucose levels
- Reminders for scheduled injections

#### 4. Analytics & Reports
- Dashboard with key metrics (average glucose, injection frequency)
- Time-period analysis (daily, weekly, monthly)
- Comparative analytics across pets
- Export reports as PDF/CSV
- Trend predictions and alerts

#### 5. User Management
- User registration and authentication
- Role-based access (Pet Owner, Veterinarian, Admin)
- Multi-user support for shared pet care
- Session management and security

### Backend Implementation Details

#### Technology Stack
- **Spring Boot 3.x** with latest Spring ecosystem
- **Spring Data JPA** for database operations
- **Spring Security** with JWT authentication
- **Spring Validation** for input validation
- **Springdoc OpenAPI** for automatic API documentation

#### Package Structure
```
src/main/java/com/pawsulin/
├── controller/       # REST API endpoints
├── service/          # Business logic layer
├── repository/       # Data access layer
├── entity/          # JPA entities
├── dto/             # Data transfer objects
├── exception/       # Custom exceptions
├── config/          # Spring configuration
├── security/        # Security configuration
├── mapper/          # Entity-to-DTO mappers
└── util/            # Utility classes
```

#### API Endpoints Design

**Pet Management**
- `GET /api/v1/pets` - List all user's pets
- `POST /api/v1/pets` - Create new pet
- `GET /api/v1/pets/{id}` - Get pet details
- `PUT /api/v1/pets/{id}` - Update pet
- `DELETE /api/v1/pets/{id}` - Delete pet

**Glucose Tracking**
- `GET /api/v1/pets/{petId}/glucose` - Get glucose readings
- `POST /api/v1/pets/{petId}/glucose` - Log glucose reading
- `GET /api/v1/pets/{petId}/glucose/analytics` - Get glucose analytics
- `DELETE /api/v1/pets/{petId}/glucose/{id}` - Delete glucose entry

**Insulin Tracking**
- `GET /api/v1/pets/{petId}/insulin` - Get insulin logs
- `POST /api/v1/pets/{petId}/insulin` - Log insulin injection
- `GET /api/v1/pets/{petId}/insulin/schedule` - Get injection schedule
- `DELETE /api/v1/pets/{petId}/insulin/{id}` - Delete insulin entry

**User Management**
- `POST /api/v1/auth/register` - User registration
- `POST /api/v1/auth/login` - User login
- `POST /api/v1/auth/logout` - User logout
- `GET /api/v1/users/profile` - Get user profile
- `PUT /api/v1/users/profile` - Update user profile

#### Database Schema (Flyway Migrations)
- **users** - User accounts and credentials
- **pets** - Pet profiles
- **glucose_readings** - Glucose tracking entries
- **insulin_logs** - Insulin administration records
- **glucose_alerts** - Alert configurations
- **user_roles** - Role definitions
- **user_pet_access** - Access control for shared pets

#### Testing Strategy
- **Unit Tests**: JUnit 5 for service and repository layer testing
- **Integration Tests**: Cucumber for BDD-style testing
- **Test Coverage**: Aim for 80%+ coverage on business logic
- **Test Data**: Use TestContainers for PostgreSQL isolation

### Frontend Implementation Details

#### Technology Stack
- **React 18.x** with hooks
- **TypeScript 5.x** with strict mode
- **React Router v6** for navigation
- **TanStack Query** for server state management
- **Zustand/Context API** for client state
- **Axios** for API communication
- **Chart.js** or **Recharts** for visualizations
- **Tailwind CSS** for styling
- **Jest & React Testing Library** for testing

#### Component Structure
```
src/
├── components/
│   ├── common/          # Reusable UI components
│   ├── layout/          # Layout wrapper components
│   ├── pet/             # Pet-related features
│   ├── glucose/         # Glucose tracking features
│   ├── insulin/         # Insulin tracking features
│   └── analytics/       # Analytics and reports
├── pages/               # Route pages
├── services/            # API client services
├── hooks/               # Custom React hooks
├── context/             # React Context
├── types/               # TypeScript type definitions
├── utils/               # Helper functions
└── styles/              # Global styles
```

#### Key Features Implementation
- **Responsive Design**: Mobile-first approach, works on all devices
- **Real-time Updates**: WebSocket support for live notifications
- **Offline Support**: Service Worker for offline access
- **Accessibility**: WCAG 2.1 AA compliance
- **Performance**: Code splitting, lazy loading, optimization

#### State Management
- **Server State**: TanStack Query for API data caching
- **Client State**: Context API or Zustand for UI state
- **Form State**: React Hook Form with validation
- **Authentication**: JWT stored securely, automatic refresh

#### Error Handling
- Global error boundary component
- API error handling with retry logic
- User-friendly error messages
- Logging and monitoring integration

### Database Design

#### Key Entities
- **User**: Authentication and profile information
- **Pet**: Pet profile with medical details
- **GlucoseReading**: Individual glucose measurements
- **InsulinLog**: Insulin administration records
- **AlertConfiguration**: User-defined alert thresholds
- **MedicalHistory**: Pet medical notes and events

#### Data Integrity
- Foreign key constraints
- Proper indexing on frequently queried columns
- Audit fields (created_at, updated_at, created_by)
- Soft delete support where applicable

### Docker & Deployment

#### Docker Compose Structure
```yaml
services:
  postgres:
    - Image: postgres:15-alpine
    - Port: 5432
    - Volumes: data persistence
  backend:
    - Build: ./backend
    - Port: 8080
    - Depends on: postgres
  frontend:
    - Build: ./frontend
    - Port: 3000
    - Depends on: backend
  nginx:
    - Image: nginx:alpine
    - Port: 80, 443
    - Routes requests to backend/frontend
```

#### Environment Configuration
- Development: `.env.dev` with local settings
- Production: `.env.prod` with secure settings
- Database connection strings
- API endpoints
- JWT secret and expiry
- Feature flags

### Security Implementation

#### Authentication & Authorization
- JWT-based authentication
- Role-based access control (RBAC)
- Secure password hashing (BCrypt)
- CSRF protection
- CORS configuration

#### Data Protection
- HTTPS/TLS enforcement
- Sensitive data encryption
- Input validation and sanitization
- SQL injection prevention
- API rate limiting

#### Compliance
- GDPR compliance for user data
- HIPAA considerations for health data
- Audit logging for sensitive operations
- Data retention policies

### Performance Optimization

#### Backend
- Database query optimization
- Caching strategy (Redis if needed)
- Pagination for list endpoints
- Lazy loading for related entities
- Connection pooling

#### Frontend
- Code splitting by route
- Image optimization
- Bundle size optimization
- Lazy loading of heavy components
- Service Worker caching

### Quality Assurance

#### Testing Coverage
- Backend: Unit tests (70%), Integration tests (Cucumber)
- Frontend: Component tests, E2E tests (Cypress/Playwright)
- API: OpenAPI specification and testing

#### Code Quality
- SonarQube integration for code analysis
- ESLint and Prettier for JavaScript
- Checkstyle and SpotBugs for Java
- Pre-commit hooks for code standards

### Development Workflow

#### Git Workflow
- Branch naming: `feature/*`, `bugfix/*`, `hotfix/*`
- Commit messages: Conventional Commits format
- PR reviews required before merge to develop
- Automated tests on PR submission

#### CI/CD Pipeline
- GitHub Actions for automation
- Automated testing on push
- Build and push Docker images
- Staging deployment on PR merge
- Production deployment on main branch

### Monitoring & Logging

#### Backend
- Structured logging with SLF4J/Logback
- Distributed tracing with Spring Cloud Sleuth
- Metrics collection with Micrometer
- Health checks and actuator endpoints

#### Frontend
- Console logging and error tracking
- User session tracking
- Performance monitoring
- Analytics integration

### Documentation

#### Required Documentation
- API documentation (OpenAPI/Swagger)
- Architecture Decision Records (ADRs)
- Database schema documentation
- Deployment guide
- Development setup guide
- Contributing guidelines

### Implementation Phases

#### Phase 1: Foundation (Sprint 1-2)
- Project setup and scaffolding
- User authentication and authorization
- Pet management CRUD
- Basic database schema

#### Phase 2: Core Features (Sprint 3-5)
- Glucose tracking implementation
- Insulin administration logging
- API endpoints for all features
- Frontend pages and components

#### Phase 3: Analytics & Reporting (Sprint 6-7)
- Dashboard implementation
- Analytics calculation and visualization
- Report generation (PDF/CSV)
- Trend analysis features

#### Phase 4: Polish & Optimization (Sprint 8-9)
- Performance optimization
- UI/UX refinement
- Comprehensive testing
- Documentation completion

#### Phase 5: Deployment & Monitoring (Sprint 10)
- Production deployment setup
- Monitoring and alerting
- User feedback implementation
- Production hardening

### Success Criteria

- All REST APIs functional and documented
- 80%+ test coverage on critical paths
- Responsive design working on all devices
- Zero security vulnerabilities
- Page load time < 2 seconds
- API response time < 200ms for 95th percentile
- Support for 10,000+ concurrent users
- 99.9% uptime SLA

### Tools & Technologies Summary

| Layer | Technology |
|-------|-----------|
| Backend Language | Java 17+ |
| Backend Framework | Spring Boot 3.x |
| Frontend Language | TypeScript 5.x |
| Frontend Framework | React 18.x |
| Database | PostgreSQL 15+ |
| Migration Tool | Flyway |
| Build Tool (Backend) | Gradle 8.x |
| Build Tool (Frontend) | npm/yarn |
| Containerization | Docker & Docker Compose |
| API Documentation | OpenAPI 3.0 / Swagger |
| Testing Backend | JUnit 5, Cucumber |
| Testing Frontend | Jest, React Testing Library |
| CI/CD | GitHub Actions |
| Code Analysis | SonarQube, ESLint |

---

## Implementation Notes

This prompt provides a comprehensive guide for implementing the Pawsulin pet diabetes tracking system. Follow this structure to maintain consistency, scalability, and quality throughout the development process. Adapt as needed based on team preferences and project evolution.
