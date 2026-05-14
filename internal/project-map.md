# Project Map

## 1. Purpose

This document is the internal navigation map for `checkout-system`.

It tells readers where to read for common project work scenarios.

It routes reading.

It does not define:

```text
- project state
- system behavior
- correctness
- implementation
- validation evidence
- reusable convention rules
```

Source documents define truth.

This map points to those source documents.

## 2. Authority Rule

This project map is not the source of project truth.

If this map conflicts with a source document, the source document is authoritative and this map must be updated.

Authority order:

```text
internal/project-state.md
    → current project position

internal/system-definition/
    → system truth

internal/implementation/
    → implementation baseline truth

internal/execution/
    → execution bridge and future Work Unit records

docs/
    → public/project-facing explanation

README.md
    → repository entry point
```

Information flows outward:

```text
internal → docs → README.md
```

## 3. Main Reading Entry Points

Use these files as starting points:

```text
README.md
    Public repository entry point.

docs/system-overview.md
    Public explanation of what the system is.

docs/methodology.md
    Public explanation of the construction approach.

docs/setup/README.md
    Setup documentation entry point.

internal/project-state.md
    Current project position and readiness record.

internal/system-definition/README.md
    Navigation for internal System Definition.

internal/implementation/baseline-docs-reference.md
    Navigation and rules for implementation baseline docs.

internal/execution/slice-register.md
    Candidate Work Unit register and execution bridge.
```

## 4. Current State Reading Path

Use this path when the task is about:

```text
- current phase
- current stratum
- completed preparation work
- readiness
- next work
- active constraints
```

Read:

```text
internal/project-state.md
```

Do not infer current state from README, public docs, or this project map.

## 5. System Definition Reading Path

Use this path when the task is about:

```text
- what the system is
- why it exists
- what it controls
- what it does not control
- responsibility ownership
- problem space
- interaction and composition risks
```

Read in order:

```text
internal/system-definition/README.md
internal/system-definition/project-description.md
internal/system-definition/project-intent.md
internal/system-definition/system-context.md
internal/system-definition/system-structure.md
internal/system-definition/problem-space.md
internal/system-definition/interaction-model.md
```

System Definition source truth lives in:

```text
internal/system-definition/
```

Public projections of this truth live in:

```text
docs/system-overview.md
docs/methodology.md
README.md
```

## 6. Public Documentation Reading Path

Use this path when the task is about:

```text
- public project explanation
- portfolio readability
- external reader orientation
- concise project overview
- public methodology explanation
```

Read:

```text
README.md
docs/system-overview.md
docs/methodology.md
```

Responsibilities:

```text
README.md
    → orients readers and links to public documentation

docs/system-overview.md
    → explains the system publicly

docs/methodology.md
    → explains the construction approach publicly
```

Public documentation explains selected project understanding.

It does not replace internal truth.

## 7. Setup / Local Environment Reading Path

Use this path when the task is about:

```text
- local setup
- container runtime expectations
- Compose usage
- local infrastructure service orchestration
- local PostgreSQL service usage
- local infrastructure inspection
```

Read:

```text
docs/setup/README.md
docs/setup/local-containers.md
docs/setup/postgres-local.md
compose.yaml
.env.example
```

Responsibilities:

```text
docs/setup/README.md
    → setup documentation navigation

docs/setup/local-containers.md
    → local container runtime and Compose usage

docs/setup/postgres-local.md
    → local PostgreSQL service usage and constrained setup guidance

compose.yaml
    → project-level local infrastructure orchestration

.env.example
    → example local service-level and database role environment variables
```

This path defines:

```text
- local infrastructure service existence
- local service execution model
- local service configuration shape
- local service usage
- local PostgreSQL setup entry point
```

This path does not define:

```text
- system behavior
- project state
- correctness slices
- application business schema
- application bootstrap behavior
```

## 8. Service Constraints Reading Path

Use this path when the task is about:

```text
- PostgreSQL authority model
- database role separation
- database ownership
- schema ownership
- migration authority
- runtime privilege boundaries
- database bootstrap verification
```

Read:

```text
docs/system/database-role-model.md
db/init/
docs/setup/postgres-local.md
.env.example
internal/preparation/postgresql-database-model-verification.md
```

Responsibilities:

```text
docs/system/database-role-model.md
    → explains the PostgreSQL authority model

db/init/
    → executable PostgreSQL initialization and verification scripts

docs/setup/postgres-local.md
    → explains how to apply and verify constrained local PostgreSQL setup

.env.example
    → example local credential and database role contract

internal/preparation/postgresql-database-model-verification.md
    → internal interpretation of database model verification
```

This path defines:

```text
- bootstrap database identity
- project database identity
- application schema identity
- bootstrap role
- database owner role
- migration role
- runtime role
- runtime access boundary
- verification interpretation
```

This path does not define:

```text
- business tables
- application entities
- reservation schema
- checkout workflow
- API contracts
- Construction slice behavior
```

## 9. Implementation Baseline Reading Path

Use this path when the task is about:

```text
- application implementation constraints
- package and layer boundaries
- persistence constraints
- testing and verification constraints
- API constraints
- local runtime constraints
- Construction implementation planning inputs
```

Read:

```text
internal/implementation/baseline-docs-reference.md
internal/implementation/application-baseline.md
internal/implementation/persistence-baseline.md
internal/implementation/testing-baseline.md
internal/implementation/api-baseline.md
internal/implementation/local-runtime-baseline.md
```

Responsibilities:

```text
baseline-docs-reference.md
    → explains what baseline docs are, when to use them, and how to maintain them

application-baseline.md
    → records Java, Spring Boot, Maven, package, layer, and application structure constraints

persistence-baseline.md
    → records PostgreSQL, Flyway, schema, role, and migration constraints

testing-baseline.md
    → records test execution, naming, boundary, and verification constraints

api-baseline.md
    → records HTTP, API error handling, and API expansion constraints

local-runtime-baseline.md
    → records local runtime, profile, Compose, Testcontainers, and environment constraints
```

This path defines:

```text
- project-wide implementation constraints
- Preparation exit implementation baselines
- constraints consumed by Construction implementation planning
```

This path does not define:

```text
- slice-specific implementation design
- final business schema
- final API contracts
- final classes or methods
- validation evidence
- completion evidence
```

## 10. Execution Bridge Reading Path

Use this path when the task is about:

```text
- candidate Work Units
- selected Work Unit
- slice status
- deferred correctness work
- future Construction entry
```

Read:

```text
internal/execution/slice-register.md
```

The slice register identifies executable correctness slices.

It does not solve them.

## 11. Construction Reading Path

Use this path when the task is about:

```text
- selected Work Unit
- Work Unit status
- slice clarification
- correctness construction
- implementation requirements
- validation records
- completion records
```

Read:

```text
internal/project-state.md
internal/execution/slice-register.md
internal/execution/slices/<slice-id>-<slice-name>/
```

For implementation planning, also read the relevant implementation baseline docs:

```text
internal/implementation/baseline-docs-reference.md
internal/implementation/application-baseline.md
internal/implementation/persistence-baseline.md
internal/implementation/testing-baseline.md
internal/implementation/api-baseline.md
internal/implementation/local-runtime-baseline.md
```

Expected per-slice artifact location pattern:

```text
internal/execution/slices/
    <slice-id>-<slice-name>/
        execution-context.md
        slice-clarification.md

        construction-notes/
            exploration-and-failure-mechanism.md
            invariant-challenge.md
            guarantee-challenge.md

        correctness-construction.md
        implementation-requirements.md

        implementation-plan.md
        validation-record.md
        completion-record.md
```

Detailed slice truth belongs in the slice artifacts, not in this map.

## 12. Consistency Groups

### System Definition Consistency Group

Check together when system definition changes:

```text
internal/system-definition/project-description.md
internal/system-definition/project-intent.md
internal/system-definition/system-context.md
internal/system-definition/system-structure.md
internal/system-definition/problem-space.md
internal/system-definition/interaction-model.md
internal/system-definition/README.md
internal/execution/slice-register.md
docs/system-overview.md
docs/methodology.md
README.md
```

### Public Documentation Consistency Group

Check together when public-facing explanation changes:

```text
README.md
docs/system-overview.md
docs/methodology.md
```

### Setup / Local Environment Consistency Group

Check together when local setup, execution environment, or infrastructure service usage changes:

```text
compose.yaml
.env.example
docs/setup/README.md
docs/setup/local-containers.md
docs/setup/postgres-local.md
internal/project-map.md
internal/project-state.md
```

### Service Constraints Consistency Group

Check together when PostgreSQL authority, roles, schemas, privileges, or verification changes:

```text
docs/system/database-role-model.md
db/init/
docs/setup/postgres-local.md
.env.example
internal/preparation/postgresql-database-model-verification.md
internal/project-map.md
internal/project-state.md
```

### Implementation Baseline Consistency Group

Check together when implementation baseline constraints change:

```text
internal/implementation/baseline-docs-reference.md
internal/implementation/application-baseline.md
internal/implementation/persistence-baseline.md
internal/implementation/testing-baseline.md
internal/implementation/api-baseline.md
internal/implementation/local-runtime-baseline.md
internal/project-map.md
internal/project-state.md
```

### Execution Bridge Consistency Group

Check together when candidate Work Units or execution entry changes:

```text
internal/system-definition/problem-space.md
internal/system-definition/interaction-model.md
internal/execution/slice-register.md
internal/project-state.md
```

## 13. External Convention Use Rule

Reusable conventions live outside this repository.

Use external conventions when:

```text
- creating or reviewing structural project documents
- changing documentation responsibility boundaries
- updating this project map structurally
- planning or checking Git history
- deciding which reusable process rule applies
```

Relevant convention entry point:

```text
system-construction-conventions/conventions-map.md
```

Relevant conventions for the current Preparation structure:

```text
system-construction-conventions/execution-model-core.md
system-construction-conventions/preparation-phase-reference.md
system-construction-conventions/project-documentation-structure-convention.md
system-construction-conventions/project-state-convention.md
system-construction-conventions/project-map-convention.md
system-construction-conventions/system-definition-convention.md
system-construction-conventions/git-history-construction-convention.md
```

External conventions define reusable structure and process.

Project-specific truth remains in this repository.

## 14. Maintenance Rule

Update this project map when:

```text
- important files are added, renamed, or moved
- a new major project area appears
- a recurring reading scenario appears
- a reading path becomes inaccurate
- a consistency group changes
- convention usage changes
```

Do not update this project map when:

```text
- project state changes but reading paths stay the same
- wording changes inside a source document
- one small implementation file changes
- one test is added
```

Project state changes belong in:

```text
internal/project-state.md
```

This map changes when navigation changes.

## 15. One-Line Mental Model

```text
Use this map to find where to read; do not use it as the source of truth.
```
