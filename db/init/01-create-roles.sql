-- Purpose:
--   Create project database roles if they do not already exist.
--
-- Run as:
--   postgres superuser
--
-- Notes:
--   Replace passwords before real use.
--   For local development, these may match values from .env.

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_roles
        WHERE rolname = 'checkout_admin'
    ) THEN
CREATE ROLE checkout_admin
    WITH LOGIN
    PASSWORD '<admin_password>';
END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM pg_roles
        WHERE rolname = 'checkout_migrator'
    ) THEN
CREATE ROLE checkout_migrator
    WITH LOGIN
    PASSWORD '<migrator_password>';
END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM pg_roles
        WHERE rolname = 'checkout_runtime'
    ) THEN
CREATE ROLE checkout_runtime
    WITH LOGIN
    PASSWORD '<runtime_password>';
END IF;
END
$$;
