package com.kezisoft.nyounda.application.provider.handler;

import com.kezisoft.nyounda.application.categories.port.out.CategoriesRepository;
import com.kezisoft.nyounda.application.provider.command.ProviderCreateCommand;
import com.kezisoft.nyounda.application.provider.port.in.ProviderUseCase;
import com.kezisoft.nyounda.application.provider.port.out.ProviderRepository;
import com.kezisoft.nyounda.application.shared.exception.CategoryNotFoundException;
import com.kezisoft.nyounda.domain.categories.Category;
import com.kezisoft.nyounda.domain.categories.CategoryId;
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
    private final CategoriesRepository categoriesRepository;

    @Override
    public Optional<Provider> getByUserId(UUID id) {
        log.debug("Fetching provider for user id: {}", id);
        return providerRepository.findByUserId(id);
    }

    @Override
    public Provider create(UUID userId, ProviderCreateCommand command) {
        return providerRepository.save(userId, command.toDomain(
                command.skills()
                        .stream()
                        .map(skillCommand ->
                                skillCommand
                                        .toDomain(category(skillCommand.subcategoryId())))
                        .toList()
        ));
    }

    public Category category(UUID categoryId) {
        CategoryId id = CategoryId.valueOf(categoryId);
        return categoriesRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
    }
}
