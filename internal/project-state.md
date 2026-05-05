# 📄 **Project State**

---

## **1. Project Identity**

```text
Project name: checkout-system
Repository name: checkout-system
Local folder: checkout-system
Default branch: main
```

---

## **2. Remote Repository**

```text
Provider: GitHub
Remote URL: https://github.com/edgaras87/checkout-system.git
Visibility: public
Description: Correctness-driven checkout backend for resolving purchase attempts into one consistent final outcome under concurrency, retries, and unreliable external signals
```

---

## **3. Current Execution Position**

```text
Phase: Preparation
Current stratum: 6 — Application Bootstrap completed
Completed strata:
    - Stratum 1 — Project Workspace & History
    - Stratum 2 — System Definition
    - Stratum 3 — Execution Environment
    - Stratum 4 — Infrastructure Services
    - Stratum 5 — Service Constraints
    - Stratum 6 — Application Bootstrap
Status: Preparation Phase ready for exit review or Construction preparation
```

---

## **4. System Definition State**

```text
System definition: completed
Internal source of truth: internal/system-definition/
Execution bridge: internal/execution/slice-landscape.md
Public projection: docs/
Repository entry point: README.md
```

---

## **5. Execution Environment State**

```text
Execution environment: completed
Local orchestration entry point: compose.yaml
Local setup entry point: docs/setup/README.md
Local container setup: docs/setup/local-containers.md
Runtime model: Docker-compatible container runtime
Orchestration model: Compose
```

---

## **6. Infrastructure Services State**

```text
Infrastructure services: completed
Defined infrastructure service: PostgreSQL
Service role: persistent state service
Local service documentation: docs/setup/postgres-local.md
Service execution model: containerized PostgreSQL managed through Compose
Persistent storage model: local named volume
```

---

## **7. Service Constraints State**

```text
Service constraints: completed
Defined constraint area: PostgreSQL database authority model
Database role model: docs/system/database-role-model.md
Local setup documentation: docs/setup/postgres-local.md
Initialization scripts: db/init/
Bootstrap database: postgres
Project database: checkout_system
Application schema: app
Bootstrap role: postgres_root
Database owner role: checkout_admin
Migration role: checkout_migrator
Runtime role: checkout_runtime
```

Service constraints define:

```text
- bootstrap authority boundary
- bootstrap database boundary
- project database ownership
- schema ownership
- migration authority
- runtime access boundary
- public access restrictions
- verification checks
```

---

## **8. Application Bootstrap State**

```text
Application bootstrap: completed
Application framework: Spring Boot
Build tool: Maven
Packaging: jar
Java version: 21
Base package: com.edge.checkout
Runtime configuration model: profile-based
Manual local startup profile: dev
Manual local startup documentation: exists
Automated test profile: test
Database connection: configured
Migration tool: Flyway
Runtime DB role: checkout_runtime
Migration DB role: checkout_migrator
Baseline migration: exists
Business tables: not defined during bootstrap
Runtime table privilege verification: deferred until real tables exist
Generated context test: removed after persistence wiring
Testcontainers runtime setup documentation: exists
PostgreSQL Testcontainers integration test baseline: exists
Maven Surefire *IT discovery: configured
Web smoke test: exists
Bootstrap HTTP endpoint: exists
Lombok: configured
API error handling baseline: exists
API error handling web-only verification: exists
Problem Detail type URI catalog: deferred
```

Application Bootstrap defines:

```text
- Spring Boot application baseline
- Maven build baseline
- Java 21 runtime baseline
- application entry point
- project-specific Spring Boot structure orientation
- shared application configuration
- dev profile configuration
- test profile configuration
- datasource configuration
- Flyway migration configuration
- baseline migration lifecycle
- PostgreSQL Testcontainers integration test baseline
- web-only smoke test baseline
- bootstrap HTTP sanity endpoint
- Lombok annotation processing support
- API error handling baseline
```

Application Bootstrap does not define:

```text
- checkout business behavior
- reservation behavior
- order behavior
- payment behavior
- final outcome behavior
- business database tables
- business migrations
- public API error catalog
- production deployment model
```

---

## **9. Verification State**

```text
Standard verification command: ./mvnw test
Current verification result: passing
Manual local startup command: ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
Manual local startup result: verified
Bootstrap HTTP endpoint: GET /ping returns pong
```

Verified by automated tests:

```text
- web smoke test verifies HTTP boot and routing
- API error handling test verifies HTTP error response baseline
- PostgreSQL Testcontainers integration test verifies Flyway migration lifecycle against real PostgreSQL
```

Verification boundaries:

```text
Manual dev startup verification:
    Uses dev profile
    Requires local Compose PostgreSQL
    Uses checkout_runtime for application datasource
    Uses checkout_migrator for Flyway
    Verifies local application startup against checkout_system/app
    Verifies bootstrap HTTP endpoint through GET /ping

Web smoke verification:
    Uses random-port Spring Boot web test
    Does not require PostgreSQL, Flyway, JPA, Testcontainers, Docker, or Podman

API error handling verification:
    Uses random-port Spring Boot web test
    Verifies title/status/detail/errors response contract
    Does not require database infrastructure
    Does not assert Problem Detail type URIs

PostgreSQL integration verification:
    Uses Testcontainers with real PostgreSQL
    Verifies Flyway baseline migration is applied
    Does not require local Compose PostgreSQL
    Does not verify business table behavior
```

Current automated verification covers:

```text
- Spring Boot web startup
- bootstrap HTTP routing through GET /ping
- request body validation error handling
- request parameter validation error handling
- malformed JSON error handling
- bad request exception handling
- server-side invariant violation handling
- Flyway migration history creation
- baseline migration execution
- Maven Surefire discovery of *Test.java, *Tests.java, and *IT.java
```

Current automated verification intentionally does not cover:

```text
- reservation capacity correctness
- order idempotency
- payment interpretation
- final outcome resolution
- runtime privileges on business tables
- business persistence behavior
```

Reason:

```text
Business behavior and business persistence belong to Construction work, not Application Bootstrap.
```

---

## **10. Current Project State**

```text
- repository workspace is initialized
- git history convention is established
- system definition is authoritative
- problem space is mapped
- candidate slices are identified
- public documentation exists
- README exists as navigation entry point
- local setup documentation exists
- local container execution model is documented
- Compose orchestration entry point exists
- PostgreSQL infrastructure service is defined
- PostgreSQL runs locally through Compose
- PostgreSQL service-level configuration is documented
- infrastructure services are completed
- PostgreSQL database role model is documented
- PostgreSQL bootstrap database is defined as postgres
- PostgreSQL bootstrap authority is separated from project database ownership
- PostgreSQL initialization scripts exist
- project database is defined as checkout_system
- application schema is defined as app
- project database owner role is defined as checkout_admin
- migration role is defined as checkout_migrator
- runtime role is defined as checkout_runtime
- runtime role is restricted to data access
- database authority boundaries are verifiable
- service constraints are completed
- Spring Boot application baseline exists
- Maven build baseline exists
- Maven Wrapper exists
- application entry point exists
- Java base package is com.edge.checkout
- Java baseline is 21
- project-specific Spring Boot application structure state exists
- dev/test profiles exist
- dev profile is used for manual local startup
- manual local startup with dev profile is documented
- test profile is used for automated tests
- local application environment contract exists
- application runtime connects through checkout_runtime
- Flyway migrations run through checkout_migrator
- baseline migration exists
- baseline migration intentionally contains no business tables
- generated generic context test was removed after persistence wiring
- runtime table privilege verification is deferred until real tables exist
- Testcontainers runtime setup documentation exists
- PostgreSQL Testcontainers integration test baseline exists
- Maven Surefire includes *Test.java, *Tests.java, and *IT.java
- web smoke test exists
- bootstrap HTTP endpoint exists
- bootstrap HTTP endpoint is verified through web smoke testing
- Lombok is configured
- API error handling baseline exists
- API error handling baseline is verified through a web-only integration test
- Problem Detail type URI catalog is intentionally deferred
- Application Bootstrap is completed
```

---

## **11. Next Step**

```text
Perform Preparation Phase exit review or begin Construction preparation
```

Expected next decision area:

```text
Move from application readiness to controlled slice construction.
```

Likely first construction focus:

```text
SL-01 — Reservation Capacity Correctness
```

Construction should begin from the prepared baseline:

```text
Application:
    Spring Boot application exists

Build:
    Maven build and wrapper exist

Runtime configuration:
    dev/test profiles exist

Database:
    PostgreSQL service and authority model exist

Migration:
    Flyway baseline exists

Verification:
    web smoke, API error handling, and PostgreSQL migration tests pass
```

Construction must preserve established boundaries:

```text
System definition:
    Must not be redefined during implementation

PostgreSQL authority model:
    Runtime access must remain separated from migration authority

Business persistence:
    Must be introduced through real slice-driven migrations

API behavior:
    Must inherit the existing API error handling baseline

Testing:
    Must keep clear verification boundaries between web-only tests and DB-backed tests
```

---

## **12. Notes**

```text
System Definition is complete enough to support controlled execution

Execution Environment is complete enough to host local infrastructure services

Infrastructure Services are complete enough to provide PostgreSQL as the local persistent state service

Service Constraints are complete enough to define PostgreSQL database authority boundaries

Application Bootstrap is complete enough to host controlled Construction work

The local PostgreSQL bootstrap database is postgres

The project database is checkout_system and is created by initialization scripts

The application schema is app

Application runtime must not use bootstrap, admin, or migration authority

Application runtime must connect through checkout_runtime

Schema changes must be performed through checkout_migrator

Business tables and application-specific schema objects are not defined during Application Bootstrap

Application Bootstrap defines migration infrastructure and neutral baseline verification only

Business database structure belongs to later Construction work

Runtime table privilege verification is deferred until real business tables exist

Problem Detail type URIs are deferred until a public API error catalog is intentionally defined

Future Construction work must align with the established system definition and must not redefine system identity, boundaries, or responsibility areas

Future Construction work must consume the defined PostgreSQL service and database constraints without redefining them

Future Construction work must preserve the authority boundaries defined in Stratum 5

Future Construction work must preserve the Application Bootstrap verification boundaries unless a slice explicitly extends them
```
