package com.kezisoft.nyounda.application.homeservice.port.out;

import com.kezisoft.nyounda.domain.servicerequest.ServiceRequest;
import com.kezisoft.nyounda.domain.servicerequest.ServiceRequestId;

import java.util.List;
import java.util.Optional;

public interface HomeServiceRepository {

    ServiceRequestId save(ServiceRequest service);

    void deleteById(ServiceRequestId id);

    Optional<ServiceRequest> findById(ServiceRequestId id);

    List<ServiceRequest> findAll();
}