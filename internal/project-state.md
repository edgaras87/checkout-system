# Project State

## 1. Purpose

This document records the current execution state of `checkout-system`.

It answers:

```text
What is true about the project state right now?
```

It does not define:

```text
- system behavior
- project navigation
- public explanation
- implementation design
- correctness construction
- validation evidence
```

---

## 2. Project Identity

```text
Project name: checkout-system
Repository name: checkout-system
Local folder: checkout-system
```

Project description:

```text
Correctness-driven checkout backend for resolving purchase attempts into one consistent final outcome under concurrency, retries, and unreliable external signals.
```

---

## 3. Repository

```text
Provider: GitHub
Remote URL: https://github.com/edgaras87/checkout-system
Visibility: public
Default branch: main
```

Repository description:

```text
Correctness-driven checkout backend for resolving purchase attempts into one consistent final outcome under concurrency, retries, and unreliable external signals.
```

---

## 4. Current Execution Position

```text
Phase: Preparation

Current stratum:
    Stratum 5 — Service Constraints completed

Status:
    ready to begin Stratum 6 — Application Bootstrap

Construction status:
    not started
```

---

## 5. Completed Preparation Work

```text
Completed strata:
    - Stratum 1 — Project Workspace & History
    - Stratum 2 — System Definition
    - Stratum 3 — Execution Environment
    - Stratum 4 — Infrastructure Services
    - Stratum 5 — Service Constraints
```

### Stratum 1 established

```text
- controlled repository workspace
- Git text normalization
- repository ignore rules
- minimal repository entry point
- initial project state record
```

### Stratum 2 established

```text
- project description
- project intent
- system context and boundary
- responsibility structure
- problem space
- interaction model
- initial slice register
- internal System Definition navigation
- public system overview
- public methodology explanation
- README alignment
- internal project navigation map
```

### Stratum 3 established

```text
- Docker-compatible local container runtime expectation
- Compose as local orchestration mechanism
- compose.yaml as project-level orchestration entry point
- setup documentation for local container usage
- setup/local environment reading path in the project map
```

### Stratum 4 established

```text
- PostgreSQL as the local infrastructure service
- PostgreSQL as the persistent state service
- PostgreSQL service definition in compose.yaml
- service-level PostgreSQL environment variables
- example local environment file
- persistent named volume for local PostgreSQL data
- local PostgreSQL setup documentation
- setup/local environment reading path updated for PostgreSQL service usage
```

### Stratum 5 established

```text
- PostgreSQL authority model
- bootstrap database identity
- project database identity
- project database role separation
- project database ownership model
- application schema ownership model
- migration authority boundary
- runtime privilege boundary
- executable PostgreSQL initialization scripts
- executable database authority verification
- local constrained PostgreSQL setup documentation
- local credential and database role contract
- service constraints reading path in the project map
- internal interpretation of database model verification
```

---

## 6. Active Constraints

```text
- System Definition is the current source of system truth.
- Public documentation is a projection of internal truth.
- README is an entry point, not a project-state record.
- project-map.md routes reading and does not define project truth.
- slice-register.md is an execution bridge and does not solve Work Units.
- The execution environment defines how local infrastructure runs.
- Infrastructure services define which local service capabilities exist.
- PostgreSQL authority is constrained before application bootstrap begins.
- PostgreSQL bootstrap variables are container initialization settings, not application database ownership.
- compose.yaml is the local infrastructure orchestration entry point.
- .env.example defines the local credential and database role contract.
- docs/system/database-role-model.md defines PostgreSQL authority boundaries.
- db/init/ defines executable PostgreSQL authority initialization and verification.
- checkout_runtime must not own or mutate database structure.
- checkout_migrator owns future application schema evolution.
- Future application migrations are expected to run as checkout_migrator.
- Application business schema does not yet exist.
- Flyway application migrations do not yet exist.
- Application bootstrap is deferred to Stratum 6.
- Construction has not started.
- Later strata must align with System Definition.
```

Not yet defined:

```text
- application business tables
- application persistence model
- Flyway baseline migration
- Spring Boot runtime configuration
- application runtime database integration
- baseline runtime verification
- correctness slice implementation
```

---

## 7. Service Constraints State

Current constrained PostgreSQL authority model:

```text
Bootstrap database:
    postgres

Project database:
    checkout_system

Application schema:
    app
```

Current PostgreSQL role model:

```text
postgres_root:
    bootstrap/superuser authority

checkout_admin:
    project database owner

checkout_migrator:
    schema owner and migration authority

checkout_runtime:
    runtime data access only
```

Current authority boundaries:

```text
checkout_runtime:
    may manipulate application data
    must not mutate database structure

checkout_migrator:
    owns future application schema evolution

public schema:
    must not be used as an uncontrolled application schema
```

Current executable authority artifacts:

```text
docs/system/database-role-model.md
db/init/
docs/setup/postgres-local.md
.env.example
internal/preparation/postgresql-database-model-verification.md
```

Current verification state:

```text
- database authority model is executable
- database authority model is verifiable
- verification interpretation is recorded
- application tables are intentionally not created yet
```

---

## 8. Readiness / Next Transition

```text
Stratum 5 — Service Constraints is complete.

The project is ready to begin Stratum 6 — Application Bootstrap.
```

Next expected work:

```text
- establish Spring Boot application baseline
- establish runtime configuration
- establish Flyway baseline migration structure
- connect application runtime to constrained PostgreSQL model
- establish baseline runtime verification
```

---

## 9. Deferred or Unresolved Work

Deferred to later Preparation strata:

```text
- application bootstrap
```

Deferred to Construction:

```text
- Reservation Capacity Correctness
- Order Idempotency
- Payment Interpretation Under Conflict
- Final Outcome State Uniqueness
- Final Outcome Composition Correctness
```

---

## 10. Last Updated Rule

Update this document when:

```text
- a preparation stratum starts or completes
- lifecycle position changes
- Construction begins
- readiness changes
- major active constraints change
- deferred or unresolved work changes materially
```

Do not update this document for:

```text
- ordinary wording changes
- small documentation edits
- navigation-only project-map changes
- minor formatting cleanup
```

---

## 11. One-Line State

```text
Stratum 5 — Service Constraints is complete; PostgreSQL authority is now constrained, executable, verifiable, and ready for Application Bootstrap.
```
