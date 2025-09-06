package com.kezisoft.nyounda.application.searchrequest;

import com.kezisoft.nyounda.application.searchrequest.command.ServiceRequestSearchQuery;
import com.kezisoft.nyounda.domain.servicerequest.ServiceRequest;
import org.springframework.data.domain.Page;

public interface ServiceSearchRequestsUseCase {
    Page<ServiceRequest> search(ServiceRequestSearchQuery query);
}
