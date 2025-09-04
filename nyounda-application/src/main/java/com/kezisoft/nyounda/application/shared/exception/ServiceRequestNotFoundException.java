package com.kezisoft.nyounda.application.shared.exception;

import java.util.UUID;

public class ServiceRequestNotFoundException extends NotFoundException {
    public ServiceRequestNotFoundException() {
        super("Service request was not found", "serviceRequest", "serviceRequestNotFound");
    }

    public ServiceRequestNotFoundException(UUID id) {
        super("Service request with ID " + id + " was not found", "serviceRequest", "serviceRequestNotFound");
    }
}
