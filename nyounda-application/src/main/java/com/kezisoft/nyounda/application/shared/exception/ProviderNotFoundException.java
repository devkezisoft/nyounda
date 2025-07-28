package com.kezisoft.nyounda.application.shared.exception;

import com.kezisoft.nyounda.domain.homeservice.ProviderId;

public class ProviderNotFoundException extends NotFoundException {
    public ProviderNotFoundException(ProviderId id) {
        super("Provider with ID " + id + " was not found");
    }
}
