package com.edge.checkout.application.reservation;

import com.edge.checkout.domain.reservation.ReservableCapacityId;
import com.edge.checkout.domain.reservation.ReservationDecision;
import com.edge.checkout.domain.reservation.ReservationQuantity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReserveCapacityResultTest {

  @Test
  void represents_accepted_reservation_result() {
    ReserveCapacityResult result = new ReserveCapacityResult(
      new ReservableCapacityId("capacity-1"),
      new ReservationQuantity(3),
      ReservationDecision.ACCEPTED
    );

    assertThat(result.isAccepted()).isTrue();
    assertThat(result.isRejected()).isFalse();
    assertThat(result.consumesCapacity()).isTrue();
  }

  @Test
  void represents_rejected_reservation_result() {
    ReserveCapacityResult result = new ReserveCapacityResult(
      new ReservableCapacityId("capacity-1"),
      new ReservationQuantity(3),
      ReservationDecision.REJECTED
    );

    assertThat(result.isAccepted()).isFalse();
    assertThat(result.isRejected()).isTrue();
    assertThat(result.consumesCapacity()).isFalse();
  }

  @Test
  void rejects_null_capacity_id() {
    ReservationQuantity requestedQuantity = new ReservationQuantity(3);

    assertThatThrownBy(() -> new ReserveCapacityResult(
      null,
      requestedQuantity,
      ReservationDecision.ACCEPTED
    ))
      .isInstanceOf(NullPointerException.class)
      .hasMessage("Reservable capacity id must not be null");
  }

  @Test
  void rejects_null_requested_quantity() {
    ReservableCapacityId capacityId = new ReservableCapacityId("capacity-1");

    assertThatThrownBy(() -> new ReserveCapacityResult(
      capacityId,
      null,
      ReservationDecision.ACCEPTED
    ))
      .isInstanceOf(NullPointerException.class)
      .hasMessage("Requested reservation quantity must not be null");
  }

  @Test
  void rejects_null_decision() {
    ReservableCapacityId capacityId = new ReservableCapacityId("capacity-1");
    ReservationQuantity requestedQuantity = new ReservationQuantity(3);

    assertThatThrownBy(() -> new ReserveCapacityResult(
      capacityId,
      requestedQuantity,
      null
    ))
      .isInstanceOf(NullPointerException.class)
      .hasMessage("Reservation decision must not be null");
  }
}
