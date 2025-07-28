package com.kezisoft.nyounda.persistence.homeservice;

import com.kezisoft.nyounda.application.homeservice.port.out.ProviderRepository;
import com.kezisoft.nyounda.domain.homeservice.Provider;
import com.kezisoft.nyounda.domain.homeservice.ProviderId;
import com.kezisoft.nyounda.persistence.homeservice.entity.ProviderEntity;
import com.kezisoft.nyounda.persistence.homeservice.jpa.JpaProviderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProviderRepositoryAdapter implements ProviderRepository {

    private final JpaProviderRepository repository;

    @Override
    public Optional<Provider> findById(ProviderId id) {
        return repository.findById(id.value())
                .map(ProviderEntity::toDomain);
    }
}
