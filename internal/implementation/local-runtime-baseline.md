# Local Runtime Baseline

Status: accepted  
Version: v1  
Scope: checkout-system implementation planning baseline

---

## 0. Baseline Change Rule

This document changes only when a project-wide local runtime, profile, orchestration, or environment planning constraint changes.

It should not be updated merely because a slice adds business behavior that can run locally.

Slice-specific runtime decisions belong in that slice's implementation-plan.md, validation-record.md, or completion-record.md.

A completed slice may update this baseline only when it establishes a reusable runtime rule, environment rule, or local execution capability that future slices must preserve.

---

## 0.1 Change Log

```text
v1:
    Created from Preparation Phase local runtime baseline before first Construction slice.
```

---

## 1. Purpose

This document records the current local runtime baseline that implementation planning must preserve.

It defines the established local execution model, profile behavior, container assumptions, and runtime verification boundaries.

It answers:

```text
What local runtime assumptions already exist before slice implementation planning begins?
```

This document does not define production deployment.

It does not define CI infrastructure.

It does not define slice-specific runtime behavior.

---

## 2. Scope

This baseline covers:

```text
- local orchestration model
- Compose entry point
- PostgreSQL local service
- dev profile runtime
- test profile runtime
- Docker-compatible runtime assumptions
- environment file usage
- manual startup verification boundary
```

This baseline does not cover:

```text
- production deployment
- cloud infrastructure
- Kubernetes
- CI pipeline design
- release process
- monitoring
- logging architecture
```

---

## 3. Current Local Runtime Baseline

Local orchestration entry point:

```text
compose.yaml
```

Local infrastructure service:

```text
PostgreSQL
```

Runtime model:

```text
Docker-compatible container runtime
```

Supported local runtime style:

```text
Docker or Podman with Docker-compatible behavior
```

Local PostgreSQL is containerized and managed through Compose.

---

## 4. Local PostgreSQL Baseline

Local PostgreSQL service:

```text
postgres:16
```

Container role:

```text
local persistent state service
```

Persistent storage model:

```text
local named volume
```

Initialization scripts:

```text
db/init/
```

Local PostgreSQL setup establishes:

```text
- bootstrap database
- project database
- database roles
- application schema
- role privileges
- database authority verification
```

---

## 5. Profile Baseline

Manual local runtime profile:

```text
dev
```

Automated test profile:

```text
test
```

Rules:

```text
dev profile:
    used for manual local application startup

test profile:
    used for automated tests
```

The dev profile expects local runtime services where needed.

The test profile must support automated verification without requiring local Compose PostgreSQL.

---

## 6. Dev Runtime Boundary

Manual local startup command:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

Dev runtime expects:

```text
- local Compose PostgreSQL is available
- checkout_system database exists
- app schema exists
- checkout_runtime is used by application datasource
- checkout_migrator is used by Flyway
- environment values are available through .env or defaults
```

Manual dev verification currently includes:

```text
- application starts with dev profile
- application connects to local PostgreSQL
- Flyway baseline migration applies
- GET /ping returns pong
```

---

## 7. Test Runtime Boundary

Automated test runtime uses:

```text
test profile
```

DB-backed automated tests use:

```text
PostgreSQL Testcontainers
```

Rules:

```text
- automated tests should not require local Compose PostgreSQL
- DB-backed tests should use Testcontainers PostgreSQL
- web-only tests should not require Docker, Podman, PostgreSQL, Flyway, or JPA
```

The test runtime is separate from manual dev runtime.

---

## 8. Environment File Baseline

The project uses:

```text
.env.example
```

Local developers may create:

```text
.env
```

Rule:

```text
.env must not be committed.
```

Environment values may include:

```text
POSTGRES_PORT
POSTGRES_DB
POSTGRES_ROOT_USER
POSTGRES_ROOT_PASSWORD
DB_HOST
DB_PORT
DB_NAME
DB_SCHEMA
DB_MIGRATOR_USER
DB_MIGRATOR_PASS
DB_RUNTIME_USER
DB_RUNTIME_PASS
```

Implementation planning must not require committing local secrets or machine-specific values.

---

## 9. Docker-Compatible Runtime Baseline

The local runtime assumes a Docker-compatible container environment.

For Docker, standard Docker Compose behavior is expected.

For rootless Podman, a Docker-compatible socket may be required for Testcontainers.

The Podman socket setup is documented separately.

Rules:

```text
- implementation planning must not depend on Docker-only behavior unless justified
- implementation planning must not require local Compose services for automated tests
- implementation planning must not embed machine-specific runtime assumptions into application code
```

---

## 10. Constraints for Implementation Planning

Implementation planning must preserve:

```text
- Compose as local orchestration entry point
- PostgreSQL as local persistent state service
- dev profile for manual local runtime
- test profile for automated tests
- Testcontainers for automated PostgreSQL integration tests
- .env-based local configuration support
- separation between dev runtime and test runtime
```

Implementation planning must not assume:

```text
- production deployment exists
- CI deployment exists
- local Compose PostgreSQL is available during automated tests
- Testcontainers should use local Compose PostgreSQL
- web-only tests should require container runtime
- local machine-specific paths should be hardcoded
```

---

## 11. Must Preserve

```text
Manual dev runtime uses local Compose PostgreSQL.

Automated DB-backed tests use Testcontainers PostgreSQL.

Web-only tests remain independent from local infrastructure.

Local environment values remain externalized.

The project remains compatible with Docker-compatible container runtimes.

Runtime setup documentation remains the place for local machine/runtime instructions.
```

---

## 12. Must Not Assume

```text
Do not assume production runtime exists.

Do not assume Kubernetes exists.

Do not assume CI deployment exists.

Do not assume local Compose PostgreSQL is used by automated tests.

Do not assume Docker-specific behavior when Docker-compatible behavior is enough.

Do not assume implementation planning should change local runtime setup unless the slice requires it.
```

---

## 13. Source References

```text
project-state.md
compose.yaml
.env.example
docs/setup/README.md
docs/setup/local-containers.md
docs/setup/postgres-local.md
docs/setup/testcontainers-podman.md
docs/setup/testcontainers-podman-troubleshooting.md
src/main/resources/application-dev.yaml
src/main/resources/application-test.yaml
```

---

## 14. Final Rule

```text
Local runtime planning must preserve the separation between dev runtime, test runtime, and web-only verification while keeping local infrastructure externalized through Compose and Docker-compatible tooling.
```
