# Persistence Baseline

Status: accepted  
Version: v1  
Scope: checkout-system implementation planning baseline

---

## 0. Baseline Change Rule

This document changes only when a project-wide persistence planning constraint changes.

It should not be updated merely because a slice adds persistence implementation details.

Slice-specific persistence decisions belong in that slice's implementation-plan.md, validation-record.md, or completion-record.md.

A completed slice may update this baseline only when it establishes a reusable persistence rule, constraint, or capability that future slices must preserve.

---

## 0.1 Change Log

```text
v1:
    Created from Preparation Phase persistence baseline before first Construction slice.
```

---

## 1. Purpose

This document records the current persistence baseline that implementation planning must preserve.

It defines the established database, migration, schema, and authority constraints for the project.

It answers:

```text
What persistence constraints already exist before slice implementation planning begins?
```

This document does not define slice-specific persistence design.

It does not define business tables.

It does not define final schema.

It does not define SQL statements.

---

## 2. Scope

This baseline covers:

```text
- PostgreSQL persistence authority
- project database
- application schema
- database roles
- migration authority
- runtime authority
- Flyway migration model
- business persistence introduction rules
- test persistence expectations
```

This baseline does not cover:

```text
- reservation table design
- order table design
- payment table design
- final outcome table design
- slice-specific migrations
- repository design
- JPA entity design
- final SQL implementation
```

---

## 3. Current Baseline

```text
Persistence service: PostgreSQL
PostgreSQL version target: 16
Project database: checkout_system
Application schema: app
Migration tool: Flyway
Business tables: not yet defined
Baseline migration: exists
Business schema migrations: deferred to Construction slices
```

The project uses PostgreSQL as the durable persistence service.

The project already defines a database authority model.

The application schema is:

```text
app
```

The project database is:

```text
checkout_system
```

The bootstrap PostgreSQL database is:

```text
postgres
```

---

## 4. Database Authority Model

The current role model is:

```text
postgres_root:
    bootstrap/local administrative role

checkout_admin:
    project database owner

checkout_migrator:
    schema owner and migration authority

checkout_runtime:
    runtime application role
```

Authority boundaries:

```text
postgres_root:
    may bootstrap local PostgreSQL setup

checkout_admin:
    owns the project database

checkout_migrator:
    owns schema structure changes through Flyway

checkout_runtime:
    uses application data at runtime
```

The runtime role must not own schema structure.

The runtime role must not perform DDL.

The runtime role must not replace the migrator role.

---

## 5. Migration Baseline

Schema changes must be introduced through Flyway migrations.

Flyway runs with migration authority.

The migration role is:

```text
checkout_migrator
```

The runtime application datasource uses:

```text
checkout_runtime
```

Rules:

```text
- business tables must be introduced by real Construction slice migrations
- schema changes must be migration-driven
- application startup must not create business tables
- Hibernate ddl-auto must not be used to create or update schema
- runtime code must not perform structural database changes
```

Allowed:

```text
A Construction slice may add a Flyway migration for required business persistence.
```

Not allowed:

```text
A Construction slice may not rely on Hibernate auto-DDL to create business tables.
```

Not allowed:

```text
Runtime application code may not create, alter, or drop database objects.
```

---

## 6. Schema Baseline

The application schema is:

```text
app
```

Business objects for dev/prod must belong to the application schema unless a later explicit architectural decision changes this.

The public schema is not the default location for business objects.

Rules:

```text
- dev/prod business persistence should use the app schema
- public schema should not become the accidental business schema
- schema ownership must remain aligned with migration authority
```

The automated test profile may use a simplified schema model when intentionally defined by test configuration.

That test simplification must not redefine dev/prod persistence authority.

---

## 7. Runtime Access Baseline

The runtime application role is:

```text
checkout_runtime
```

Runtime access means:

```text
- application data access
- application data mutation
- no schema ownership
- no DDL authority
```

Implementation planning must preserve the distinction between:

```text
structural authority:
    migration role

runtime access:
    application runtime role
```

A slice implementation may require new runtime privileges on new business tables.

Those privileges must be granted intentionally through migration or setup flow.

Runtime table privilege verification is deferred until real business tables exist.

---

## 8. Business Persistence Introduction Rule

Business persistence does not exist yet.

Current state:

```text
- no reservation business tables
- no order business tables
- no payment business tables
- no checkout outcome business tables
```

Business persistence must be introduced only when required by a Construction slice.

For each slice-driven persistence addition, implementation planning must identify:

```text
- what state must be authoritative
- what state must be durable
- what state must be transactionally consistent
- what state must be visible to validation
```

Implementation planning must not jump directly to final schema.

It may state persistence responsibilities.

It must not define final SQL, final table layout, or final entity mappings.

---

## 9. Transaction and Concurrency Baseline

PostgreSQL may be used as an authoritative concurrency control participant when correctness requires it.

For concurrency-sensitive slices, implementation planning must identify:

```text
- authoritative decision boundary
- atomic state transition requirements
- stale read/write patterns to avoid
- concurrent decision behavior
- transaction responsibility
```

For reservation capacity correctness, planning must not assume that application-memory checks are sufficient.

Correctness-sensitive capacity decisions must survive concurrent requests against the same durable state.

---

## 10. Constraints for Implementation Planning

Implementation planning must preserve:

```text
- PostgreSQL as the persistence service
- checkout_system as the project database
- app as the application schema for dev/prod business objects
- Flyway as the schema migration mechanism
- checkout_migrator as structural authority
- checkout_runtime as runtime authority
- separation between DDL authority and runtime access
- migration-driven business persistence
- real PostgreSQL validation for persistence-sensitive correctness
```

Implementation planning must not assume:

```text
- business tables already exist
- runtime role may create tables
- Hibernate may generate production schema
- public schema is acceptable by default for business objects
- in-memory persistence is sufficient for correctness slices involving durable state
- fake repositories are sufficient for database concurrency correctness
```

---

## 11. Must Preserve

```text
Structural database authority belongs to migration flow.

Runtime application access remains separated from schema ownership.

Business persistence is introduced through slice-driven migrations.

Database-backed correctness must be validated against PostgreSQL behavior when PostgreSQL participates in enforcement.

Application Bootstrap remains free of business tables.

The baseline migration remains neutral.
```

---

## 12. Must Not Assume

```text
Do not assume reservation persistence already exists.

Do not assume capacity tables already exist.

Do not assume order/payment/outcome persistence already exists.

Do not assume runtime DDL is allowed.

Do not assume Hibernate ddl-auto may create schema.

Do not assume implementation planning may define final SQL.

Do not assume test simplifications redefine dev/prod persistence authority.
```

---

## 13. Source References

```text
project-state.md
docs/system/database-role-model.md
docs/setup/postgres-local.md
db/init/
src/main/resources/application.yaml
src/main/resources/application-dev.yaml
src/main/resources/application-test.yaml
src/main/resources/db/migration/
```

---

## 14. Final Rule

```text
Persistence planning must preserve PostgreSQL authority boundaries, migration-driven schema ownership, and runtime DML-only access while introducing business persistence only through real Construction slice needs.
```
