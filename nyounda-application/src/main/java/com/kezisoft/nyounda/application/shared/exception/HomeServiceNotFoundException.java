package com.kezisoft.nyounda.application.shared.exception;

import com.kezisoft.nyounda.domain.homeservice.HomeServiceId;

public class HomeServiceNotFoundException extends NotFoundException {
    public HomeServiceNotFoundException(HomeServiceId id) {
        super("HomeService with ID " + id + " was not found");
    }
}
