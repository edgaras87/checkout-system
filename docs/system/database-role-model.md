# 📄 **Database Role Model**

---

## **1. Purpose**

Define the PostgreSQL authority model used by the project.

This document establishes the service constraints that control how the database may be accessed, changed, and used.

It ensures that:

```text
- database responsibilities are separated
- structural changes are controlled
- runtime access is restricted to data operations
- application execution cannot bypass database ownership boundaries
```

---

## **2. Core Principle**

```text
The application must be allowed to use the database,
but must not be allowed to define or modify database structure.
```

The database is treated as a controlled persistence service, not as an unrestricted storage box.

Structural authority and runtime data access must remain separate.

---

## **3. Database Roles**

The project uses the following database roles:

```text
postgres_root
checkout_admin
checkout_migrator
checkout_runtime
```

---

## **4. Role Responsibilities**

```text
postgres_root:
    - local PostgreSQL bootstrap/superuser role
    - created by the PostgreSQL container during local initialization
    - connects to the bootstrap database
    - creates project roles
    - creates the project database
    - used only during bootstrap and local administration

checkout_admin:
    - owns the project database
    - represents database-level ownership
    - manages database-level access boundaries

checkout_migrator:
    - owns the application schema
    - performs schema changes
    - creates and modifies database structure through migrations

checkout_runtime:
    - used by the running application
    - performs data operations only
    - must not modify database structure
```

---

## **5. Authority Boundaries**

The database authority model follows these boundaries:

```text
- bootstrap/superuser authority is limited to initialization and local administration
- database ownership does not imply application schema ownership
- schema ownership belongs to the migration role
- runtime access is limited to data operations
- application runtime must not own database structure
```

These boundaries prevent the running application from accidentally or silently changing the persistence model.

---

## **6. Database Identity Model**

The local PostgreSQL service has two distinct database identities:

```text
Bootstrap database:
    postgres
        created by: PostgreSQL container initialization
        used for: bootstrap connection and administration

Project database:
    checkout_system
        created by: db/init/02-create-database.sql
        owner: checkout_admin
        used for: application persistence
```

This means:

```text
POSTGRES_DB=postgres is the bootstrap database.
checkout_system is the project database.
```

The project database must not be created implicitly by Compose through `POSTGRES_DB`.

---

## **7. Ownership Model**

The ownership model is:

```text
Local bootstrap foundation:
    created by: PostgreSQL container
    bootstrap role: postgres_root
    bootstrap database: postgres

Project database:
    checkout_system
        created by: postgres_root
        owner: checkout_admin

Application schema:
    app
        created by: postgres_root
        owner: checkout_migrator

Future application tables:
    created by: checkout_migrator
    usable by: checkout_runtime

Application runtime:
    connects as: checkout_runtime
```

This means:

```text
postgres_root creates the project database, project schema, and privilege boundaries after container bootstrap.
checkout_admin owns the project database.
checkout_migrator owns the schema and future structure.
checkout_runtime uses the data.
```

---

## **8. Schema Model**

The application uses a dedicated schema:

```text
app
```

The `app` schema is the only schema intended for application-owned database objects.

The default `public` schema must not be used as an application escape hatch.

Expected rule:

```text
Application data objects belong in schema app.
```

Not in:

```text
public
```

---

## **9. Migration Authority Model**

Schema changes must be performed by:

```text
checkout_migrator
```

This role is responsible for controlled structural evolution.

Examples of structural changes:

```text
- creating tables
- altering tables
- creating indexes
- creating constraints
- creating sequences
- modifying schema-owned database objects
```

The migration role exists so that structural change is explicit, controlled, and separated from runtime application behavior.

---

## **10. Runtime Access Model**

The running application must connect as:

```text
checkout_runtime
```

The runtime role may perform data operations required by the application.

Allowed runtime operations:

```text
SELECT
INSERT
UPDATE
DELETE
```

Runtime may also use required sequences for generated identifiers:

```text
USAGE
SELECT
```

on sequences.

The runtime role must not perform structural operations.

Forbidden runtime operations:

```text
CREATE
ALTER
DROP
TRUNCATE
```

The runtime role must not own application tables, schemas, or database structure.

---

## **11. Minimum Safety Constraint**

```text
The runtime role must never be able to modify database structure.
```

This is the minimum safety guarantee of the database role model.

It protects the system from this failure mode:

```text
Application execution accidentally changes the persistence structure.
```

This matters because later correctness guarantees may depend on database structure being controlled and stable.

---

## **12. Privilege Boundary**

The intended privilege boundary is:

```text
checkout_migrator:
    - owns app schema
    - creates structure
    - modifies structure through migrations

checkout_runtime:
    - uses app schema
    - reads and writes application data
    - does not create or modify structure
```

The runtime role should receive privileges through explicit grants, not through ownership.

Expected grants for runtime:

```sql
GRANT USAGE ON SCHEMA app TO checkout_runtime;

GRANT SELECT, INSERT, UPDATE, DELETE
  ON ALL TABLES IN SCHEMA app
  TO checkout_runtime;

GRANT USAGE, SELECT
  ON ALL SEQUENCES IN SCHEMA app
  TO checkout_runtime;
```

Runtime should not receive:

```text
CREATE ON SCHEMA app
OWNERSHIP OF TABLES
OWNERSHIP OF SCHEMA
SUPERUSER
CREATEDB
CREATEROLE
```

---

## **13. Default Privileges Rule**

Default privileges must be configured so future objects created by the migration role are usable by the runtime role.

Important rule:

```text
Default privileges apply to future objects created by the role that defines them.
```

Therefore:

```text
checkout_migrator must define default privileges
checkout_migrator must create future application objects
```

This allows future migrated tables and sequences to be usable by the runtime role without granting structural authority.

---

## **14. Public Access Rule**

Default PostgreSQL public access must not be treated as part of the application authority model.

The project should avoid relying on implicit privileges granted through:

```text
PUBLIC
```

Expected rule:

```text
Access should be explicit.
```

Recommended constraints:

```text
- revoke unnecessary database privileges from PUBLIC
- revoke unnecessary schema privileges from PUBLIC
- do not use public schema for application objects
```

This prevents accidental access paths outside the defined role model.

---

## **15. Enforcement Model**

The role model is enforced through:

```text
- explicit role creation
- explicit project database creation
- explicit database ownership
- explicit schema ownership
- explicit grants
- restricted runtime privileges
- verification queries
```

Implemented through local initialization scripts:

```text
db/init/
    01-create-roles.sql
    02-create-database.sql
    03-grant-database-access.sql
    04-create-schema.sql
    05-grant-runtime-privileges.sql
    06-verify-database-model.sql
```

---

## **16. Verification Model**

The database role model must be verifiable.

Verification should confirm:

```text
- project roles exist
- roles do not have unintended superuser authority
- project database exists
- project database owner is checkout_admin
- app schema owner is checkout_migrator
- runtime role can use app schema
- runtime role cannot create or alter structure
```

Verification does not need to prove every future migration is correct.

It only needs to confirm that the service constraint model is applied.

---

## **17. What This Model Does Not Define**

This document does not define:

```text
- application tables
- business entities
- inventory reservation schema
- order schema
- payment schema
- checkout outcome schema
- JPA mappings
- repository interfaces
- application migrations for business features
```

Those belong to later construction work.

This document defines only the database authority boundary that future schema design must obey.

---

## **18. Summary**

```text
PostgreSQL container creates the local bootstrap database and bootstrap role.
postgres_root creates the project database, project schema, and privilege boundaries after container bootstrap.
checkout_admin owns the project database.
checkout_migrator owns and changes structure.
checkout_runtime uses data only.
```

The core constraint is:

```text
Runtime may operate on data,
but runtime must not control structure.
```

---

## **19. Final State**

```text
- bootstrap database is postgres
- project database is checkout_system
- database authority boundaries are defined
- database ownership is separated from schema ownership
- schema changes belong to the migration role
- runtime access is restricted to data operations
- application structure cannot be modified by runtime execution
- future application bootstrap has a clear database access model
```

---

# 🧠 One-line Mental Model

```text
The database is not just available — it is governed by explicit authority boundaries.
```
