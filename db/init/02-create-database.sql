-- Purpose:
--   Create the project database if it does not already exist.
--
-- Run as:
--   postgres superuser
--
-- Important:
--   This is a psql script, not pure SQL.
--   PostgreSQL does not support CREATE DATABASE IF NOT EXISTS.
--   The \gexec command executes the generated CREATE DATABASE statement only
--   when the database does not already exist.

SELECT 'CREATE DATABASE checkout_system OWNER checkout_admin'
WHERE NOT EXISTS (
    SELECT 1
    FROM pg_database
    WHERE datname = 'checkout_system'
)\gexec
