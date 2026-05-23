package com.edge.checkout.domain.reservation;

import java.util.Objects;

public record CommittedReservationQuantity(long value) {

  public CommittedReservationQuantity {
    if (value < 0) {
      throw new IllegalArgumentException("Committed reservation quantity must not be negative");
    }
  }

  public CommittedReservationQuantity plus(ReservationQuantity quantity) {
    Objects.requireNonNull(quantity, "Reservation quantity must not be null");

    return new CommittedReservationQuantity(Math.addExact(value, quantity.value()));
  }
}
