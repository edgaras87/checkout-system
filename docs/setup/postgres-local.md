# PostgreSQL Local Service

This document explains how to run PostgreSQL locally for the project.

PostgreSQL is used as the local persistent state service.

At this stage, PostgreSQL is only defined as an infrastructure service.
Database roles, schemas, privileges, migrations, and application connection are defined later.

---

## Runtime Note

Commands below use Docker syntax.

If using Podman, replace:

```bash
docker
```

with:

```bash
podman
```

Example:

```bash
docker compose up -d postgres
```

becomes:

```bash
podman compose up -d postgres
```

---

## Environment File

The project includes:

```text
.env.example
```

This file is a template for local PostgreSQL environment variables.

To create a local environment file:

```bash
cp .env.example .env
```

Compose automatically reads `.env` from the project root.

The local `.env` file should not be committed.

This project also defines default values in `compose.yaml`, so creating `.env` is optional unless local values need to be changed.

Current PostgreSQL service variables:

```text
POSTGRES_PORT
POSTGRES_DB
POSTGRES_ROOT_USER
POSTGRES_ROOT_PASSWORD
```

---

## Start PostgreSQL

From the project root:

```bash
docker compose up -d postgres
```

---

## Check Running Containers

```bash
docker ps
```

Expected container:

```text
checkout-system-postgres
```

---

## View Logs

```bash
docker logs checkout-system-postgres
```

---

## Connect with psql

```bash
docker exec -it checkout-system-postgres psql -U postgres_root -d postgres
```

Inside `psql`, test the connection:

```sql
SELECT version();
```

Exit:

```sql
\q
```

The current local connection uses the PostgreSQL bootstrap user created by the container.

Project-specific database roles are defined later in Service Constraints.

---

## Data Storage

PostgreSQL data is stored in a named volume managed by the container runtime.

The volume is defined in `compose.yaml` as:

```text
checkout-system-postgres-data
```

The actual physical location is not inside the project directory.
It depends on the container runtime.

Compose may prefix the runtime volume name with the project name, for example:

```text
checkout-system_checkout-system-postgres-data
```

For rootless Podman on Linux, the data may be stored under:

```text
~/.local/share/containers/storage/volumes/
```

To inspect the volume:

```bash
docker volume inspect checkout-system_checkout-system-postgres-data
```

If using Podman, replace `docker` with `podman`.

---

## Stop PostgreSQL

```bash
docker compose stop postgres
```

---

## Remove PostgreSQL Container

```bash
docker compose down
```

This removes the container and network, but keeps the named volume.

---

## Remove Local PostgreSQL Data

Use this only when you intentionally want to delete local database data:

```bash
docker compose down -v
```

This removes the container, network, and named volume.

---

## Current Boundary

Included now:

```text
- PostgreSQL service
- local container execution
- service-level environment variables
- local persistent volume
- bootstrap PostgreSQL user for local service initialization
```

Not included yet:

```text
- project database role model
- schemas
- privileges
- migrations
- application database connection
- Spring Boot configuration
- domain tables
```
