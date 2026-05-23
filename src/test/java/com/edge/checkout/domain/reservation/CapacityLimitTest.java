package com.edge.checkout.domain.reservation;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CapacityLimitTest {

  @Test
  void accepts_zero_capacity_limit() {
    CapacityLimit limit = new CapacityLimit(0);

    assertThat(limit.value()).isZero();
  }

  @Test
  void accepts_positive_capacity_limit() {
    CapacityLimit limit = new CapacityLimit(10);

    assertThat(limit.value()).isEqualTo(10);
  }

  @Test
  void rejects_negative_capacity_limit() {
    assertThatThrownBy(() -> new CapacityLimit(-1))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Capacity limit must not be negative");
  }

  @Test
  void can_cover_requested_quantity_when_total_stays_within_limit() {
    CapacityLimit limit = new CapacityLimit(10);
    CommittedReservationQuantity committed = new CommittedReservationQuantity(6);
    ReservationQuantity requested = new ReservationQuantity(4);

    assertThat(limit.canCover(committed, requested)).isTrue();
  }

  @Test
  void cannot_cover_requested_quantity_when_total_exceeds_limit() {
    CapacityLimit limit = new CapacityLimit(10);
    CommittedReservationQuantity committed = new CommittedReservationQuantity(7);
    ReservationQuantity requested = new ReservationQuantity(4);

    assertThat(limit.canCover(committed, requested)).isFalse();
  }

  @Test
  void rejects_null_committed_quantity_when_checking_coverage() {
    CapacityLimit limit = new CapacityLimit(10);
    ReservationQuantity requested = new ReservationQuantity(1);

    assertThatThrownBy(() -> limit.canCover(null, requested))
      .isInstanceOf(NullPointerException.class)
      .hasMessage("Committed reservation quantity must not be null");
  }

  @Test
  void rejects_null_requested_quantity_when_checking_coverage() {
    CapacityLimit limit = new CapacityLimit(10);
    CommittedReservationQuantity committed = new CommittedReservationQuantity(1);

    assertThatThrownBy(() -> limit.canCover(committed, null))
      .isInstanceOf(NullPointerException.class)
      .hasMessage("Reservation quantity must not be null");
  }
}
