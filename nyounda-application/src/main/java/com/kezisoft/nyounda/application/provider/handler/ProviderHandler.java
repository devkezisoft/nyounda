package com.kezisoft.nyounda.application.provider.handler;

import com.kezisoft.nyounda.application.provider.port.in.ProviderUseCase;
import com.kezisoft.nyounda.application.provider.port.out.ProviderRepository;
import com.kezisoft.nyounda.domain.provider.Provider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProviderHandler implements ProviderUseCase {

    private final ProviderRepository providerRepository;

    @Override
    public Optional<Provider> getByUserId(UUID id) {
        log.debug("Fetching provider for user id: {}", id);
        return providerRepository.findByUserId(id);
    }
}
