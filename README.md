# Gym Buddy 💪

A full-stack fitness tracking application built with **Spring Boot 3.3** (backend) and **Angular 19** (frontend). Track your workouts, exercises, and progress all in one place.

---

## 📋 Table of Contents

- [Project Overview](#project-overview)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Development](#development)
- [Testing](#testing)
- [Deployment](#deployment)
- [Architecture](#architecture)

---

## Project Overview

Gym Buddy is a monorepo application that combines:
- **Backend**: Spring Boot REST API with hexagonal architecture
- **Frontend**: Angular 19 SPA with signals-based state management
- **Database**: H2 in-memory (development), easily swappable for production DB
- **Containerization**: Multi-stage Docker build for optimized deployment
- **CI/CD**: GitHub Actions for automated testing and Docker image builds

### Key Features

✅ User management (create, read, update, delete)  
✅ Workout tracking with exercises  
✅ Real-time statistics dashboard  
✅ Responsive UI with Tailwind CSS  
✅ Full-stack E2E tests with Playwright  
✅ Clean hexagonal architecture  
✅ TDD-driven development  

---

## Tech Stack

### Backend

| Technology | Version | Purpose |
|-----------|---------|---------|
| Java | 25 | Runtime environment |
| Spring Boot | 3.3.x | Web framework |
| Spring Data JPA | Latest | ORM & persistence |
| H2 Database | Latest | In-memory database |
| Lombok | Latest | Boilerplate reduction |
| JUnit 5 | Latest | Unit testing |
| Mockito | Latest | Mocking framework |
| ArchUnit | 1.2.1 | Architecture testing |

### Frontend

| Technology | Version | Purpose |
|-----------|---------|---------|
| Angular | 19 | Web framework |
| TypeScript | 5.9+ | Language |
| Tailwind CSS | 4.x | Styling |
| Vitest | 4.x | Unit testing |
| Playwright | 1.5x | E2E testing |
| RxJS | 7.x | Reactive streams |
| Bun | Latest | Package manager |

### DevOps

| Technology | Purpose |
|-----------|---------|
| Docker | Containerization |
| GitHub Actions | CI/CD pipeline |
| Maven | Java build tool |

---

## Project Structure

```
gym-buddy/
├── backend/                          # Spring Boot application
│   ├── src/main/java/com/gymbuddy/
│   │   ├── domain/                   # Domain layer (no dependencies)
│   │   │   ├── entity/               # Domain entities
│   │   │   ├── exception/            # Domain exceptions
│   │   │   └── value/                # Value objects
│   │   ├── application/              # Application layer
│   │   │   ├── port/                 # Port interfaces
│   │   │   ├── service/              # Use-case services
│   │   │   └── dto/                  # Data transfer objects
│   │   ├── adapter/                  # Adapter layer
│   │   │   ├── inbound/controller/   # REST controllers
│   │   │   ├── outbound/persistence/ # JPA repositories
│   │   │   └── config/               # Spring config
│   │   └── GymBuddyApplication.java
│   ├── src/main/resources/
│   │   ├── application.yml           # Spring configuration
│   │   ├── schema.sql                # H2 schema
│   │   └── data.sql                  # Sample data
│   ├── src/test/java/                # Unit & integration tests
│   └── pom.xml                       # Maven dependencies
│
├── frontend/                         # Angular application
│   ├── src/
│   │   ├── app/
│   │   │   ├── pages/                # Page components
│   │   │   │   ├── dashboard/
│   │   │   │   ├── users/
│   │   │   │   └── workouts/
│   │   │   ├── ui/                   # Reusable UI components
│   │   │   ├── services/             # HTTP & business logic
│   │   │   ├── models/               # TypeScript interfaces
│   │   │   ├── state/                # Signals state management
│   │   │   └── app.routes.ts         # Routing config
│   │   ├── main.ts                   # Entry point
│   │   └── index.html
│   ├── e2e/                          # Playwright E2E tests
│   ├── vitest.config.ts              # Test configuration
│   ├── tailwind.config.js            # Tailwind config
│   ├── eslint.config.js              # ESLint config
│   └── package.json
│
├── .github/workflows/                # CI/CD pipelines
│   ├── test.yml                      # Backend & frontend tests
│   ├── docker.yml                    # Docker build & push
│   └── e2e.yml                       # E2E tests
│
├── Dockerfile                        # Multi-stage build
├── .dockerignore
├── docker-compose.yml                # Local dev setup (optional)
└── README.md
```

---

## Getting Started

### Prerequisites

- **Java 25** (for backend)
- **Node.js 22+** or **Bun** (for frontend)
- **Docker** (for containerization)
- **Git**

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/gym-buddy.git
   cd gym-buddy
   ```

2. **Backend Setup**
   ```bash
   cd backend
   mvn clean install
   ```

3. **Frontend Setup**
   ```bash
   cd frontend
   bun install
   ```

---

## Development

### Running the Backend

```bash
cd backend
mvn spring-boot:run
```

Backend runs on `http://localhost:8080`
- API endpoints: `http://localhost:8080/api/v1`
- Health check: `http://localhost:8080/api/v1/health`
- H2 Console: `http://localhost:8080/h2-console`

### Running the Frontend

```bash
cd frontend
bun run start
```

Frontend dev server runs on `http://localhost:4200` with hot reload.

### Development Workflow

1. Backend changes are auto-reloaded with Spring Boot DevTools
2. Frontend changes trigger hot reload in dev server
3. Both run independently during development
4. For production, Angular builds are embedded in Spring Boot JAR

---

## Testing

### Backend Tests (JUnit 5 + Mockito)

```bash
cd backend
mvn test                  # Run all tests
mvn test -Dtest=UserTest # Run specific test
```

**Test Coverage**
- Domain entities & business logic
- Application services & use-cases
- Controller integration tests
- Architecture validation (ArchUnit)

### Frontend Tests (Vitest)

```bash
cd frontend
bun run test             # Watch mode
bun run test:ci          # CI mode (single run)
```

**Test Coverage**
- Component unit tests
- Service integration tests
- Pipe & directive tests
- ~80% code coverage goal

### E2E Tests (Playwright)

```bash
cd frontend
bun run test:e2e         # Run all E2E tests
bun run test:e2e --ui    # Interactive mode
```

**Test Scenarios**
- Dashboard page & statistics
- User CRUD operations
- Workout creation & management
- Navigation & routing
- Form validation & error handling

---

## Deployment

### Building Docker Image

```bash
docker build -t gym-buddy:latest .
```

### Running Docker Container

```bash
docker run -p 8080:8080 gym-buddy:latest
```

Access the app at `http://localhost:8080/ui/`

### CI/CD with GitHub Actions

Three main workflows:

1. **test.yml** - Runs on every push & PR
   - Builds backend & frontend
   - Runs all tests
   - Lints code
   - Generates coverage reports

2. **docker.yml** - Triggered by successful tests
   - Builds multi-stage Docker image
   - Pushes to GitHub Container Registry
   - Caches layers for faster rebuilds

3. **e2e.yml** - Full-stack E2E tests
   - Starts Docker container
   - Runs Playwright tests
   - Uploads test reports

---

## Architecture

### Hexagonal Architecture (Backend)

```
┌─────────────────────────────────────┐
│      REST Controllers (Inbound)     │
├─────────────────────────────────────┤
│   Application / Use-Case Services   │
├─────────────────────────────────────┤
│  Domain (Entities, Value Objects)   │
├─────────────────────────────────────┤
│  Repositories & Adapters (Outbound) │
└─────────────────────────────────────┘
```

**Principles**
- Domain layer has zero framework dependencies
- Application layer orchestrates use-cases
- Adapters handle framework integration
- Ports define contracts (UserPort, WorkoutPort, etc.)
- DTO mapping at boundaries

### Frontend Architecture (Page/UI Pattern)

```
Dashboard Page (Route Level)
├── UserList UI Component (Reusable)
├── WorkoutForm UI Component (Reusable)
└── ApiService (HTTP Client)

Pages: Dashboard, Users, Workouts
UI: Buttons, Forms, Cards (Tailwind)
State: Signals (computed, effects)
Services: API communication
```

**Principles**
- Page components manage data & orchestration
- UI components are pure & reusable
- Signals for local & shared state
- RxJS for complex async operations
- One-way data flow (down), events flow up

---

## API Endpoints

### Users

| Method | Endpoint | Purpose |
|--------|----------|---------|
| GET | `/api/v1/users` | List all users |
| GET | `/api/v1/users/{id}` | Get user by ID |
| POST | `/api/v1/users` | Create user |
| PUT | `/api/v1/users/{id}` | Update user |
| DELETE | `/api/v1/users/{id}` | Delete user |

### Workouts

| Method | Endpoint | Purpose |
|--------|----------|---------|
| GET | `/api/v1/workouts` | List all workouts |
| GET | `/api/v1/workouts/{id}` | Get workout by ID |
| GET | `/api/v1/users/{userId}/workouts` | List user's workouts |
| POST | `/api/v1/workouts` | Create workout |
| PUT | `/api/v1/workouts/{id}` | Update workout |
| DELETE | `/api/v1/workouts/{id}` | Delete workout |

### Health

| Method | Endpoint | Purpose |
|--------|----------|---------|
| GET | `/api/v1/health` | Application health check |

---

## Configuration

### Backend (application.yml)

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: create-drop
    database-platform: org.hibernate.dialect.H2Dialect
  h2:
    console:
      enabled: true
      path: /h2-console

server:
  port: 8080
```

### Frontend (angular.json)

- Strict TypeScript mode enabled
- Tailwind CSS utility-first styling
- Vitest for unit testing
- Standalone components
- Tree-shaking for optimal bundles

---

## Contributing

1. Create a feature branch: `git checkout -b feature/new-feature`
2. Develop following TDD practices
3. Run tests: `mvn test` (backend), `bun test` (frontend)
4. Commit with clear messages
5. Push and create a Pull Request

---

## Future Enhancements

- [ ] User authentication & JWT
- [ ] Exercise library & templates
- [ ] Progress tracking & charts
- [ ] Social features & friend system
- [ ] Mobile app (React Native)
- [ ] PostgreSQL support
- [ ] Kubernetes deployment
- [ ] OpenAPI/Swagger documentation

---

## License

MIT License - see LICENSE file for details

---

## Support

For issues, questions, or suggestions, please open a GitHub issue.

**Happy training! 🏋️**
