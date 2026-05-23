package com.edge.checkout.domain.reservation;

public enum ReservationDecision {

  ACCEPTED(true),
  REJECTED(false);

  private final boolean capacityConsuming;

  ReservationDecision(boolean capacityConsuming) {
    this.capacityConsuming = capacityConsuming;
  }

  public boolean consumesCapacity() {
    return capacityConsuming;
  }
}
