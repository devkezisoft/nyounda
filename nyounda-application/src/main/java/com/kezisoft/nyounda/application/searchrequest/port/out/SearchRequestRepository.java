package com.kezisoft.nyounda.application.searchrequest.port.out;

import com.kezisoft.nyounda.application.searchrequest.command.ServiceRequestSearchQuery;
import com.kezisoft.nyounda.domain.servicerequest.ServiceRequest;

import java.util.List;

public interface SearchRequestRepository {
    List<ServiceRequest> search(ServiceRequestSearchQuery query);
}
