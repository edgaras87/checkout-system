-- Purpose:
--   Create project database roles if they do not already exist.
--
-- Run as:
--   postgres_root / PostgreSQL superuser
--
-- Database context:
--   postgres
--
-- Local development note:
--   Passwords are explicit local-development placeholders.
--   Change them in real environments.
--
-- Boundary:
--   This script creates authority identities only.
--   It does not create the project database, schema, tables, or privileges.

DO $$
  BEGIN
    IF NOT EXISTS (
      SELECT 1
      FROM pg_roles
      WHERE rolname = 'checkout_admin'
    ) THEN
      CREATE ROLE checkout_admin
        WITH LOGIN
        PASSWORD 'checkout_admin_password';
    END IF;

    IF NOT EXISTS (
      SELECT 1
      FROM pg_roles
      WHERE rolname = 'checkout_migrator'
    ) THEN
      CREATE ROLE checkout_migrator
        WITH LOGIN
        PASSWORD 'checkout_migrator_password';
    END IF;

    IF NOT EXISTS (
      SELECT 1
      FROM pg_roles
      WHERE rolname = 'checkout_runtime'
    ) THEN
      CREATE ROLE checkout_runtime
        WITH LOGIN
        PASSWORD 'checkout_runtime_password';
    END IF;
  END
$$;
