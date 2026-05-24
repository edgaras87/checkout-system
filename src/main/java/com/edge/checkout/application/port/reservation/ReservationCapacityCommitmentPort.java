package com.edge.checkout.application.port.reservation;

import com.edge.checkout.application.reservation.ReserveCapacityCommand;
import com.edge.checkout.domain.reservation.ReservationDecision;

public interface ReservationCapacityCommitmentPort {

  ReservationDecision commit(ReserveCapacityCommand command);
}
