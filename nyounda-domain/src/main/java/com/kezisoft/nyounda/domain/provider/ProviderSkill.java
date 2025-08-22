package com.kezisoft.nyounda.domain.provider;

import com.kezisoft.nyounda.domain.categories.Category;
import com.kezisoft.nyounda.domain.homeservice.Money;

import java.util.UUID;

public record ProviderSkill(
        UUID id,
        Category category,   // feuille (ligne de category avec parent_id non null)
        String description,
        Integer experienceYears,
        Money rate
) {
}
