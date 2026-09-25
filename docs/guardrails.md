# Automated Gardrails for AI Systems
## Compile Time
Backend: 
Frontend: strict rule checks (typescript-compiler), stict template (angular compiler)

## Formatting and Style
Backend: spotless (palantir-java-format)
Frontend: prettier ()

## Linting and best practices
Backend: checkstyle (checkstyle-style)
Frontend: eslint (with angular-eslint, templateChecks), stylelint

## Architecture 
Backend:
Frontend: dependency-cruiser

## Team / Personal standards
Backend:
Frontend: 

## Enforcement
CI Pipeline (github actions)

maven verify: enforcer:enforce, spotless:check, checkstyle:check

=> pre-commit hooks


