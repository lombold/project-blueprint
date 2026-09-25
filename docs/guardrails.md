# Automated Gardrails for AI Systems

## Compile Time

Backend:-Xlint:all and -Werror (strict javac rule checks like deprecation), mapstruct (strict mapping checks)
Frontend: strict rule checks (typescript-compiler), strict template (angular compiler)

## Linting and best practices

Backend: checkstyle (checkstyle-style), spotbugs, errorprone, findbugs, dependency-check (not used: pmd, cpd)
Frontend: eslint (with angular-eslint, template, accessibility), stylelint

## Formatting and Style

Backend: spotless (palantir-java-format)
Frontend: prettier

## Architecture

Backend: Archunit (domain→application→adapter)
Frontend: dependency-cruiser

## Team / Personal standards

Backend:
Frontend:

## Enforcement

CI Pipeline (github actions)

maven verify: enforcer:enforce, spotless:check, checkstyle:check

=> pre-commit hooks

* Stufe 1 (5 sek): autofix (spotless:apply, prettier --write, eslint --fix)
* Stufe 2 (30 sek): full verification (maven verify, eslint, stylelint)
