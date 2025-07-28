package com.kezisoft.nyounda.persistence.homeservice;

import com.kezisoft.nyounda.application.homeservice.port.out.ProviderRepository;
import com.kezisoft.nyounda.domain.homeservice.Provider;
import com.kezisoft.nyounda.domain.homeservice.ProviderId;
import com.kezisoft.nyounda.persistence.homeservice.entity.ProviderEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaProviderRepository implements ProviderRepository {

    private final SpringJpaProviderRepository repository;

    @Override
    public Optional<Provider> findById(ProviderId id) {
        return repository.findById(id.value())
                .map(ProviderEntity::toDomain);
    }
}
