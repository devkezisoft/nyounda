package com.kezisoft.nyounda.domain.servicerequest;

public record ServiceRequestSearchHit(
        ServiceRequest request,
        boolean applied
) {
}
