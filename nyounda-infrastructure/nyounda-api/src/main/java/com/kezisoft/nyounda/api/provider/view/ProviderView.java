package com.kezisoft.nyounda.api.provider.view;

import com.kezisoft.nyounda.domain.provider.Provider;
import com.kezisoft.nyounda.domain.provider.ProviderSkill;

import java.util.List;
import java.util.UUID;

public record ProviderView(
        UUID id,
        String fullName,
        String avatarUrl,
        double averageRating,
        int numberOfReviews,
        String location,
        int yearsExperience,
        int jobsDone,
        String responseTime, // Example: "< 2 hours"
        List<ProviderSkill> skills
) {
    public static ProviderView fromDomain(Provider provider) {
        return new ProviderView(
                provider.id().value(),
                provider.fullName(),
                provider.avatarUrl(),
                provider.averageRating(),
                provider.numberOfReviews(),
                provider.location(),
                provider.yearsExperience(),
                provider.jobsDone(),
                provider.responseTime(),
                provider.skills()
        );
    }
}
