package com.edge.checkout.domain.reservation;

import java.util.Objects;

public record ReservableCapacityId(String value) {

  public ReservableCapacityId {
    Objects.requireNonNull(value, "Reservable capacity id must not be null");

    if (value.isBlank()) {
      throw new IllegalArgumentException("Reservable capacity id must not be blank");
    }
  }
}
