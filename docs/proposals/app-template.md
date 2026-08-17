# Angular/Ionic/Capacitor App Template Proposal

- Status: Proposed
- Target: Project Blueprint
- Scope: Add an installable iOS and Android client alongside the existing web client

## Summary

Add a separate `app/` client built with Angular 21, Ionic Angular, and Capacitor. The app will
consume the same Spring Boot API and OpenAPI contract as the existing `frontend/`, while retaining
its own navigation, UI components, runtime configuration, tests, and native projects.

This proposal assumes that “composer” refers to **Capacitor**, Ionic's official native runtime. PHP
Composer is not part of the proposed stack.

## Goals

- Generate web and mobile API clients from the existing OpenAPI contract.
- Support browser-based app development plus native Android and iOS builds.
- Preserve the existing Angular 21 standalone, signals-first, and Bun conventions.
- Use official Ionic, Angular, and Capacitor commands to generate and initialize project artifacts.
- Keep web and mobile presentation layers independent.
- Make application name and native bundle identifier part of blueprint initialization.
- Add CI checks for the app without changing how the web frontend is packaged and deployed.
- Establish safe defaults for API URLs, CORS, native configuration, and secret handling.

## Non-goals

- Replacing the existing Angular web frontend with Ionic.
- Sharing web and mobile UI components.
- Implementing authentication, offline synchronization, push notifications, deep links, or store
  publishing in the first increment.
- Serving the native app from the Spring Boot JAR or Docker image.
- Introducing a root JavaScript workspace or a shared Angular library before reusable application
  logic exists.

## Proposed Repository Structure

```text
project-blueprint/
├── backend/                         # Spring Boot API and OpenAPI contract
├── frontend/                        # Existing Angular web application
├── app/                             # Ionic Angular application
│   ├── android/                     # Committed Capacitor Android project
│   ├── ios/                         # Committed Capacitor iOS project
│   ├── public/
│   ├── src/
│   │   ├── app/
│   │   │   ├── core/
│   │   │   │   ├── api/            # Generated; do not edit
│   │   │   │   ├── adapters/       # API and native-plugin adapters
│   │   │   │   └── services/       # App-wide services
│   │   │   ├── pages/              # Lazy route-level Ionic pages
│   │   │   └── shared/             # App-only reusable UI and utilities
│   │   └── environments/            # Build-time API endpoints
│   ├── angular.json
│   ├── capacitor.config.ts
│   ├── ionic.config.json
│   ├── package.json
│   └── bun.lockb
├── docs/proposals/
└── ...
```

The native `android/` and `ios/` directories are source, not disposable build output. They contain
platform settings, entitlements, manifests, icons, signing configuration references, and native
plugin integration and must be committed.

## Architecture Decisions

### Separate web and app clients

The existing `frontend/` remains the browser client. The new `app/` is a separate Angular
application because mobile navigation stacks, safe areas, gestures, lifecycle events, native
plugins, and store releases evolve independently from the web UI.

Both clients may implement the same use cases, but they will not import components from each other.
If meaningful framework-independent logic emerges, it can later move into a small package under
`packages/`. That extraction should follow demonstrated duplication rather than being part of the
initial scaffold.

### Angular and Ionic conventions

The app will use:

- Angular 21 with standalone components and `ChangeDetectionStrategy.OnPush`.
- Ionic Angular 8, using imports from `@ionic/angular/standalone`.
- Capacitor 8 for the iOS and Android runtimes.
- Angular signals, `computed()`, and signal forms for new state and forms.
- `rxResource` for HTTP-backed read models where it fits the request lifecycle.
- Lazy route components and a root `ion-router-outlet`.
- Ionic components for app UI; Tailwind is not required in the first increment.
- Bun for dependency installation and JavaScript scripts.

The exact compatible dependency versions will be resolved and locked when the scaffold is created.
Angular remains on the same major and minor line as `frontend/`.

### Command-driven scaffolding and initialization

The implementation must start from the official generators instead of manually reconstructing an
Ionic or native project. From the repository root, the initial sequence is:

```bash
# Confirm the available starter and flags before generating.
bunx @ionic/cli start --list
bunx @ionic/cli start --help

# Generate a standalone Angular app and initialize its Capacitor integration.
bunx @ionic/cli start app blank --type=angular --capacitor --no-deps --no-git

cd app
bun install

# Generate the native projects from the initialized Capacitor configuration.
bunx cap add android
bunx cap add ios

# Build the web assets and synchronize plugins/configuration into both projects.
bun run build
bunx cap sync
```

`ionic start --capacitor` is the initialization command; a second `cap init` must not be run over
the generated configuration. Before committing the scaffold, verify the generated Angular version
against `frontend/package.json` and use supported Angular update commands if alignment is required.
Do not hand-convert an outdated generated module application into a standalone application.

Subsequent framework artifacts must also begin with the relevant generator, for example:

```bash
cd app
bunx @ionic/cli generate page pages/users
bunx ng generate service core/services/example
bunx ng generate component shared/components/example
```

Generated output may then be adapted to the blueprint's inline-template, OnPush, signals-first,
testing, and dependency-boundary conventions. Configuration files and domain-specific code remain
normal deliberate edits; the command requirement applies to framework and native scaffolding.

### OpenAPI remains the source of truth

The backend's `src/main/resources/openapi.yml` remains the single API contract. Add a second
`typescript-angular` execution to `backend/pom.xml`:

```text
generate-backend-openapi  -> backend/target/generated-sources/openapi
generate-frontend-openapi -> frontend/src/app/core/api
generate-app-openapi      -> app/src/app/core/api
```

The app-generated client follows the existing rule: it is imported only through its generated
barrel and is never edited manually. Initially generating one client per consumer is preferred to
publishing a shared Angular package because it keeps both applications independently buildable and
keeps Maven as the only generation entry point.

CI will regenerate both clients and fail on an unexpected diff. This detects contract drift while
retaining the repository's current convention of committing generated frontend sources.

### API endpoint configuration

The web client can continue using a relative base URL and the Angular development proxy. A native
WebView cannot use that proxy, so the app must provide an absolute endpoint:

```typescript
provideApi(environment.apiBaseUrl)
```

Initial environments:

| Build | API base URL |
|---|---|
| Browser development | Configurable local or LAN backend URL |
| Native development | Configurable LAN/emulator backend URL |
| Production | Public HTTPS API URL |

No production URL should be hard-coded in a service. Angular environment replacement supplies the
value at build time. If deployments later need one binary for multiple environments, introduce a
small runtime configuration loader as a separate decision.

`capacitor.config.ts` must not contain a committed `server.url`. Live reload may inject a temporary
LAN URL, but production always packages the contents of `webDir`.

### Capacitor build output

Capacitor expects `index.html` at the root of its web asset directory. Configure the Angular
application builder so that the production build writes:

```text
app/www/index.html
```

and not:

```text
app/www/browser/index.html
```

The Capacitor configuration will use:

```typescript
const config: CapacitorConfig = {
  appId: 'com.example.projectname',
  appName: 'ProjectName',
  webDir: 'www',
};
```

The app uses `/` as its Angular base href. It does not inherit the web frontend's `/ui/` production
base href.

### Backend CORS

The backend currently enables Spring Security CORS support but does not define the blueprint's
allowed origins. Add a `CorsConfigurationSource` whose values come from application properties.

Default development origins should cover:

- `http://localhost:4200` for the web frontend.
- `http://localhost:8100` for Ionic browser development.
- `capacitor://localhost` for the iOS WebView.
- `https://localhost` for the Android WebView.

Any LAN live-reload origin must be supplied explicitly through a development-only environment
setting. Production configuration must contain only the deployed web origins and Capacitor origins.
Wildcard origins are not allowed when credentials are enabled.

The production app communicates with the backend over HTTPS. Clear-text traffic and Capacitor
navigation allowlists are permitted only in local development configuration.

### Native capability boundaries

Application code will not call Capacitor plugins directly from page components. Each native
capability receives an injectable adapter under `app/src/app/core/adapters/`. This provides one
place for permission handling, browser fallback behavior, error mapping, and unit-test doubles.

No secret, private API key, signing password, keystore, certificate, or provisioning profile is
stored in the repository. Public mobile configuration is not treated as a secret. When
authentication is introduced, the preferred design is authorization code with PKCE through the
system browser and platform-appropriate secure token storage.

## Blueprint Initialization

Extend `init-project.sh` with a required reverse-DNS application identifier while preserving the
existing project name substitutions:

```bash
./init-project.sh my-cool-app lombold ./my-cool-app ch.lombold.mycoolapp
```

Proposed arguments:

```text
init-project.sh <kebab-case-name> <github-owner> [target-dir] <app-id>
```

Before implementation, the parser should be converted to named options to avoid ambiguity between
the optional target directory and required app ID. The intended final interface is:

```bash
./init-project.sh my-cool-app lombold \
  --target-dir ./my-cool-app \
  --app-id ch.lombold.mycoolapp
```

Initialization must:

1. Validate the reverse-DNS identifier.
2. Replace the placeholder in `capacitor.config.ts`.
3. Update the Android application ID and namespace.
4. Update the iOS bundle identifier and display name.
5. Continue replacing the existing kebab, camel, Pascal, and lowercase project-name variants.
6. Print app development commands in the completion message.

Requiring an explicit app ID prevents generated projects from accidentally shipping with a shared
placeholder identifier.

## Developer Commands

The app will expose commands consistent with the existing frontend:

```bash
cd app
bun install
bun run start              # browser development on :8100
bun run build              # production web bundle in www/
bun run test -- --watch=false
bun run test:ci
bun run lint
bun run sync               # build, then capacitor sync
bun run android            # open Android Studio
bun run ios                # open Xcode
```

`sync` must build Angular before running `cap sync`. Platform open/run commands do not silently
modify production environment files.

## Testing and CI

### Required on every pull request

- Existing backend formatting and tests.
- Existing frontend lint, dependency checks, unit tests, and production build.
- App lint, unit tests, dependency-boundary checks, and production Angular build.
- OpenAPI regeneration followed by a clean generated-source diff check.
- Verification that `www/index.html` exists before Capacitor synchronization.

### Native build validation

- Android debug compilation on Linux with a pinned JDK and Android SDK.
- iOS simulator compilation on macOS without signing.
- No store credentials or release signing in pull-request workflows.

Native UI automation is deferred until the app has a real critical path. Browser-level Playwright
tests can cover initial routing and API integration; a later proposal can select Appium or Maestro
for device-level flows.

## Delivery Plan

### Increment 1: Buildable app shell

- Use `ionic start --capacitor` to generate and initialize the Angular 21 standalone application
  under `app/`; retain the generator output as the starting point for all subsequent changes.
- Configure Ionic routing, Vitest, linting, dependency boundaries, and Bun.
- Configure `www/index.html` output and Capacitor.
- Use `cap add android` and `cap add ios`, then add and commit the generated native projects.
- Add app documentation and CI Angular-build checks.

Exit criteria: the app builds, tests, renders in a browser, synchronizes into both native projects,
and both native projects compile in CI.

### Increment 2: Backend integration

- Add the app OpenAPI generator execution.
- Configure build-specific API base URLs.
- Add property-driven backend CORS configuration and tests.
- Implement one read-only users page using the generated client, with tests written first.
- Add generated-source drift validation to CI.

Exit criteria: the browser, Android emulator, and iOS simulator can load users from the same backend
contract and present a useful error state when the backend is unavailable.

### Increment 3: Blueprint lifecycle

- Extend and test `init-project.sh` project and native identifier replacement.
- Run the real initialization command against a temporary target and build the generated backend,
  web client, and app; do not validate initialization through mocked file replacements alone.
- Update `README.md`, `AGENTS.md`, Docker documentation, and development commands.
- Add native icons and splash assets with documented regeneration commands.
- Verify blueprint synchronization does not treat native build output as source.

Exit criteria: a newly initialized repository has unique web, Android, and iOS identities and passes
all documented build and test commands without manual source edits.

## Expected File Changes

| Area | Change |
|---|---|
| `app/` | New Ionic Angular application and Capacitor native projects |
| `backend/pom.xml` | Add app API-client generation |
| Backend security/config | Add property-driven CORS configuration and tests |
| `init-project.sh` | Add native app ID parsing, validation, and replacement |
| `.github/workflows/test.yml` | Add app lint, test, and build job |
| New native workflows | Add Android and unsigned iOS compilation |
| `README.md` | Document app architecture, prerequisites, and commands |
| `AGENTS.md` | Add app conventions and verification commands |
| `.gitignore` / `.dockerignore` | Exclude app build and native transient output |

The existing Docker image continues to package the Spring Boot backend and Angular web frontend.
The app and native toolchains are not added to the Docker build.

## Alternatives Considered

### Convert `frontend/` into one Ionic application

Rejected. It couples browser deployment to mobile navigation and native release requirements and
would force existing web components into an Ionic presentation model.

### Create a root Angular workspace containing both applications

Deferred. This can reduce dependency duplication, but it increases blueprint migration scope and
couples upgrades and tooling. Separate applications are easier to understand and can be combined
later if workspace-level reuse becomes valuable.

### Publish one shared generated API-client package

Deferred. It avoids generated-file duplication but requires Angular package build and versioning
infrastructure. Per-consumer generation is simpler while both consumers use the same generator and
contract.

### Ship only a progressive web app

Rejected for this proposal because it does not provide the requested native iOS and Android
project templates or a standard path to native capabilities and app-store distribution.

## Risks and Mitigations

| Risk | Mitigation |
|---|---|
| Angular/Ionic/Capacitor version drift | Pin compatible versions and update web and app Angular versions together. |
| Generated clients diverge | Generate both from Maven and enforce a clean diff in CI. |
| Local API works in browser but not on device | Require an absolute app API URL and document emulator/LAN configuration. |
| CORS is overly permissive | Use profile-driven explicit origins and test production defaults. |
| Native folders accumulate generated noise | Commit native source but ignore build, dependency, and IDE-local output. |
| Web components leak into mobile UI | Enforce separate dependency boundaries and share only non-UI logic. |
| App identity remains a placeholder | Require and validate `--app-id` during blueprint initialization. |

## Acceptance Criteria

The proposal is implemented when:

- `backend/`, `frontend/`, and `app/` are independently buildable.
- The Ionic application and native projects originate from the documented CLI generation and
  initialization commands rather than a hand-created skeleton.
- The app uses standalone Ionic components, signals-first Angular patterns, and lazy routes.
- Android and iOS projects build from a clean checkout.
- Both Angular clients are generated from the same OpenAPI document.
- Native runtime builds use an absolute HTTPS production API endpoint.
- CORS allows only configured web and native origins.
- A generated project receives a validated, unique native application identifier.
- CI detects test failures, native compile failures, invalid Capacitor web output, and generated API
  drift.
- No secrets or local live-reload URLs are committed.
