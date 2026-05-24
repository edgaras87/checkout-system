package com.edge.checkout.application.reservation;

import com.edge.checkout.domain.reservation.ReservableCapacityId;
import com.edge.checkout.domain.reservation.ReservationQuantity;

import java.util.Objects;

public record ReserveCapacityCommand(
  ReservableCapacityId capacityId,
  ReservationQuantity requestedQuantity
) {

  public ReserveCapacityCommand {
    Objects.requireNonNull(capacityId, "Reservable capacity id must not be null");
    Objects.requireNonNull(requestedQuantity, "Requested reservation quantity must not be null");
  }
}
