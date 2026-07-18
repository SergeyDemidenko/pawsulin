# Pawsulin Implementation Execution Plan

**Generated:** 2026-07-18  
**Branch:** first-ai  
**Based on:** copilot-full-implementation-prompt.md

## Current Project State Summary

### ✅ Completed (Phase 1-2)
- **Phase 1 Foundation**: Project scaffold, Spring Boot setup, all entities, all repositories, database schema (Flyway)
- **Phase 2 Auth**: JWT security, authentication endpoints, user management with role-based access
- **Phase 3 Pet Management**: Full CRUD with authorization, soft delete, pagination, comprehensive tests
- **Phase 4 Glucose Tracking**: Full CRUD with analytics, auto-categorization, date-range queries, comprehensive tests

### ⚠️ Partially Complete
- **Glucose Controller/Service**: Logic complete, but user ID extraction hardcoded to `1L` (needs real JWT integration)

### ❌ Not Started
- **Insulin Management**: Entity/repo/DB exist; missing `InsulinService` and `InsulinController`
- **Glucose Alerts**: Entity/repo/DB exist; missing `GlucoseAlertService` and `GlucoseAlertController`
- **User Pet Access**: Entity/repo/DB exist; missing service and controller for shared pet management
- **Request DTOs**: Missing `CreateInsulinLogRequest` and `UpdateInsulinLogRequest`
- **Real User ID Extraction**: Controllers hardcoded to return `1L`
- **Frontend**: Entire React/TypeScript application
- **Docker**: No docker-compose.yml, Dockerfile, or nginx config
- **Documentation**: No setup, architecture, or deployment guides
- **CI/CD**: No GitHub Actions workflows

## Implementation Plan

### Phase: Backend Completion (Critical Path)

#### **Step 1: Fix User ID Extraction in Controllers** (Highest Priority)
**Why:** Required for all subsequent features to work properly with authentication  
**Tasks:**
- Create utility method to extract userId from Spring Security context
- Update `PetController.extractUserIdFromAuthentication()`
- Update `GlucoseController.extractUserIdFromAuthentication()`
- Verify JWT filter sets authentication correctly
- Add unit tests

**Files to Modify:**
- `backend/src/main/java/com/pawsulin/controller/PetController.java`
- `backend/src/main/java/com/pawsulin/controller/GlucoseController.java`
- `backend/src/main/java/com/pawsulin/util/SecurityUtil.java` (create new)
- `backend/src/test/java/com/pawsulin/controller/test/PetControllerTest.java`
- `backend/src/test/java/com/pawsulin/controller/test/GlucoseControllerTest.java`

---

#### **Step 2: Implement Insulin Management (Service & Controller)**
**Why:** Critical feature for the application; follows same patterns as glucose  
**Tasks:**
- Create `CreateInsulinLogRequest` and `UpdateInsulinLogRequest` DTOs
- Create `InsulinService` with full CRUD (create, read, update, delete, pagination, date-range queries)
- Create `InsulinController` with REST endpoints
- Add authorization checks (user-pet ownership validation)
- Add unit and integration tests

**Endpoints to Implement:**
- `POST /api/v1/pets/{petId}/insulin` - Create insulin log
- `GET /api/v1/pets/{petId}/insulin/{logId}` - Get specific insulin log
- `GET /api/v1/pets/{petId}/insulin` - List paginated insulin logs
- `GET /api/v1/pets/{petId}/insulin/range` - Get logs by date range
- `PUT /api/v1/pets/{petId}/insulin/{logId}` - Update insulin log
- `DELETE /api/v1/pets/{petId}/insulin/{logId}` - Delete insulin log

**Files to Create/Modify:**
- `backend/src/main/java/com/pawsulin/dto/CreateInsulinLogRequest.java` (new)
- `backend/src/main/java/com/pawsulin/dto/UpdateInsulinLogRequest.java` (new)
- `backend/src/main/java/com/pawsulin/service/InsulinService.java` (new)
- `backend/src/main/java/com/pawsulin/controller/InsulinController.java` (new)
- `backend/src/test/java/com/pawsulin/service/test/InsulinServiceTest.java` (new)
- `backend/src/test/java/com/pawsulin/controller/test/InsulinControllerTest.java` (new)

---

#### **Step 3: Implement Glucose Alert Management**
**Why:** Essential for monitoring and user notifications  
**Tasks:**
- Create `CreateGlucoseAlertRequest` and `UpdateGlucoseAlertRequest` DTOs
- Create `GlucoseAlertService` with CRUD operations
- Create `GlucoseAlertController` with REST endpoints
- Implement alert configuration and threshold management
- Add authorization and tests

**Endpoints to Implement:**
- `POST /api/v1/pets/{petId}/alerts` - Create alert
- `GET /api/v1/pets/{petId}/alerts` - List alerts
- `GET /api/v1/pets/{petId}/alerts/{alertId}` - Get specific alert
- `PUT /api/v1/pets/{petId}/alerts/{alertId}` - Update alert
- `DELETE /api/v1/pets/{petId}/alerts/{alertId}` - Delete alert
- `GET /api/v1/pets/{petId}/alerts/status` - Get alert status

**Files to Create/Modify:**
- `backend/src/main/java/com/pawsulin/dto/CreateGlucoseAlertRequest.java` (new)
- `backend/src/main/java/com/pawsulin/dto/UpdateGlucoseAlertRequest.java` (new)
- `backend/src/main/java/com/pawsulin/dto/GlucoseAlertDTO.java` (update existing)
- `backend/src/main/java/com/pawsulin/service/GlucoseAlertService.java` (new)
- `backend/src/main/java/com/pawsulin/controller/GlucoseAlertController.java` (new)
- `backend/src/test/java/com/pawsulin/service/test/GlucoseAlertServiceTest.java` (new)
- `backend/src/test/java/com/pawsulin/controller/test/GlucoseAlertControllerTest.java` (new)

---

#### **Step 4: Implement User Pet Access Management (Shared Pet Access)**
**Why:** Multi-user support for shared pet care; improves usability  
**Tasks:**
- Create DTOs for access management requests
- Create `UserPetAccessService` for managing sharing permissions
- Create `UserPetAccessController` for API endpoints
- Implement role-based access levels (OWNER, EDITOR, VIEWER)
- Update existing services to check access level
- Add tests

**Endpoints to Implement:**
- `POST /api/v1/pets/{petId}/access` - Grant access to another user
- `GET /api/v1/pets/{petId}/access` - List users with access to pet
- `PUT /api/v1/pets/{petId}/access/{userId}` - Update access level
- `DELETE /api/v1/pets/{petId}/access/{userId}` - Revoke access

**Files to Create/Modify:**
- `backend/src/main/java/com/pawsulin/dto/UserPetAccessRequest.java` (new)
- `backend/src/main/java/com/pawsulin/dto/UserPetAccessDTO.java` (new)
- `backend/src/main/java/com/pawsulin/service/UserPetAccessService.java` (new)
- `backend/src/main/java/com/pawsulin/controller/UserPetAccessController.java` (new)
- `backend/src/test/java/com/pawsulin/service/test/UserPetAccessServiceTest.java` (new)
- `backend/src/test/java/com/pawsulin/controller/test/UserPetAccessControllerTest.java` (new)

---

#### **Step 5: Create Mapper Classes (MapStruct Integration)**
**Why:** Cleaner separation of concerns; build already includes MapStruct  
**Tasks:**
- Create mapper interfaces for all DTOs (Pet, Glucose, Insulin, Alert, UserPetAccess, User)
- Replace inline mapping in services with mapper method calls
- Update services to use injected mappers
- Update tests to mock mappers

**Files to Create:**
- `backend/src/main/java/com/pawsulin/mapper/PetMapper.java` (new)
- `backend/src/main/java/com/pawsulin/mapper/GlucoseReadingMapper.java` (new)
- `backend/src/main/java/com/pawsulin/mapper/InsulinLogMapper.java` (new)
- `backend/src/main/java/com/pawsulin/mapper/GlucoseAlertMapper.java` (new)
- `backend/src/main/java/com/pawsulin/mapper/UserPetAccessMapper.java` (new)
- `backend/src/main/java/com/pawsulin/mapper/UserMapper.java` (new)

**Files to Modify:**
- All service classes to use mappers

---

#### **Step 6: Add Utility Classes**
**Why:** Code reusability, better organization  
**Tasks:**
- Create validation utilities
- Create date/time utilities for range queries
- Create pagination utilities
- Create response builder utilities

**Files to Create:**
- `backend/src/main/java/com/pawsulin/util/SecurityUtil.java` (create if not done in Step 1)
- `backend/src/main/java/com/pawsulin/util/DateTimeUtil.java` (new)
- `backend/src/main/java/com/pawsulin/util/ValidationUtil.java` (new)
- `backend/src/main/java/com/pawsulin/util/PaginationUtil.java` (new)

---

#### **Step 7: Implement Cucumber Integration Tests** (Optional but Recommended)
**Why:** BDD-style tests for feature validation  
**Tasks:**
- Create feature files: `pet.feature`, `glucose.feature`, `insulin.feature`, `auth.feature`
- Implement step definitions
- Add cucumber-java and cucumber-spring dependencies
- Create test runner class

---

## Frontend Phase (Following Backend)

### **Step 8: Bootstrap React/TypeScript Frontend**
- Create React 18 app with TypeScript
- Setup React Router, TanStack Query, Zustand
- Configure Axios with API interceptor
- Setup Tailwind CSS
- Create folder structure

### **Step 9: Implement Authentication UI**
- Login/Register pages
- JWT token management
- Protected routes
- Session management

### **Step 10: Implement Pet Management UI**
- Pet CRUD forms
- Pet list with pagination
- Pet detail page

### **Step 11: Implement Glucose Tracking UI**
- Glucose entry form
- Glucose history table
- Glucose charts (Recharts)
- Analytics dashboard

### **Step 12: Implement Insulin Tracking UI**
- Insulin entry form
- Insulin history
- Injection schedule

### **Step 13: Implement Alerts & Notifications UI**
- Alert configuration
- Real-time notifications (WebSocket)
- Alert history

---

## Deployment Phase (Final)

### **Step 14: Docker Setup**
- Create Dockerfile for backend
- Create Dockerfile for frontend
- Create docker-compose.yml
- Create nginx.conf

### **Step 15: GitHub Actions CI/CD**
- Create test workflow
- Create build workflow
- Create deploy workflow

### **Step 16: Documentation**
- API documentation (OpenAPI/Swagger)
- Architecture Decision Records (ADRs)
- Deployment guide
- Development setup guide

---

## Implementation Order Summary

| Order | Step | Priority | Est. Effort |
|-------|------|----------|-------------|
| 1 | Fix User ID Extraction | 🔴 CRITICAL | 1h |
| 2 | Insulin Management | 🔴 CRITICAL | 4h |
| 3 | Glucose Alerts | 🟠 HIGH | 4h |
| 4 | User Pet Access | 🟠 HIGH | 3h |
| 5 | MapStruct Mappers | 🟡 MEDIUM | 2h |
| 6 | Utility Classes | 🟡 MEDIUM | 1h |
| 7 | Cucumber Tests | 🟢 LOW | 3h |
| 8-13 | Frontend Implementation | 🔴 CRITICAL | 20h |
| 14 | Docker Setup | 🟠 HIGH | 2h |
| 15 | GitHub Actions | 🟠 HIGH | 2h |
| 16 | Documentation | 🟡 MEDIUM | 3h |

**Total Estimated Effort:** ~45 hours

---

## Success Criteria for This Execution

- [ ] All backend endpoints implemented (Pet, Glucose, Insulin, Alerts, UserPetAccess, Auth)
- [ ] All endpoints properly extract and validate user ID from JWT
- [ ] Unit test coverage ≥ 70% on business logic
- [ ] Integration tests (Cucumber) for happy paths
- [ ] Frontend fully functional with all features
- [ ] Docker setup working (local development)
- [ ] GitHub Actions CI/CD pipeline operational
- [ ] API documented (Swagger/OpenAPI)
- [ ] README with setup instructions
- [ ] Zero security vulnerabilities

---

## Notes

- Follow Spring Boot and React best practices throughout
- Maintain consistent error handling
- Use the provided prompt as the source of truth for requirements
- Test after each step (unit + integration tests)
- Commit and push changes after completing each major step
- Use conventional commit messages (feat:, fix:, test:, docs:)
