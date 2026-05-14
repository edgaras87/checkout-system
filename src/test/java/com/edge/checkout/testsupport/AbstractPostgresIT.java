package com.edge.checkout.testsupport;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

// @Testcontainers enables JUnit Jupiter lifecycle integration.
// @ServiceConnection handles datasource wiring only.
@Testcontainers
public abstract class AbstractPostgresIT {

  @Container
  @ServiceConnection
  static final PostgreSQLContainer postgres =
    new PostgreSQLContainer("postgres:16-alpine")
      // Explicit test credentials for readability in logs/debugging.
      // Not required: @ServiceConnection injects the container datasource for both Flyway and runtime in tests.
      .withDatabaseName("test_db")
      .withUsername("test_user")
      .withPassword("test_pass");
}