package com.kezisoft.nyounda.application.shared.exception;

import com.kezisoft.nyounda.domain.provider.ProviderId;

public class ProviderNotFoundException extends NotFoundException {
    public ProviderNotFoundException(ProviderId id) {
        super("Provider with ID " + id + " was not found", "provider", "providerNotFound");
    }

    public ProviderNotFoundException() {
        super("Provider was not found", "provider", "providerNotFound");
    }
}
