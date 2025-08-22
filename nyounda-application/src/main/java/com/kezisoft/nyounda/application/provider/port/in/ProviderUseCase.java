package com.kezisoft.nyounda.application.provider.port.in;

import com.kezisoft.nyounda.domain.provider.Provider;

import java.util.Optional;
import java.util.UUID;

public interface ProviderUseCase {
    Optional<Provider> getByUserId(UUID userId);
}
