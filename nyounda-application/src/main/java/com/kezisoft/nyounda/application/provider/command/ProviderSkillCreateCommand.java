package com.kezisoft.nyounda.application.provider.command;

import com.kezisoft.nyounda.domain.categories.Category;
import com.kezisoft.nyounda.domain.provider.ProviderSkill;

import java.util.UUID;

public record ProviderSkillCreateCommand(
        UUID categoryId,
        UUID subcategoryId,
        String description
) {
    public ProviderSkill toDomain(Category category) {
        return new ProviderSkill(
                UUID.randomUUID(),
                category,
                description,
                0 // Default experience years, can be modified as needed
        );
    }
}
