# {{ProjectName}}

{{TBD: Short project description}}

## Tech Stack

- Backend: Java 25, Spring Boot 4, Spring Data JPA, H2, Flyway, MapStruct, ArchUnit, Maven
- Web: Angular 21, Tailwind CSS 4, Vitest, Playwright, Bun
- App: Ionic 8, Angular 21, Capacitor 8, native Android and iOS projects, Bun
- Contract: one OpenAPI specification generates the backend API and both Angular clients
- Tooling: Docker and GitHub Actions

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
│   │   └── db/migration/             # Flyway SQL
│   │       ├── current/              # Local development snapshot
│   │       └── incremental/          # Forward-only migrations
│   ├── src/test/java/                # Unit & integration tests
│   └── pom.xml                       # Maven dependencies
│
├── frontend/                         # Angular application
│   ├── src/
│   │   ├── app/
│   │   │   ├── core/api/             # Generated OpenAPI client
│   │   │   ├── pages/                # Route-level page components
│   │   │   ├── shared/               # Cross-feature UI and utilities
│   │   │   └── app.routes.ts         # Routing config
│   ├── e2e/                          # Playwright E2E tests
│   └── package.json
│
├── app/                              # Ionic Angular application
│   ├── src/app/
│   │   ├── core/api/                 # Generated OpenAPI client
│   │   ├── pages/users/              # Starter route and API example
│   │   └── shared/                   # Cross-feature UI and utilities
│   ├── android/                      # Capacitor Android project
│   ├── ios/                          # Capacitor iOS project
│   ├── capacitor.config.ts
│   └── package.json
│
├── .github/workflows/                # CI/CD pipelines
│   ├── test.yml                      # Backend & frontend tests
│   ├── docker.yml                    # Docker build & push
│   └── e2e.yml                       # E2E tests
│
├── Dockerfile                        # Multi-stage web build
├── .dockerignore                     # Ignored files for docker build
├── docker-compose.yml                # Local dev setup (optional)
└── README.md
```

## Commands
Backend (run from `backend/`):

- start: `mvn spring-boot:run`
- start with local snapshot: `mvn spring-boot:run -Dspring-boot.run.profiles=local`
- test: `mvn test`
- full verification: `mvn clean verify`
- regenerate API contracts: `mvn generate-sources`

## Database Migrations

Flyway runs on backend startup and validates the database before Hibernate validates the JPA
mapping. Hibernate DDL generation is disabled (`ddl-auto: validate`), so database structure must
come from Flyway.

Migration folders:
- `backend/src/main/resources/db/migration/incremental/`: production-style forward-only migrations.
  These are versioned `V...__description.sql` files and should not be edited after they have been
  committed or applied anywhere.
- `backend/src/main/resources/db/migration/current/`: local development snapshot. This folder is used
  only with the `local` Spring profile and contains a destructive repeatable migration that rebuilds
  the local H2 schema from the current desired state.

Recommended workflow:
1. During local schema design, update
   `backend/src/main/resources/db/migration/current/R__current_schema.sql`.
2. Start the backend with `mvn spring-boot:run -Dspring-boot.run.profiles=local` to rebuild and
   validate the local database.
3. When the schema change is ready, create an immutable migration:
   `backend/scripts/new-migration.sh add-user-status`.
4. Add the equivalent forward-only SQL to the generated file in `incremental/`.
5. Run `cd backend && mvn test`.

Web frontend (run from `frontend/`):

- install: `bun install`
- start: `bun run start`
- build: `bun run build`
- test: `bun run test -- --watch=false`
- lint: `bun run lint`
- architecture: `bun run depcruise`
- e2e: `bun run e2e`

Ionic app (run from `app/`):

- install: `bun install`
- browser development: `bun run start` (port 4200)
- build: `bun run build`
- test: `bun run test -- --watch=false`
- lint: `bun run lint`
- architecture: `bun run depcruise`
- copy the web build and native dependencies: `bun run sync`
- open Android Studio: `bun run android`
- open Xcode: `bun run ios`

Set the production API URL in `app/src/environments/environment.prod.ts` before shipping. The
development configuration calls `http://localhost:8080`; Android emulators normally reach the host
machine at `http://10.0.2.2:8080`, so adjust the development environment when testing there.
Override `app.cors.allowed-origins` in deployed backend configuration so it contains only the
deployed browser origins plus the Capacitor origins used by the native apps.

The generated native projects include placeholder icons and splash screens. Replace them before a
release; Capacitor's asset generator can regenerate both platforms with
`bunx @capacitor/assets generate` after source artwork is placed under `app/resources/`.

The Angular CLI persistent disk cache is disabled for the app because the current Angular 21 and
Ionic build combination can deadlock in the esbuild service. It can be re-enabled after upgrading
the toolchain and confirming repeatable builds.

## Creating a New Project from This Blueprint

Instead of GitHub's "Use this template" (which strips git history), use the init script to clone with full history and rename everything in one step:

```bash
./init-project.sh <kebab-case-name> <github-owner> --app-id <reverse-dns-id> [--target-dir <path>]

# Example:
./init-project.sh my-cool-app lombold \
  --app-id ch.lombold.mycoolapp \
  --target-dir ../my-cool-app
```

The script derives all project-name variants, replaces the Capacitor/Android/iOS app identifier,
renames files and directories (including Java and Android packages), creates a private GitHub
repository, and pushes it.

Prerequisites: `git`, authenticated `gh`, Java 25, and Maven.

For native development, also install Android Studio with an Android SDK. Building iOS requires
macOS and Xcode.

## Blueprint Sync

Projects created from this blueprint include a daily GitHub Actions workflow (`sync-blueprint.yml`) that checks for new blueprint commits, rebases onto them, and opens a PR. If conflicts occur, it opens an issue with resolution instructions instead.

To override the default blueprint source, set a repository variable:
```bash
gh variable set BLUEPRINT_REPO --body 'lombold/project-blueprint' --repo <owner>/<repo>
```
