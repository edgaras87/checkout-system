-- Purpose:
--   Verify the database role, ownership, schema, and privilege model.
--
-- Run as:
--   postgres superuser
--
-- Database context:
--   checkout_system
--
-- Boundary:
--   This script verifies the database bootstrap model.
--
--   It does not require application tables to exist.
--   Application tables are created later by application migrations.
--   Runtime privileges on actual application tables are verified later,
--   after real application migrations create real tables.

\echo ''
\echo '=== 1. Project roles and role capabilities ==='

-- Verify project roles and role capabilities.
SELECT
  rolname,
  rolsuper,
  rolcreatedb,
  rolcreaterole,
  rolcanlogin
FROM pg_roles
WHERE rolname IN (
                  'checkout_admin',
                  'checkout_migrator',
                  'checkout_runtime'
  )
ORDER BY rolname;

\echo ''
\echo '=== 2. Database ownership ==='

-- Verify database ownership.
SELECT
  datname AS database_name,
  pg_get_userbyid(datdba) AS database_owner
FROM pg_database
WHERE datname = 'checkout_system';

\echo ''
\echo '=== 3. Schema ownership ==='

-- Verify schema ownership.
SELECT
  nspname AS schema_name,
  nspowner::regrole AS schema_owner
FROM pg_namespace
WHERE nspname IN ('app', 'public')
ORDER BY nspname;

\echo ''
\echo '=== 4. Schema privileges ==='

-- Verify schema privileges.
--
-- PostgreSQL does not provide information_schema.schema_privileges.
-- Use has_schema_privilege to verify schema-level access explicitly.
SELECT
  role_name AS grantee,
  privilege_type,
  has_schema_privilege(role_name, 'app', privilege_type) AS has_privilege
FROM (
       VALUES
         ('checkout_migrator'),
         ('checkout_runtime')
     ) AS roles(role_name)
       CROSS JOIN (
  VALUES
    ('USAGE'),
    ('CREATE')
) AS privileges(privilege_type)
ORDER BY grantee, privilege_type;

\echo ''
\echo '=== 5. Default privileges for future tables/sequences ==='

-- Verify default privileges for future tables/sequences.
--
-- These are the important object-privilege checks at this stage because
-- future application tables and sequences will be created later by
-- checkout_migrator.
SELECT
  defaclrole::regrole AS owner_role,
  defaclnamespace::regnamespace AS schema_name,
  CASE defaclobjtype
    WHEN 'r' THEN 'table'
    WHEN 'S' THEN 'sequence'
    WHEN 'f' THEN 'function'
    WHEN 'T' THEN 'type'
    WHEN 'n' THEN 'schema'
    ELSE defaclobjtype::text
    END AS object_type,
  acl.grantee::regrole AS grantee,
  acl.privilege_type,
  acl.is_grantable
FROM pg_default_acl
       CROSS JOIN LATERAL aclexplode(defaclacl) AS acl
WHERE defaclnamespace = 'app'::regnamespace
ORDER BY
  defaclrole::regrole::text,
  defaclobjtype,
  acl.grantee::regrole::text,
  acl.privilege_type;