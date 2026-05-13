# PostgreSQL Local Service

This document explains how to run PostgreSQL locally for the project.

PostgreSQL is used as the local persistent state service.

The service is now defined with local database constraints:

```text
- bootstrap PostgreSQL role
- bootstrap database
- project database
- project database roles
- application schema
- runtime privilege boundary
```

Application bootstrap and business schema migrations are defined later.

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

This file is a template for local PostgreSQL service environment variables.

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

Current local defaults:

```text
POSTGRES_PORT=5432
POSTGRES_DB=postgres
POSTGRES_ROOT_USER=postgres_root
POSTGRES_ROOT_PASSWORD=postgres_root_password
```

Important:

```text
POSTGRES_DB is the bootstrap database created by the PostgreSQL container.
It is not the project database used by the application.
```

The project database is created later by the initialization scripts:

```text
Project database: checkout_system
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

Project-specific database roles and the project database are defined through the service constraint initialization scripts.

---

## Apply Database Service Constraints

Database service constraints are defined in:

```text
docs/system/database-role-model.md
```

They are applied locally through initialization scripts in:

```text
db/init/
```

Expected script order:

```text
01-create-roles.sql
02-create-database.sql
03-grant-database-access.sql
04-create-schema.sql
05-grant-runtime-privileges.sql
06-verify-database-model.sql
```

These scripts establish:

```text
- project database roles
- project database creation
- project database ownership
- application schema ownership
- runtime access boundaries
- verification checks
```

---

### 1. Create Project Roles

```bash
docker exec -i checkout-system-postgres \
  psql -U postgres_root -d postgres \
  < db/init/01-create-roles.sql
```

---

### 2. Create Project Database

```bash
docker exec -i checkout-system-postgres \
  psql -U postgres_root -d postgres \
  < db/init/02-create-database.sql
```

Expected project database:

```text
checkout_system
```

---

### 3. Grant Database Access

```bash
docker exec -i checkout-system-postgres \
  psql -U postgres_root -d postgres \
  < db/init/03-grant-database-access.sql
```

---

### 4. Create Application Schema

```bash
docker exec -i checkout-system-postgres \
  psql -U postgres_root -d checkout_system \
  < db/init/04-create-schema.sql
```

---

### 5. Grant Runtime Privileges

```bash
docker exec -i checkout-system-postgres \
  psql -U checkout_migrator -d checkout_system \
  < db/init/05-grant-runtime-privileges.sql
```

---

### 6. Verify Database Model

```bash
docker exec -i checkout-system-postgres \
  psql -U postgres_root -d checkout_system \
  < db/init/06-verify-database-model.sql
```

Expected ownership:

```text
checkout_system database:
    owner: checkout_admin

app schema:
    owner: checkout_migrator
```

Expected runtime boundary:

```text
checkout_runtime:
    can use application data
    cannot modify database structure
```

Expected result summary:

```text
project roles exist
project roles have limited capabilities
checkout_admin owns checkout_system
checkout_migrator owns app
checkout_runtime can use app
checkout_runtime cannot create objects in app
future tables created by checkout_migrator grant runtime data access to checkout_runtime
future sequences created by checkout_migrator grant runtime sequence access to checkout_runtime
checkout_runtime cannot delegate those privileges to other roles
```

This verification does not prove:

```text
- application tables exist
- application sequences exist
- runtime privileges on actual application tables
- runtime privileges on actual application sequences
- indexes or constraints exist
- persistence behavior works
```

Those checks belong later, after real application migrations exist.

Detailed output interpretation:

```text
internal/preparation/postgresql-database-model-verification.md
```

If using Podman, replace `docker` with `podman`.

---

## Reinitialize After Role Model Changes

If the PostgreSQL volume was created before the current role model, recreate the local database volume:

```bash
docker compose down -v
docker compose up -d postgres
```

Use this when changing:

```text
- POSTGRES_DB
- POSTGRES_ROOT_USER
- POSTGRES_ROOT_PASSWORD
- project database name
- database roles
- database initialization scripts
```

The named volume stores initialized PostgreSQL state. Environment changes do not fully apply to an already-initialized volume.

Important:

```text
POSTGRES_DB should remain the bootstrap database.
For this project, the expected local value is postgres.
```

Do not set:

```text
POSTGRES_DB=checkout_system
```

The project database is created by:

```text
db/init/02-create-database.sql
```

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
- bootstrap database for local service initialization
- project database role model
- project database creation
- application schema creation
- runtime privilege boundary
- database model verification
```

Not included yet:

```text
- application business tables
- inventory reservation schema
- order schema
- payment schema
- checkout outcome schema
- Spring Boot configuration
- Flyway business migrations
- application database connection
```
