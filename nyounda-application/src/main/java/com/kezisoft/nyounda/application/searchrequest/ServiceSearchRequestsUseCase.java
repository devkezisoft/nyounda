package com.kezisoft.nyounda.application.searchrequest;

import com.kezisoft.nyounda.application.searchrequest.command.ServiceRequestSearchQuery;
import com.kezisoft.nyounda.domain.servicerequest.ServiceRequestSearchHit;
import org.springframework.data.domain.Page;

public interface ServiceSearchRequestsUseCase {
    Page<ServiceRequestSearchHit> search(ServiceRequestSearchQuery query);
}
