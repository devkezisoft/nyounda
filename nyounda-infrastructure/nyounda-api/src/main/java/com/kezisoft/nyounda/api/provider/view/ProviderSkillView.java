package com.kezisoft.nyounda.api.provider.view;

import com.kezisoft.nyounda.api.categories.response.CategoryView;
import com.kezisoft.nyounda.domain.provider.ProviderSkill;

import java.util.UUID;

public record ProviderSkillView(
        UUID id,
        CategoryView category,
        String description,
        Integer experienceYears
) {
    public static ProviderSkillView fromDomain(ProviderSkill providerSkill) {
        return new ProviderSkillView(
                providerSkill.id(),
                CategoryView.fromDomain(providerSkill.category()),
                providerSkill.description(),
                providerSkill.experienceYears()
        );
    }
}
