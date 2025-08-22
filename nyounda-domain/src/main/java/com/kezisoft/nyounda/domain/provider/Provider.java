package com.kezisoft.nyounda.domain.provider;

import java.util.List;

public record Provider(
        ProviderId id,
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
}
