package com.edge.checkout.application.reservation;

import com.edge.checkout.application.port.reservation.ReservationCapacityCommitmentPort;
import com.edge.checkout.domain.reservation.ReservableCapacityId;
import com.edge.checkout.domain.reservation.ReservationDecision;
import com.edge.checkout.domain.reservation.ReservationQuantity;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReserveCapacityServiceTest {

  @Test
  void returns_accepted_result_when_commitment_port_accepts() {
    ReservationCapacityCommitmentPort port = command -> ReservationDecision.ACCEPTED;
    ReserveCapacityService service = new ReserveCapacityService(port);

    ReserveCapacityCommand command = command();

    ReserveCapacityResult result = service.reserve(command);

    assertThat(result.capacityId()).isEqualTo(command.capacityId());
    assertThat(result.requestedQuantity()).isEqualTo(command.requestedQuantity());
    assertThat(result.decision()).isEqualTo(ReservationDecision.ACCEPTED);
    assertThat(result.isAccepted()).isTrue();
    assertThat(result.consumesCapacity()).isTrue();
  }

  @Test
  void returns_rejected_result_when_commitment_port_rejects() {
    ReservationCapacityCommitmentPort port = command -> ReservationDecision.REJECTED;
    ReserveCapacityService service = new ReserveCapacityService(port);

    ReserveCapacityCommand command = command();

    ReserveCapacityResult result = service.reserve(command);

    assertThat(result.capacityId()).isEqualTo(command.capacityId());
    assertThat(result.requestedQuantity()).isEqualTo(command.requestedQuantity());
    assertThat(result.decision()).isEqualTo(ReservationDecision.REJECTED);
    assertThat(result.isRejected()).isTrue();
    assertThat(result.consumesCapacity()).isFalse();
  }

  @Test
  void passes_command_to_commitment_port() {
    AtomicReference<ReserveCapacityCommand> capturedCommand = new AtomicReference<>();

    ReservationCapacityCommitmentPort port = command -> {
      capturedCommand.set(command);
      return ReservationDecision.ACCEPTED;
    };

    ReserveCapacityService service = new ReserveCapacityService(port);
    ReserveCapacityCommand command = command();

    service.reserve(command);

    assertThat(capturedCommand).hasValue(command);
  }

  @Test
  void rejects_null_commitment_port() {
    assertThatThrownBy(() -> new ReserveCapacityService(null))
      .isInstanceOf(NullPointerException.class)
      .hasMessage("Reservation capacity commitment port must not be null");
  }

  @Test
  void rejects_null_command() {
    ReserveCapacityService service = new ReserveCapacityService(
      command -> ReservationDecision.ACCEPTED
    );

    assertThatThrownBy(() -> service.reserve(null))
      .isInstanceOf(NullPointerException.class)
      .hasMessage("Reserve capacity command must not be null");
  }

  @Test
  void rejects_null_decision_from_commitment_port() {
    ReserveCapacityService service = new ReserveCapacityService(
      command -> null
    );

    assertThatThrownBy(() -> service.reserve(command()))
      .isInstanceOf(NullPointerException.class)
      .hasMessage("Reservation decision must not be null");
  }

  private static ReserveCapacityCommand command() {
    return new ReserveCapacityCommand(
      new ReservableCapacityId("capacity-1"),
      new ReservationQuantity(3)
    );
  }
}
