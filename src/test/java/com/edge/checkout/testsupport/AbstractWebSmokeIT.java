package com.edge.checkout.testsupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Web-only smoke test base.
 *
 * Starts the HTTP server and exposes TestRestTemplate,
 * but intentionally disables DB / Flyway / JPA auto-configuration.
 *
 * Use for:
 *   - server boot sanity checks
 *   - basic routing / serialization, for example /ping
 *
 * Do NOT use for endpoints that require database state.
 * For DB-backed endpoints, use AbstractWebDbIT.
 */
@SpringBootTest(
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
  properties = {
    "spring.flyway.enabled=false",
    "spring.jpa.hibernate.ddl-auto=none",
    "spring.autoconfigure.exclude="
      + "org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration,"
      + "org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration"
  }
)
@AutoConfigureTestRestTemplate
@ActiveProfiles("test")
public abstract class AbstractWebSmokeIT {

  @Autowired
  protected TestRestTemplate http;
}
