package com.kezisoft.nyounda.application.servicerequest.port.out;

import com.kezisoft.nyounda.application.searchrequest.command.ServiceRequestSearchQuery;
import com.kezisoft.nyounda.domain.servicerequest.ServiceRequest;
import com.kezisoft.nyounda.domain.servicerequest.ServiceRequestId;
import com.kezisoft.nyounda.domain.user.User;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface ServiceRequestRepository {

    ServiceRequest save(ServiceRequest service);

    void deleteById(ServiceRequestId id);

    Optional<ServiceRequest> findById(ServiceRequestId id);

    List<ServiceRequest> findAllByUser(User user);

    Page<ServiceRequest> search(ServiceRequestSearchQuery fixed);
}