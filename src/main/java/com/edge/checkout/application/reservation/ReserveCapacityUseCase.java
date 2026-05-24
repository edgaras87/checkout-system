package com.edge.checkout.application.reservation;

public interface ReserveCapacityUseCase {

  ReserveCapacityResult reserve(ReserveCapacityCommand command);
}
