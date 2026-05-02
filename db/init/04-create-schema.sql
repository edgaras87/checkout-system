-- Purpose:
--   Create the application schema and assign schema ownership.
--
-- Run as:
--   postgres superuser
--
-- Database context:
--   checkout_system

CREATE SCHEMA IF NOT EXISTS app;

ALTER SCHEMA app OWNER TO checkout_migrator;

-- Prevent the default public schema from becoming an application escape hatch.
REVOKE CREATE ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM PUBLIC;
