package com.edge.checkout.application.reservation;

import com.edge.checkout.application.port.reservation.ReservationCapacityCommitmentPort;
import com.edge.checkout.domain.reservation.ReservationDecision;

import java.util.Objects;

public final class ReserveCapacityService implements ReserveCapacityUseCase {

  private final ReservationCapacityCommitmentPort commitmentPort;

  public ReserveCapacityService(ReservationCapacityCommitmentPort commitmentPort) {
    this.commitmentPort = Objects.requireNonNull(
      commitmentPort,
      "Reservation capacity commitment port must not be null"
    );
  }

  @Override
  public ReserveCapacityResult reserve(ReserveCapacityCommand command) {
    Objects.requireNonNull(command, "Reserve capacity command must not be null");

    ReservationDecision decision = Objects.requireNonNull(
      commitmentPort.commit(command),
      "Reservation decision must not be null"
    );

    return new ReserveCapacityResult(
      command.capacityId(),
      command.requestedQuantity(),
      decision
    );
  }
}
