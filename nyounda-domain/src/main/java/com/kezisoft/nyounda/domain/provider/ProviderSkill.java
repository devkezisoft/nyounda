package com.kezisoft.nyounda.domain.provider;

import com.kezisoft.nyounda.domain.categories.Category;

import java.util.UUID;

public record ProviderSkill(
        UUID id,
        Category category,
        String description,
        Integer experienceYears
) {
}
