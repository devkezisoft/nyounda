package com.kezisoft.nyounda.application.homeservice.port.in;

import com.kezisoft.nyounda.application.homeservice.command.CreateServiceCommand;
import com.kezisoft.nyounda.application.homeservice.command.UpdateServiceCommand;
import com.kezisoft.nyounda.domain.homeservice.HomeServiceId;

public interface HomeServiceCommandUseCase {
    HomeServiceId create(CreateServiceCommand command);

    void update(HomeServiceId homeServiceId, UpdateServiceCommand command);

    void delete(HomeServiceId homeServiceId);
}