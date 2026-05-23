package com.edge.checkout.domain.reservation;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReservationDecisionTest {

  @Test
  void accepted_decision_consumes_capacity() {
    assertThat(ReservationDecision.ACCEPTED.consumesCapacity()).isTrue();
  }

  @Test
  void rejected_decision_does_not_consume_capacity() {
    assertThat(ReservationDecision.REJECTED.consumesCapacity()).isFalse();
  }
}
