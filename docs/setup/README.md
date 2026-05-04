# Local Setup

Local setup documentation for this project.

## Related Setup Guides

- [Local Containers](local-containers.md)
- [PostgreSQL Local Service](postgres-local.md)
- [Testcontainers with Podman](testcontainers-podman.md)
- [Testcontainers with Podman Troubleshooting](testcontainers-podman-troubleshooting.md)

## Local Application Startup

Manual local application startup should use the `dev` profile.

The application expects PostgreSQL to be available through the local setup defined in:

```text
docs/setup/postgres-local.md
```

The local environment file is based on:

```text
.env.example
```

Create a local environment file if one does not exist:

```bash
cp .env.example .env
```

Start PostgreSQL first:

```bash
podman compose up -d postgres
```

Then run the application with the `dev` profile:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

Expected startup signals:

```text
The following 1 profile is active: "dev"
Database: jdbc:postgresql://localhost:5432/checkout_system?currentSchema=app
Successfully validated 1 migration
Schema "app" is up to date
Tomcat started on port 8080
Started CheckoutSystemApplication
```

Stop the application with:

```text
Ctrl+C
```

A graceful shutdown after `Ctrl+C` is expected and is not a failure.

The `test` profile is reserved for automated tests.

## Tests

The standard project test command is:

```bash
./mvnw test
```

At this stage, this command runs the project verification baseline, including:

```text
- PostgreSQL Testcontainers integration tests
```

Testcontainers-based integration tests require a Docker-compatible container runtime.

Supported local runtime paths:

```text
- Docker, usually auto-detected
- Podman, with socket setup
```

Docker users usually do not need additional configuration.

Podman users should follow:

```text
docs/setup/testcontainers-podman.md
```

If Podman/Testcontainers fails because the runtime cannot be detected or reached, see:

```text
docs/setup/testcontainers-podman-troubleshooting.md
```

## Current Coverage

Setup instructions are added progressively as the project becomes executable.

Current setup coverage:

```text
- execution environment
- infrastructure services
- service constraints
- application bootstrap
- local application startup
- integration test runtime setup
- PostgreSQL integration test execution baseline
```

Expected future setup areas:

```text
- web smoke verification
- API error handling verification
- construction-phase runtime usage
```
