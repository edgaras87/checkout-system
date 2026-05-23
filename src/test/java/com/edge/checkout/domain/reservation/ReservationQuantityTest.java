package com.edge.checkout.domain.reservation;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReservationQuantityTest {

  @Test
  void accepts_positive_quantity() {
    ReservationQuantity quantity = new ReservationQuantity(3);

    assertThat(quantity.value()).isEqualTo(3);
  }

  @Test
  void rejects_zero_quantity() {
    assertThatThrownBy(() -> new ReservationQuantity(0))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Reservation quantity must be positive");
  }

  @Test
  void rejects_negative_quantity() {
    assertThatThrownBy(() -> new ReservationQuantity(-1))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Reservation quantity must be positive");
  }
}
