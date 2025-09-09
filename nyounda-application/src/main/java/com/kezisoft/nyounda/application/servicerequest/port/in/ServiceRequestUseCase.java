package com.kezisoft.nyounda.application.servicerequest.port.in;

import com.kezisoft.nyounda.application.servicerequest.command.ServiceRequestCreateCommand;
import com.kezisoft.nyounda.application.servicerequest.command.UpdateServiceCommand;
import com.kezisoft.nyounda.domain.servicerequest.OfferCandidateView;
import com.kezisoft.nyounda.domain.servicerequest.ServiceRequest;
import com.kezisoft.nyounda.domain.servicerequest.ServiceRequestId;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ServiceRequestUseCase {
    ServiceRequest create(ServiceRequestCreateCommand command);

    Optional<ServiceRequest> update(ServiceRequestId serviceRequestId, UpdateServiceCommand command);

    void delete(UUID currentUserId, ServiceRequestId serviceRequestId) throws AccessDeniedException;

    List<ServiceRequest> findAllByUserId(UUID userId);

    Optional<ServiceRequest> findById(ServiceRequestId id);

    boolean hasUserAlreadyApplied(UUID userId, ServiceRequestId serviceRequestId);

    List<OfferCandidateView> findCandidates(ServiceRequestId requestId);

}