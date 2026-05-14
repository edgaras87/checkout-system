# Testing Baseline

Status: accepted  
Version: v1  
Scope: checkout-system implementation planning baseline

---

## 0. Baseline Change Rule

This document changes only when a project-wide testing or verification planning constraint changes.

It should not be updated merely because a slice adds test classes or test fixtures.

Slice-specific validation decisions belong in that slice's implementation-plan.md, validation-record.md, or completion-record.md.

A completed slice may update this baseline only when it establishes a reusable testing rule, verification boundary, or validation capability that future slices must preserve.

---

## 0.1 Change Log

```text
v1:
    Created from Preparation Phase testing baseline before first Construction slice.
```

---

## 1. Purpose

This document records the current testing and verification baseline that implementation planning must preserve.

It defines the established test structure, verification boundaries, and test execution model.

It answers:

```text
What testing constraints already exist before slice implementation planning begins?
```

This document does not define slice-specific tests.

It does not write test code.

It does not define exact test methods.

---

## 2. Scope

This baseline covers:

```text
- standard verification command
- Maven test discovery
- test naming rules
- web-only test boundary
- database-backed integration test boundary
- PostgreSQL Testcontainers usage
- current bootstrap verification coverage
- validation expectations for future slices
```

This baseline does not cover:

```text
- final slice test code
- exact concurrency test mechanics
- exact fixture classes
- exact assertion structure
- exact test data
```

---

## 3. Current Baseline

Standard verification command:

```bash
./mvnw test
```

Maven Surefire discovers:

```text
*Test.java
*Tests.java
*IT.java
```

Current automated verification includes:

```text
- Spring Boot web smoke verification
- bootstrap HTTP endpoint verification
- API error handling baseline verification
- PostgreSQL Testcontainers migration verification
- Flyway baseline migration verification
```

Current manual verification includes:

```text
- dev profile startup against local Compose PostgreSQL
- GET /ping returns pong
```

---

## 4. Test Naming Baseline

Naming rules:

```text
*Test:
    unit or focused test

*Tests:
    conventional grouped test

*IT:
    integration test
```

Examples:

```text
ReservationCapacityTest
DatabaseMigrationsIT
ReservationPersistenceIT
ReservationConcurrencyIT
```

Rule:

```text
Integration tests that use Spring, PostgreSQL, HTTP runtime, or Testcontainers should use *IT.
```

---

## 5. Test Structure Baseline

Default test structure:

```text
src/test/java/com/edge/checkout/
├── testsupport/
├── application/
├── domain/
├── infra/
├── db/
└── web/
```

Rules:

```text
- test structure reflects what is tested
- tests do not need to mirror production packages exactly
- shared test setup belongs in testsupport/
- web-only tests should stay separate from DB-backed tests
- DB-backed tests should make database dependency visible
```

---

## 6. Web-Only Verification Boundary

Web-only tests verify HTTP behavior without requiring persistence infrastructure.

Current web-only verification covers:

```text
- Spring Boot web startup
- HTTP routing
- GET /ping
- validation error handling
- malformed JSON handling
- bad request handling
- server-side invariant violation handling
```

Web-only tests must not require:

```text
- PostgreSQL
- Flyway
- JPA
- Testcontainers
- Docker
- Podman
```

Rule:

```text
If a test is meant to verify HTTP behavior only, it should not accidentally become DB-backed.
```

---

## 7. Database-Backed Integration Boundary

Database-backed integration tests verify behavior that depends on real PostgreSQL.

Current database-backed verification covers:

```text
- PostgreSQL Testcontainers startup
- Spring datasource integration
- Flyway migration lifecycle
- baseline migration execution
- flyway_schema_history creation
```

Database-backed tests may require:

```text
- PostgreSQL Testcontainers
- Docker-compatible runtime
- Flyway
- datasource configuration
- real SQL behavior
```

Rule:

```text
Correctness that depends on PostgreSQL transaction or concurrency behavior must be validated against real PostgreSQL.
```

---

## 8. Testcontainers Baseline

Automated database integration tests use PostgreSQL Testcontainers.

The test profile is:

```text
test
```

Testcontainers is used to avoid dependence on local Compose PostgreSQL during automated tests.

Rules:

```text
- automated DB-backed tests should not require local Compose PostgreSQL
- local Compose PostgreSQL is for manual dev runtime
- Testcontainers PostgreSQL is for automated test runtime
- PostgreSQL correctness behavior should not be replaced by unrelated in-memory databases
```

For rootless Podman environments, the Docker-compatible socket may need to be configured outside the test code.

That environment setup is documented separately.

---

## 9. Current Verification Coverage

Current automated verification covers:

```text
- web application startup
- bootstrap HTTP routing
- request body validation errors
- request parameter validation errors
- malformed JSON errors
- bad request exceptions
- server-side invariant violation response handling
- Flyway migration history creation
- baseline migration execution
- Maven Surefire test discovery
```

Current automated verification intentionally does not cover:

```text
- reservation capacity correctness
- order idempotency
- payment interpretation
- final outcome resolution
- business persistence behavior
- runtime privileges on real business tables
```

Reason:

```text
Business behavior belongs to Construction slices.
```

---

## 10. Constraints for Implementation Planning

Implementation planning must identify validation needs at planning level.

For each slice, planning should state:

```text
- what must be proven
- what pressure must be simulated
- what failure must be prevented
- what observable result confirms correctness
- whether validation is domain-only, application-level, web-level, DB-backed, or concurrency-backed
```

Implementation planning must not define:

```text
- exact test method names
- exact thread counts
- exact latch mechanics
- exact SQL assertions
- full fixture code
```

Unless a specific mechanism is part of the chosen correctness strategy, exact mechanics belong to implementation execution.

---

## 11. Slice Validation Guidance

A slice should use the narrowest test type that proves the required behavior.

Guidance:

```text
domain-only rule:
    use focused tests when no Spring or DB behavior is required

application orchestration:
    use application-level tests when use-case behavior is central

web/API behavior:
    use web integration tests when HTTP behavior is exposed

persistence behavior:
    use DB-backed integration tests when durable state matters

transaction/concurrency behavior:
    use real PostgreSQL integration tests when database isolation or atomicity matters
```

For concurrency-dominated persistence correctness, fake repositories are not enough.

---

## 12. Must Preserve

```text
./mvnw test remains the standard verification command.

Web-only tests remain independent from database infrastructure.

DB-backed tests use real PostgreSQL through Testcontainers.

Correctness involving PostgreSQL behavior is not validated using an unrelated in-memory database.

Bootstrap tests remain neutral and must not become business behavior tests.

Slice tests are added only when slice implementation introduces behavior requiring validation.
```

---

## 13. Must Not Assume

```text
Do not assume all tests should start the full application.

Do not assume all tests require PostgreSQL.

Do not assume web tests should use the database by default.

Do not assume DB correctness can be proven by mocks.

Do not assume concurrency correctness can be proven by single-threaded tests only.

Do not assume implementation planning should write final test code.
```

---

## 14. Source References

```text
project-state.md
pom.xml
src/test/java/com/edge/checkout/
docs/setup/testcontainers-podman.md
docs/setup/testcontainers-podman-troubleshooting.md
```

---

## 15. Final Rule

```text
Testing must preserve clear verification boundaries: web-only tests stay infrastructure-free, DB-backed correctness uses real PostgreSQL, and slice validation is planned before implementation execution writes final tests.
```
