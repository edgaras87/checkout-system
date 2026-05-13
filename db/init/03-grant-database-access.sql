-- Purpose:
--   Define explicit database-level access for project roles.
--
-- Run as:
--   postgres superuser
--
-- Database context:
--   Can be run from any database.

REVOKE ALL ON DATABASE checkout_system FROM PUBLIC;

GRANT CONNECT ON DATABASE checkout_system TO checkout_admin;
GRANT CONNECT ON DATABASE checkout_system TO checkout_migrator;
GRANT CONNECT ON DATABASE checkout_system TO checkout_runtime;
