# PostgreSQL Database Model Verification

---

## 1. Purpose

This document explains how to interpret the output of:

```text
db/init/06-verify-database-model.sql
```

The verification script checks the PostgreSQL database authority model created during service constraint setup.

It verifies:

```text
- project roles
- role capabilities
- database ownership
- schema ownership
- schema privileges
- default privileges for future application objects
```

It does not verify application tables or persistence behavior.

---

## 2. Verification Boundary

This verification belongs to the database bootstrap model.

It proves that PostgreSQL is prepared for future application migrations.

Included:

```text
- project roles exist
- project roles have limited capabilities
- project database has the expected owner
- application schema has the expected owner
- migration role can create schema objects
- runtime role can use the application schema
- runtime role cannot create schema objects
- future objects created by the migration role grant runtime access automatically
```

Not included:

```text
- application tables exist
- application sequences exist
- runtime privileges on actual application tables
- runtime privileges on actual application sequences
- indexes exist
- constraints exist
- persistence behavior works
```

Reason:

```text
application tables do not exist during database bootstrap
application migrations create them later
```

---

## 3. Verification Script

The verification script is:

```text
db/init/06-verify-database-model.sql
```

Run command:

```bash
docker exec -i checkout-system-postgres \
  psql -U postgres_root -d checkout_system \
  < db/init/06-verify-database-model.sql
```

If using Podman, replace:

```bash
docker
```

with:

```bash
podman
```

---

## 4. Output Sections

The script prints five result sections:

```text
=== 1. Project roles and role capabilities ===
=== 2. Database ownership ===
=== 3. Schema ownership ===
=== 4. Schema privileges ===
=== 5. Default privileges for future tables/sequences ===
```

Each section verifies one part of the database authority model.

---

## 5. Project Roles and Role Capabilities

Printed title:

```text
=== 1. Project roles and role capabilities ===
```

Output columns:

```text
rolname | rolsuper | rolcreatedb | rolcreaterole | rolcanlogin
```

Meaning:

```text
rolname:
    PostgreSQL role name.

rolsuper:
    Whether the role is a PostgreSQL superuser.
    Expected: false for all project roles.

rolcreatedb:
    Whether the role can create databases.
    Expected: false for all project roles.

rolcreaterole:
    Whether the role can create or modify roles.
    Expected: false for all project roles.

rolcanlogin:
    Whether the role can log in.
    Expected: true for checkout_admin, checkout_migrator, and checkout_runtime.
```

Expected interpretation:

```text
checkout_admin:
    login role
    owns the project database
    does not have superuser/database/role creation authority

checkout_migrator:
    login role
    owns the application schema
    creates application database objects through migrations
    does not have superuser/database/role creation authority

checkout_runtime:
    login role
    used by the running application
    can access application data
    cannot create schema objects
    does not have superuser/database/role creation authority
```

Correct example:

```text
checkout_admin    | f | f | f | t
checkout_migrator | f | f | f | t
checkout_runtime  | f | f | f | t
```

This means:

```text
project roles exist
project roles can log in
project roles do not have elevated PostgreSQL authority
```

---

## 6. Database Ownership

Printed title:

```text
=== 2. Database ownership ===
```

Output columns:

```text
database_name | database_owner
```

Meaning:

```text
database_name:
    Project database being verified.

database_owner:
    Role that owns the project database.
```

Expected interpretation:

```text
checkout_system is owned by checkout_admin
```

This means:

```text
checkout_admin owns the project database boundary
checkout_migrator does not own the database
checkout_runtime does not own the database
```

Correct example:

```text
checkout_system | checkout_admin
```

---

## 7. Schema Ownership

Printed title:

```text
=== 3. Schema ownership ===
```

Output columns:

```text
schema_name | schema_owner
```

Meaning:

```text
schema_name:
    Schema being verified.

schema_owner:
    Role that owns the schema.
```

Expected interpretation:

```text
app is owned by checkout_migrator
public is not owned by checkout_runtime
```

Correct example:

```text
app    | checkout_migrator
public | pg_database_owner
```

The `public` schema may appear as:

```text
pg_database_owner
```

That is acceptable.

The important project-owned schema is:

```text
app
```

This means:

```text
checkout_migrator owns application schema evolution
checkout_runtime does not own the application schema
```

---

## 8. Schema Privileges

Printed title:

```text
=== 4. Schema privileges ===
```

Output columns:

```text
grantee | privilege_type | has_privilege
```

Meaning:

```text
grantee:
    Role being checked.

privilege_type:
    Schema privilege being checked.

has_privilege:
    Whether the role has that privilege on schema app.
```

Expected interpretation:

```text
checkout_migrator:
    USAGE  = true
    CREATE = true

checkout_runtime:
    USAGE  = true
    CREATE = false
```

Correct example:

```text
checkout_migrator | CREATE | t
checkout_migrator | USAGE  | t
checkout_runtime  | CREATE | f
checkout_runtime  | USAGE  | t
```

This means:

```text
checkout_migrator can use the app schema
checkout_migrator can create objects in the app schema

checkout_runtime can use the app schema
checkout_runtime cannot create objects in the app schema
```

Core boundary:

```text
migrator owns schema changes
runtime only uses application data
```

---

## 9. Default Privileges for Future Tables/Sequences

Printed title:

```text
=== 5. Default privileges for future tables/sequences ===
```

Output columns:

```text
owner_role | schema_name | object_type | grantee | privilege_type | is_grantable
```

Meaning:

```text
owner_role:
    Role whose future objects are affected by the default privilege rule.

schema_name:
    Schema where the default privilege rule applies.

object_type:
    Type of future object affected by the rule.
    Expected here: table or sequence.

grantee:
    Role that receives privileges on future objects.

privilege_type:
    Privilege granted on future objects.

is_grantable:
    Whether the grantee can grant that privilege to other roles.
    Expected: false.
```

Expected key relationship:

```text
owner_role = checkout_migrator
schema_name = app
grantee = checkout_runtime
```

This proves that default privileges are attached to the role that will create future application objects.

Expected future table privileges:

```text
SELECT
INSERT
UPDATE
DELETE
```

Expected future sequence privileges:

```text
USAGE
SELECT
```

Correct example:

```text
checkout_migrator | app | sequence | checkout_runtime | SELECT | f
checkout_migrator | app | sequence | checkout_runtime | USAGE  | f
checkout_migrator | app | table    | checkout_runtime | DELETE | f
checkout_migrator | app | table    | checkout_runtime | INSERT | f
checkout_migrator | app | table    | checkout_runtime | SELECT | f
checkout_migrator | app | table    | checkout_runtime | UPDATE | f
```

This means:

```text
when Flyway runs as checkout_migrator
and creates future application tables/sequences in app
checkout_runtime automatically receives runtime access
```

The `is_grantable` value should be:

```text
false
```

This means:

```text
checkout_runtime receives the privilege
but checkout_runtime cannot grant that privilege to other roles
```

---

## 10. Why Default Privileges Matter

Default privileges prevent repeated manual grants after every migration.

Without default privileges, each future table migration would require manual grants such as:

```sql
GRANT SELECT, INSERT, UPDATE, DELETE
ON app.some_table
TO checkout_runtime;
```

With default privileges, future tables created by `checkout_migrator` in `app` automatically grant runtime access to `checkout_runtime`.

The same idea applies to sequences.

This matters because runtime inserts may need sequence access when tables use generated identifiers.

---

## 11. Important Rule

Default privileges apply only to objects created by the role recorded as:

```text
owner_role
```

For this project, the expected owner role is:

```text
checkout_migrator
```

Therefore, future migrations must run as:

```text
checkout_migrator
```

If future tables are created by another role, such as:

```text
postgres_root
checkout_admin
```

then these default privileges will not automatically apply.

That would break the expected runtime access model.

---

## 12. What Correct Verification Means

A correct verification result means:

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

The core meaning is:

```text
database authority is separated before application tables exist
```

---

## 13. What Correct Verification Does Not Mean

Correct database model verification does not mean:

```text
application persistence is complete
application tables exist
runtime privileges on actual tables have been verified
runtime privileges on actual sequences have been verified
JPA mappings are valid
Flyway migrations are complete
application database behavior works
```

Those checks belong later, after real application migrations exist.

---

## 14. Future Verification Boundary

Later, after real application migrations exist, verification must move from authority model checks to application schema checks.

Future verification should check:

```text
- expected application tables exist
- expected application sequences exist
- expected constraints exist
- expected indexes exist
- checkout_runtime has required privileges on real tables
- checkout_runtime has required privileges on real sequences
- checkout_runtime cannot create, alter, or drop schema objects
```

That future verification should belong to application schema verification or integration tests, not to this database bootstrap verification document.

---

## 15. One-line Mental Model

```text
This verification proves that PostgreSQL authority is prepared correctly before application tables exist.
```
