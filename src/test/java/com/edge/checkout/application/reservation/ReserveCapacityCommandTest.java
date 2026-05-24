package com.edge.checkout.application.reservation;

import com.edge.checkout.domain.reservation.ReservableCapacityId;
import com.edge.checkout.domain.reservation.ReservationQuantity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReserveCapacityCommandTest {

  @Test
  void represents_capacity_id_and_requested_quantity() {
    ReservableCapacityId capacityId = new ReservableCapacityId("capacity-1");
    ReservationQuantity requestedQuantity = new ReservationQuantity(3);

    ReserveCapacityCommand command = new ReserveCapacityCommand(capacityId, requestedQuantity);

    assertThat(command.capacityId()).isEqualTo(capacityId);
    assertThat(command.requestedQuantity()).isEqualTo(requestedQuantity);
  }

  @Test
  void rejects_null_capacity_id() {
    ReservationQuantity requestedQuantity = new ReservationQuantity(3);

    assertThatThrownBy(() -> new ReserveCapacityCommand(null, requestedQuantity))
      .isInstanceOf(NullPointerException.class)
      .hasMessage("Reservable capacity id must not be null");
  }

  @Test
  void rejects_null_requested_quantity() {
    ReservableCapacityId capacityId = new ReservableCapacityId("capacity-1");

    assertThatThrownBy(() -> new ReserveCapacityCommand(capacityId, null))
      .isInstanceOf(NullPointerException.class)
      .hasMessage("Requested reservation quantity must not be null");
  }
}
