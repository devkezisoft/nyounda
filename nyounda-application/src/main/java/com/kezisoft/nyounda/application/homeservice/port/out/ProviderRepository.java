package com.kezisoft.nyounda.application.homeservice.port.out;

import com.kezisoft.nyounda.domain.homeservice.Provider;
import com.kezisoft.nyounda.domain.homeservice.ProviderId;

import java.util.Optional;

public interface ProviderRepository {
    Optional<Provider> findById(ProviderId providerId);
}
