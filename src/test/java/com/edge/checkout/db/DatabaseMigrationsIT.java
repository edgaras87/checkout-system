package com.edge.checkout.db;

import com.edge.checkout.testsupport.AbstractDbIT;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DatabaseMigrationsIT extends AbstractDbIT {


  @Test
  void flyway_schema_history_exists() {
    Integer n =
      jdbc.queryForObject("select count(*) from flyway_schema_history", Integer.class);

    // At least V1__baseline.sql must be recorded
    assertThat(n).isNotNull();
    assertThat(n).isGreaterThan(0);
  }
}