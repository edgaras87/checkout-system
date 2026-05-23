package com.edge.checkout.domain.reservation;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CommittedReservationQuantityTest {

  @Test
  void accepts_zero_committed_quantity() {
    CommittedReservationQuantity quantity = new CommittedReservationQuantity(0);

    assertThat(quantity.value()).isZero();
  }

  @Test
  void accepts_positive_committed_quantity() {
    CommittedReservationQuantity quantity = new CommittedReservationQuantity(5);

    assertThat(quantity.value()).isEqualTo(5);
  }

  @Test
  void rejects_negative_committed_quantity() {
    assertThatThrownBy(() -> new CommittedReservationQuantity(-1))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Committed reservation quantity must not be negative");
  }

  @Test
  void adds_requested_quantity_to_committed_quantity() {
    CommittedReservationQuantity committed = new CommittedReservationQuantity(5);
    ReservationQuantity requested = new ReservationQuantity(3);

    CommittedReservationQuantity result = committed.plus(requested);

    assertThat(result.value()).isEqualTo(8);
  }

  @Test
  void rejects_null_quantity_when_adding_to_committed_quantity() {
    CommittedReservationQuantity committed = new CommittedReservationQuantity(5);

    assertThatThrownBy(() -> committed.plus(null))
      .isInstanceOf(NullPointerException.class)
      .hasMessage("Reservation quantity must not be null");
  }
}
