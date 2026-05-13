-- Purpose:
--   Grant runtime access to application data without granting structural authority.
--
-- Run as:
--   checkout_migrator
--
-- Database context:
--   checkout_system
--
-- Important:
--   Default privileges apply only to future objects created by the role that
--   executes ALTER DEFAULT PRIVILEGES.
--
--   Therefore, this script must be run as checkout_migrator, and future
--   migrations must also run as checkout_migrator.

GRANT USAGE ON SCHEMA app
  TO checkout_runtime;

-- Existing tables, if any.
--
-- At this stage, this may affect zero tables because application tables are
-- intentionally not created during database bootstrap.
GRANT SELECT, INSERT, UPDATE, DELETE
  ON ALL TABLES IN SCHEMA app
  TO checkout_runtime;

-- Existing sequences, if any.
--
-- At this stage, this may affect zero sequences because application tables are
-- intentionally not created during database bootstrap.
GRANT USAGE, SELECT
  ON ALL SEQUENCES IN SCHEMA app
  TO checkout_runtime;

-- Future tables created by checkout_migrator.
ALTER DEFAULT PRIVILEGES
  IN SCHEMA app
  GRANT SELECT, INSERT, UPDATE, DELETE
  ON TABLES
  TO checkout_runtime;

-- Future sequences created by checkout_migrator.
ALTER DEFAULT PRIVILEGES
  IN SCHEMA app
  GRANT USAGE, SELECT
  ON SEQUENCES
  TO checkout_runtime;
