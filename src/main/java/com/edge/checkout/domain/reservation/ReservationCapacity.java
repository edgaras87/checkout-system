package com.edge.checkout.domain.reservation;

import java.util.Objects;

public record ReservationCapacity(
  ReservableCapacityId id,
  CapacityLimit limit,
  CommittedReservationQuantity committedQuantity
) {

  public ReservationCapacity {
    Objects.requireNonNull(id, "Reservable capacity id must not be null");
    Objects.requireNonNull(limit, "Capacity limit must not be null");
    Objects.requireNonNull(committedQuantity, "Committed reservation quantity must not be null");

    if (committedQuantity.value() > limit.value()) {
      throw new IllegalArgumentException(
        "Committed reservation quantity must not exceed capacity limit"
      );
    }
  }

  public boolean hasRemainingCapacityFor(ReservationQuantity requestedQuantity) {
    Objects.requireNonNull(requestedQuantity, "Reservation quantity must not be null");

    return limit.canCover(committedQuantity, requestedQuantity);
  }
}
