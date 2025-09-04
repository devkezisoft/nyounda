package com.kezisoft.nyounda.application.shared.exception;

import com.kezisoft.nyounda.domain.servicerequest.ServiceRequestId;

public class HomeServiceNotFoundException extends NotFoundException {
    public HomeServiceNotFoundException(ServiceRequestId id) {
        super("HomeService with ID " + id + " was not found", "homeService", "homeServiceNotFound");
    }
}
