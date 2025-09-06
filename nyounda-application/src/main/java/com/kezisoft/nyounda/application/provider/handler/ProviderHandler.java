package com.kezisoft.nyounda.application.provider.handler;

import com.kezisoft.nyounda.application.categories.port.out.CategoriesRepository;
import com.kezisoft.nyounda.application.provider.command.ProviderCreateCommand;
import com.kezisoft.nyounda.application.provider.command.ProviderUpdateCommand;
import com.kezisoft.nyounda.application.provider.port.in.ProviderUseCase;
import com.kezisoft.nyounda.application.provider.port.out.ProviderRepository;
import com.kezisoft.nyounda.application.shared.exception.CategoryNotFoundException;
import com.kezisoft.nyounda.domain.categories.Category;
import com.kezisoft.nyounda.domain.categories.CategoryId;
import com.kezisoft.nyounda.domain.provider.Provider;
import com.kezisoft.nyounda.domain.provider.ProviderId;
import com.kezisoft.nyounda.domain.provider.ProviderSkill;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
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

    @Override
    public Optional<Provider> update(UUID currentUserId, ProviderId providerId, ProviderUpdateCommand command) {
        return providerRepository.findById(providerId).map(existingProvider -> {
            if (!CollectionUtils.isEmpty(command.skills())) {
                providerRepository.deleteAllProviderSkills(
                        skillsToDelete(command, existingProvider)
                );
                existingProvider = existingProvider.withSkills(command.skills()
                        .stream()
                        .map(skillCommand ->
                                skillCommand
                                        .toDomain(category(skillCommand.subcategoryId())))
                        .toList());
            }
            if (command.location() != null) {
                existingProvider = existingProvider.withLocation(command.location());
            }
            return providerRepository.save(currentUserId, existingProvider);
        });
    }

    private static List<UUID> skillsToDelete(ProviderUpdateCommand command, Provider existingProvider) {
        return existingProvider.skills()
                .stream()
                .map(ProviderSkill::id)
                .filter(id -> command.skills()
                        .stream()
                        .noneMatch(skill -> skill.id() != null && skill.id().equals(id)))
                .toList();
    }

    public Category category(UUID categoryId) {
        CategoryId id = CategoryId.valueOf(categoryId);
        return categoriesRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
    }
}
