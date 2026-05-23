package com.edge.checkout.domain.reservation;

import java.util.Objects;

public record CapacityLimit(long value) {

  public CapacityLimit {
    if (value < 0) {
      throw new IllegalArgumentException("Capacity limit must not be negative");
    }
  }

  public boolean canCover(
    CommittedReservationQuantity committedQuantity,
    ReservationQuantity requestedQuantity
  ) {
    Objects.requireNonNull(committedQuantity, "Committed reservation quantity must not be null");
    Objects.requireNonNull(requestedQuantity, "Reservation quantity must not be null");

    long requestedTotal = Math.addExact(committedQuantity.value(), requestedQuantity.value());

    return requestedTotal <= value;
  }
}
