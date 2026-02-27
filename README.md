# {{ProjectName}}

{{TBD: Short project description}}

## Tech Stack
- Backend: Java 25, Spring Boot 4.0, Spring Data JPA, H2, MapStruct, ArchUnit, Maven
- Frontend: Angular 21, TypeScript 5.9, Tailwind CSS 4.1, RxJS 7.8, Vitest 4, Playwright 1.58, Bun 1.1.30
- Tooling: Docker, GitHub Actions

## Project Structure
```
{{project-name}}/
├── backend/                          # Spring Boot application
│   ├── src/main/java/com/{{projectname}}/
│   │   ├── domain/                   # Domain layer (no dependencies)
│   │   │   ├── entity/               # Domain entities
│   │   │   ├── exception/            # Domain exceptions
│   │   │   └── value/                # Value objects
│   │   ├── application/              # Application layer
│   │   │   ├── port/                 # Port interfaces (inbound use-cases, outbound repositories, access, etc.), Requests, Responses, etc.
│   │   │   └── service/              # Use-case service implementations
│   │   ├── adapter/                  # Adapter layer
│   │   │   ├── inbound/controller/   # REST controllers, DTOs, etc.
│   │   │   ├── outbound/persistence/ # JPA repositories
│   │   │   └── config/               # Spring config
│   │   └── {{ProjectName}}Application.java
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
├── .dockerignore                     # Ignored files for docker build
├── docker-compose.yml                # Local dev setup (optional)
└── README.md
```

## Commands
Backend (run from `backend/`):
- start: `mvn spring-boot:run`
- test: `mvn test`
- lint: not configured

Frontend (run from `frontend/`):
- install: `bun install`
- start: `bun run start`
- build: `bun run build`
- test: `bun run test`
- lint: `bunx eslint .`
- e2e: `bunx playwright test`

## Creating a New Project from This Blueprint

Instead of GitHub's "Use this template" (which strips git history), use the init script to clone with full history and rename everything in one step:

```bash
./init-project.sh <kebab-case-name> <github-owner> [target-dir]

# Example:
./init-project.sh my-cool-app lombold
```

The script derives all case variants from the kebab-case name (`myCoolApp`, `MyCoolApp`, `mycoolapp`, `my-cool-app`), replaces them across all files, renames directories and files (including the Java package), creates a private GitHub repo, and pushes.

Prerequisites: `git`, `gh`.

## Blueprint Sync

Projects created from this blueprint include a daily GitHub Actions workflow (`sync-blueprint.yml`) that checks for new blueprint commits, rebases onto them, and opens a PR. If conflicts occur, it opens an issue with resolution instructions instead.

To override the default blueprint source, set a repository variable:
```bash
gh variable set BLUEPRINT_REPO --body 'lombold/project-blueprint' --repo <owner>/<repo>
```
