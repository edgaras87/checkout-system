package com.edge.checkout.domain.reservation;

public record ReservationQuantity(long value) {

  public ReservationQuantity {
    if (value <= 0) {
      throw new IllegalArgumentException("Reservation quantity must be positive");
    }
  }
}
