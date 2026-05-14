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
    Stratum 6 — Application Bootstrap completed

Status:
    Preparation Phase completed
    ready for Preparation exit review or Construction preparation

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
    - Stratum 6 — Application Bootstrap
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

### Stratum 6 established

```text
- Spring Boot application baseline
- Java 21 application baseline
- Maven build baseline
- Maven Wrapper
- application entry point
- base package com.edge.checkout
- profile-based configuration
- dev profile for manual local startup
- test profile for automated tests
- .env import for local runtime configuration
- datasource configuration
- Flyway configuration
- Spring Data JPA integration
- PostgreSQL driver integration
- JDBC support
- Flyway PostgreSQL support
- baseline migration
- neutral migration path without business tables
- Hibernate schema validation mode
- disabled Hibernate schema mutation
- disabled Open Session in View
- generated generic context test removed after persistence wiring
- Testcontainers runtime setup documentation
- PostgreSQL Testcontainers integration test baseline
- DB-only integration test base
- web + DB integration test base
- Flyway migration verification against real PostgreSQL
- Maven Surefire discovery for *Test.java, *Tests.java, and *IT.java
- web-only smoke test baseline
- bootstrap HTTP sanity endpoint
- GET /ping returns pong
- Lombok configuration for boilerplate reduction
- API error handling baseline
- API error handling verification
- implementation baseline docs as Preparation exit artifacts
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
- PostgreSQL authority is constrained before application behavior begins.
- PostgreSQL bootstrap variables are container initialization settings, not application database ownership.
- compose.yaml is the local infrastructure orchestration entry point.
- .env.example defines the local credential and database role contract.
- docs/system/database-role-model.md defines PostgreSQL authority boundaries.
- db/init/ defines executable PostgreSQL authority initialization and verification.
- checkout_runtime must not own or mutate database structure.
- checkout_migrator owns application schema evolution.
- Application migrations run through Flyway.
- Application runtime uses checkout_runtime.
- Flyway uses checkout_migrator for local dev migration authority.
- Hibernate validates schema compatibility but does not create or update schema.
- Application Bootstrap intentionally defines no business tables.
- Bootstrap HTTP endpoint is not business behavior.
- API error handling baseline exists before business endpoints.
- Implementation baseline docs constrain Construction implementation planning.
- Construction has not started.
- Later Construction work must align with System Definition and implementation baselines.
```

Not yet defined:

```text
- reservation business behavior
- order business behavior
- payment business behavior
- final outcome business behavior
- application business tables
- business persistence model
- business API endpoints
- runtime privileges on real business tables
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
    owns application schema evolution through Flyway

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
- application tables are intentionally not created during bootstrap
```

---

## 8. Application Bootstrap State

Application baseline:

```text
Framework:
    Spring Boot

Language:
    Java

Java version:
    21

Build tool:
    Maven

Packaging:
    jar

Base package:
    com.edge.checkout

Application entry point:
    CheckoutSystemApplication
```

Configuration baseline:

```text
Shared configuration:
    application.yaml

Manual local profile:
    dev

Automated test profile:
    test

Local environment import:
    optional:file:.env[.properties]
```

Persistence baseline:

```text
Persistence service:
    PostgreSQL

Migration tool:
    Flyway

Application schema:
    app

Baseline migration:
    src/main/resources/db/migration/V1__baseline.sql

Business tables:
    not defined during Application Bootstrap
```

Runtime authority baseline:

```text
Runtime datasource authority:
    checkout_runtime

Migration authority:
    checkout_migrator

Runtime DDL:
    not allowed

Hibernate schema mutation:
    disabled

Hibernate schema validation:
    enabled
```

Testing baseline:

```text
Standard verification command:
    ./mvnw test

Maven test discovery:
    *Test.java
    *Tests.java
    *IT.java

DB-backed verification:
    PostgreSQL Testcontainers

Web-only verification:
    database-independent random-port web tests
```

Current bootstrap HTTP endpoint:

```text
GET /ping
    → pong
```

API baseline:

```text
- request body validation failures are handled
- request parameter validation failures are handled
- malformed JSON is handled
- bad request exceptions are handled
- server-side invariant violations are handled as server errors
- Problem Detail type URI catalog is deferred
```

---

## 9. Implementation Baseline Docs State

Implementation baseline docs are Preparation exit artifacts.

Location:

```text
internal/implementation/
```

Current baseline docs:

```text
baseline-docs-reference.md
application-baseline.md
persistence-baseline.md
testing-baseline.md
api-baseline.md
local-runtime-baseline.md
```

Purpose:

```text
Implementation baseline docs record project-wide implementation constraints that Construction implementation planning must preserve.
```

They are consumed by:

```text
- implementation planning
- slice design decisions
- validation planning
- baseline preservation checks
```

They do not define:

```text
- slice-specific implementation design
- final business schema
- final API contracts
- final classes or methods
- validation evidence
- completion evidence
```

---

## 10. Verification State

Automated verification command:

```bash
./mvnw test
```

Automated verification currently covers:

```text
- PostgreSQL Testcontainers integration test baseline
- Flyway migration lifecycle against real PostgreSQL
- baseline migration recording in flyway_schema_history
- web smoke test
- GET /ping returns pong
- API error handling baseline
- Maven test discovery for *Test.java, *Tests.java, and *IT.java
```

Manual local startup command:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

Manual local startup verifies:

```text
- dev profile is active
- local PostgreSQL is reachable
- datasource configuration resolves
- Flyway connects to checkout_system/app
- baseline migration is validated/applied
- application starts successfully
- bootstrap HTTP endpoint is reachable
```

Manual bootstrap HTTP check:

```bash
curl http://localhost:8080/ping
```

Expected response:

```text
pong
```

---

## 11. Readiness / Next Transition

```text
Stratum 6 — Application Bootstrap is complete.

Preparation Phase is complete enough for exit review or Construction preparation.

Construction has not started.
```

Next expected work:

```text
- review Preparation exit state
- confirm implementation baselines are accepted
- select or confirm first Construction Work Unit
- prepare Construction execution context
- begin SL-01 — Reservation Capacity Correctness when ready
```

---

## 12. Deferred or Unresolved Work

Deferred to Construction:

```text
- Reservation Capacity Correctness
- Order Idempotency
- Payment Interpretation Under Conflict
- Final Outcome State Uniqueness
- Final Outcome Composition Correctness
```

Business implementation deferred to Construction:

```text
- reservation business behavior
- order business behavior
- payment business behavior
- final outcome behavior
- business tables
- business persistence model
- business API endpoints
- runtime privileges on real business tables
```

---

## 13. Last Updated Rule

Update this document when:

```text
- a preparation stratum starts or completes
- lifecycle position changes
- Construction begins
- readiness changes
- major active constraints change
- deferred or unresolved work changes materially
- implementation baseline state changes materially
```

Do not update this document for:

```text
- ordinary wording changes
- small documentation edits
- navigation-only project-map changes
- minor formatting cleanup
```

---

## 14. One-Line State

```text
Preparation is complete through Application Bootstrap; the application foundation, verification baseline, and implementation baselines are ready for Construction preparation.
```
