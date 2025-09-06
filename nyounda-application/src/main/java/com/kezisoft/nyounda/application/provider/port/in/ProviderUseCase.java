package com.kezisoft.nyounda.application.provider.port.in;

import com.kezisoft.nyounda.application.provider.command.ProviderCreateCommand;
import com.kezisoft.nyounda.application.provider.command.ProviderUpdateCommand;
import com.kezisoft.nyounda.domain.provider.Provider;
import com.kezisoft.nyounda.domain.provider.ProviderId;

import java.util.Optional;
import java.util.UUID;

public interface ProviderUseCase {
    Optional<Provider> getByUserId(UUID userId);

    Provider create(UUID userId, ProviderCreateCommand command);

    Optional<Provider> update(UUID currentUserId, ProviderId providerId, ProviderUpdateCommand command);
}
