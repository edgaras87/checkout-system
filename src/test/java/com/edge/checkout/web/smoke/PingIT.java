package com.edge.checkout.web.smoke;

import com.edge.checkout.testsupport.AbstractWebSmokeIT;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PingIT extends AbstractWebSmokeIT {

  @Test
  void ping_returns_pong() {
    String body = http.getForObject("/ping", String.class);

    assertThat(body).isEqualTo("pong");
  }
}
