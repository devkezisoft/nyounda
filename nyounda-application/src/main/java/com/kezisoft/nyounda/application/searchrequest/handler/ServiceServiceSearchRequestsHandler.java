package com.kezisoft.nyounda.application.searchrequest.handler;

import com.kezisoft.nyounda.application.searchrequest.ServiceSearchRequestsUseCase;
import com.kezisoft.nyounda.application.searchrequest.command.ServiceRequestSearchQuery;
import com.kezisoft.nyounda.application.servicerequest.port.out.ServiceRequestRepository;
import com.kezisoft.nyounda.domain.servicerequest.ServiceRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServiceServiceSearchRequestsHandler implements ServiceSearchRequestsUseCase {
    private final ServiceRequestRepository repo;

    @Override
    public Page<ServiceRequest> search(ServiceRequestSearchQuery q) {
        // validate & normalize
        var fixed = q.fixed();
        return repo.search(fixed);
    }
}
