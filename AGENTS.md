# Gym Buddy — Project Agent Guide

## 1. Mission & Scope
- You are working inside `gym-buddy`, a mono-repo with Spring Boot backend and Angular frontend.
- Always apply TDD (red → green → refactor) and the hexagonal architecture defaults from `/Users/colberry/.config/opencode/AGENTS.md`.
- Prefer behavior tests, keep domain free of framework dependencies, and respect the page/UI split on the frontend.

## 2. Key Versions (stay current)
- Java 25 (runtime & compiler target per `backend/pom.xml`)
- Spring Boot 4.0.x parent (web/data/validation starters)
- Angular 21.1.x (`frontend/angular.json`, `ng version`)
- Node.js 22.15.x & Bun 1.1.x (package manager: `packageManager": "bun@1.1.30"`)
- Tailwind CSS 4.1.x + PostCSS 8.5.x
- RxJS 7.8.x, TypeScript 5.9.x, Vitest 4.0.x, Playwright 1.58.x
- Maven 3.9+ (required for Java 25 toolchain)

## 3. Repository Layout & Boundaries
- `backend/` — Spring Boot hexagonal app: `domain/`, `application/port|service|dto`, `adapter/inbound|outbound`, resources, tests.
- `frontend/` — Angular workspace with strict `core/`, `modules/<feature>/{pages,ui,data-access,state,models,util}`, `shared/` for cross-cutting UI/utilities, e2e, Vitest config, Tailwind config.
- Root: Dockerfile, docker-compose, Playwright config, CI workflows. Do not move these without updating docs.

## 4. Backend Workflow
- Tooling: Maven + Java 25. Commands run from `backend/`.
  - Run app: `mvn spring-boot:run`
  - Unit/integration tests + ArchUnit: `mvn test`
- Architecture rules:
  - Domain ring has zero Spring/Lombok annotations; value objects and invariants live here.
  - Application ring exposes `*UseCase` inbound ports, orchestrates transactions, depends only on domain.
  - Adapters implement `*Port` interfaces (`adapter/inbound/controller`, `adapter/outbound/persistence`). Map DTOs explicitly; never return JPA entities outside adapters.
- Persistence:
  - Default DB: H2 in-memory via `application.yml`. Keep SQL schema/data in `src/main/resources`.
  - Use MapStruct (1.6.x) for mapping when helpful; configure processors through Maven as already defined.

## 5. Frontend Workflow
- Package manager: Bun. Run commands from `frontend/`.
  - Dev server: `bun run start` (Angular serve on :4200).
  - Build: `bun run build` (ng build output to `dist/`).
  - Unit tests: `bun run test` (Vitest via `ng test`).
  - E2E: `bun run test:e2e` (Playwright per repo README).
- Mandatory Angular MCP workflow:
  1. Run Angular MCP `list_projects` to identify workspaces before modifying Angular code.
  2. Fetch version-specific best practices with `angular-mcp_get_best_practices` (pass workspace path) and follow them.
  3. For conceptual guidance, query `angular-mcp_search_documentation` with version 21.
- Architecture expectations:
  - `core/` handles bootstrap, routing, interceptors, global providers ONLY.
  - `modules/<feature>/pages` orchestrate data flow; `ui` components are dumb and accept inputs (signals preferred) + emit outputs.
  - `data-access` performs HTTP calls, `state` hosts signal stores/effects, `models` store types, `util` keeps pure helpers.
  - `shared/` houses cross-feature UI primitives, pipes, directives. Never import `modules/` code from `shared/` or `core/`.
- Styling: Tailwind CSS v4. Keep themes tokenized in `tailwind.config` and avoid adding global CSS unless unavoidable.

## 6. Testing & Quality Gates
- **Backend**
  - Unit tests for domain + use-cases (JUnit 5).
  - Adapter/integration tests (Spring Boot slices as needed).
  - Architecture tests via ArchUnit (already a dependency).
- **Frontend**
  - Vitest component/state tests. Favor signal behavior specs.
  - Playwright E2E flows: dashboard, user CRUD, workouts, navigation.
  - Use dependency-cruiser (if configured) to enforce module boundaries.
- **Global**
  - Follow TDD cadence; do not write production code without a failing test.
  - Run linters (`eslint`) and formatters via existing configs before committing.
  - Ensure CI workflows continue to pass (`.github/workflows/`).

## 7. Definition of Done (DoD)
- Tests cover the new/changed behavior (unit + integration/E2E as appropriate) and are deterministic.
- Hexagonal constraints hold: no forbidden imports between domain/application/adapters.
- Frontend respects core/modules/shared rules, uses signals-first data flow, and keeps UI components presentational.
- Tailwind utilities or shared tokens cover styling; avoid ad-hoc CSS.
- Lint, unit tests, and relevant Playwright specs are green locally.
- Documentation (README, this agents file) updated when behavior, commands, or architecture decisions change.
- No secrets or env values are committed; git status clean aside from intended changes.

## 8. Collaboration Checklist
- Branch naming: `feature/<summary>` or `fix/<summary>`.
- Write meaningful commit messages describing **why** the change exists.
- When touching both backend and frontend, keep commits grouped per logical change; mention cross-cutting impacts in PR description.
- Communicate architectural deviations early; propose new ports/adapters before implementation when possible.
- If introducing new tooling/dependencies, add to this file under Key Versions.

## 9. Quick Reference Commands
```
# Backend
cd backend
mvn spring-boot:run
mvn test

# Frontend
cd frontend
bun install       # first-time setup
bun run start
bun run build
bun run test
bun run test:e2e
```

Keep this `agents.md` current; future contributors rely on it as the single source for repo-specific expectations.
