# Local Containers

---

## 1. Purpose

This document explains how local infrastructure services are expected to run for this project.

The project uses containers to run development infrastructure in a controlled and reproducible way.

This document covers:

```text
- local container runtime expectation
- Compose usage
- common operational commands
- Docker / Podman compatibility notes
```

It does not define the infrastructure services themselves.

---

## 2. Runtime Requirement

A Docker-compatible container runtime is required.

Supported options:

```text
- Docker
- Podman with Docker-compatible support
```

Docker is the default command style used in examples.

Podman may also be used when configured to behave compatibly with Docker tooling.

---

## 3. Compose

Local infrastructure is controlled through Compose from the repository root.

```text
compose.yaml
```

The Compose file is the project-level entry point for starting and stopping local infrastructure services.

At this stage, this document defines the container execution convention.

Concrete services are introduced separately.

---

## 4. Basic Commands

Start local infrastructure services:

```bash
docker compose up -d
```

Stop local infrastructure services:

```bash
docker compose down
```

View running services:

```bash
docker compose ps
```

View service logs:

```bash
docker compose logs
```

Follow service logs:

```bash
docker compose logs -f
```

Remove containers and volumes:

```bash
docker compose down -v
```

Use `down -v` carefully because it removes local persisted service data.

---

## 5. Podman Note

Podman can be used if it provides Docker-compatible behavior.

Depending on the local setup, this may require:

```text
- Podman Compose support
- a Docker-compatible Podman socket
- environment variables required by Docker-compatible tooling
```

For Podman-based usage, the equivalent command may be:

```bash
podman compose up -d
podman compose down
podman compose ps
podman compose logs
```

Some tools, especially integration test tooling, may expect access to a Docker-compatible socket.

When using Podman, make sure Docker-compatible tooling can access the container runtime before running tests or tools that depend on containers.

---

## 6. Repository Root Convention

Compose commands should be executed from the repository root.

```text
checkout-system/
    compose.yaml
    docs/
    internal/
```

Expected command location:

```bash
docker compose up -d
```

Run from:

```text
checkout-system/
```

This keeps paths, service names, volumes, and environment files predictable.

---

## 7. Operational Expectations

The local container environment should support:

```text
- starting infrastructure services
- stopping infrastructure services
- inspecting running services
- reading service logs
- removing local containers and volumes when needed
- supporting future integration tests through a Docker-compatible runtime
```

These expectations are environment-level.

They do not define application behavior or service-specific rules.

---

## 8. Boundary

This document describes how local infrastructure is run.

It does not define:

```text
- PostgreSQL service configuration
- database names
- database roles
- database schemas
- database privileges
- application datasource configuration
- Flyway migrations
- correctness rules
- responsibility areas
```

Those are defined in later setup and system documents.

---

## 9. Relationship to Setup Flow

This document belongs to the execution environment setup.

The project setup flow is:

```text
Execution environment
    ↓
Infrastructure services
    ↓
Service constraints
    ↓
Application bootstrap
```

This means:

```text
Local containers define how services run.
Infrastructure service docs define which services run.
Service constraint docs define the rules inside those services.
Application docs define how the application connects to them.
```

---

## 10. Completion Meaning

After this document exists, the project has a documented convention for local container execution.

The next step is to define concrete infrastructure services, starting with PostgreSQL.

---

# One-line Mental Model

```text
Containers provide the local place where project infrastructure runs.
```
