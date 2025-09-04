package com.kezisoft.nyounda.application.homeservice.port.in;

import com.kezisoft.nyounda.application.homeservice.command.CreateServiceCommand;
import com.kezisoft.nyounda.application.homeservice.command.UpdateServiceCommand;
import com.kezisoft.nyounda.domain.servicerequest.ServiceRequestId;

public interface HomeServiceCommandUseCase {
    ServiceRequestId create(CreateServiceCommand command);

    void update(ServiceRequestId serviceRequestId, UpdateServiceCommand command);

    void delete(ServiceRequestId serviceRequestId);
}