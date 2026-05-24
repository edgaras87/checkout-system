package com.edge.checkout.application.reservation;

import com.edge.checkout.domain.reservation.ReservableCapacityId;
import com.edge.checkout.domain.reservation.ReservationDecision;
import com.edge.checkout.domain.reservation.ReservationQuantity;

import java.util.Objects;

public record ReserveCapacityResult(
  ReservableCapacityId capacityId,
  ReservationQuantity requestedQuantity,
  ReservationDecision decision
) {

  public ReserveCapacityResult {
    Objects.requireNonNull(capacityId, "Reservable capacity id must not be null");
    Objects.requireNonNull(requestedQuantity, "Requested reservation quantity must not be null");
    Objects.requireNonNull(decision, "Reservation decision must not be null");
  }

  public boolean isAccepted() {
    return decision == ReservationDecision.ACCEPTED;
  }

  public boolean isRejected() {
    return decision == ReservationDecision.REJECTED;
  }

  public boolean consumesCapacity() {
    return decision.consumesCapacity();
  }
}
