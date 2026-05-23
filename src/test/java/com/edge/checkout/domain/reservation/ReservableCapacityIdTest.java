package com.edge.checkout.domain.reservation;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReservableCapacityIdTest {

  @Test
  void accepts_non_blank_capacity_identity() {
    ReservableCapacityId id = new ReservableCapacityId("capacity-1");

    assertThat(id.value()).isEqualTo("capacity-1");
  }

  @Test
  void rejects_null_capacity_identity() {
    assertThatThrownBy(() -> new ReservableCapacityId(null))
      .isInstanceOf(NullPointerException.class)
      .hasMessage("Reservable capacity id must not be null");
  }

  @Test
  void rejects_blank_capacity_identity() {
    assertThatThrownBy(() -> new ReservableCapacityId(" "))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Reservable capacity id must not be blank");
  }
}
