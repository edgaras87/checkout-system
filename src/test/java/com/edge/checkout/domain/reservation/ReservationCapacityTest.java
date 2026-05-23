package com.edge.checkout.domain.reservation;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReservationCapacityTest {

  @Test
  void represents_capacity_identity_limit_and_committed_quantity() {
    ReservableCapacityId id = new ReservableCapacityId("capacity-1");
    CapacityLimit limit = new CapacityLimit(10);
    CommittedReservationQuantity committed = new CommittedReservationQuantity(4);

    ReservationCapacity capacity = new ReservationCapacity(id, limit, committed);

    assertThat(capacity.id()).isEqualTo(id);
    assertThat(capacity.limit()).isEqualTo(limit);
    assertThat(capacity.committedQuantity()).isEqualTo(committed);
  }

  @Test
  void rejects_null_capacity_identity() {
    CapacityLimit limit = new CapacityLimit(10);
    CommittedReservationQuantity committed = new CommittedReservationQuantity(4);

    assertThatThrownBy(() -> new ReservationCapacity(null, limit, committed))
      .isInstanceOf(NullPointerException.class)
      .hasMessage("Reservable capacity id must not be null");
  }

  @Test
  void rejects_null_capacity_limit() {
    ReservableCapacityId id = new ReservableCapacityId("capacity-1");
    CommittedReservationQuantity committed = new CommittedReservationQuantity(4);

    assertThatThrownBy(() -> new ReservationCapacity(id, null, committed))
      .isInstanceOf(NullPointerException.class)
      .hasMessage("Capacity limit must not be null");
  }

  @Test
  void rejects_null_committed_quantity() {
    ReservableCapacityId id = new ReservableCapacityId("capacity-1");
    CapacityLimit limit = new CapacityLimit(10);

    assertThatThrownBy(() -> new ReservationCapacity(id, limit, null))
      .isInstanceOf(NullPointerException.class)
      .hasMessage("Committed reservation quantity must not be null");
  }

  @Test
  void rejects_committed_quantity_above_capacity_limit() {
    ReservableCapacityId id = new ReservableCapacityId("capacity-1");
    CapacityLimit limit = new CapacityLimit(10);
    CommittedReservationQuantity committed = new CommittedReservationQuantity(11);

    assertThatThrownBy(() -> new ReservationCapacity(id, limit, committed))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Committed reservation quantity must not exceed capacity limit");
  }

  @Test
  void has_remaining_capacity_when_requested_quantity_keeps_total_within_limit() {
    ReservationCapacity capacity = new ReservationCapacity(
      new ReservableCapacityId("capacity-1"),
      new CapacityLimit(10),
      new CommittedReservationQuantity(6)
    );

    assertThat(capacity.hasRemainingCapacityFor(new ReservationQuantity(4))).isTrue();
  }

  @Test
  void does_not_have_remaining_capacity_when_requested_quantity_exceeds_limit() {
    ReservationCapacity capacity = new ReservationCapacity(
      new ReservableCapacityId("capacity-1"),
      new CapacityLimit(10),
      new CommittedReservationQuantity(7)
    );

    assertThat(capacity.hasRemainingCapacityFor(new ReservationQuantity(4))).isFalse();
  }

  @Test
  void rejects_null_requested_quantity_when_checking_remaining_capacity() {
    ReservationCapacity capacity = new ReservationCapacity(
      new ReservableCapacityId("capacity-1"),
      new CapacityLimit(10),
      new CommittedReservationQuantity(7)
    );

    assertThatThrownBy(() -> capacity.hasRemainingCapacityFor(null))
      .isInstanceOf(NullPointerException.class)
      .hasMessage("Reservation quantity must not be null");
  }
}
