package com.kezisoft.nyounda.application.provider.port.out;

import com.kezisoft.nyounda.domain.provider.Provider;
import com.kezisoft.nyounda.domain.provider.ProviderId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProviderRepository {
    Optional<Provider> findById(ProviderId providerId);

    Optional<Provider> findByUserId(UUID userId);

    Provider save(UUID userId, Provider provider);

    void deleteAllProviderSkills(List<UUID> skillIds);
}
