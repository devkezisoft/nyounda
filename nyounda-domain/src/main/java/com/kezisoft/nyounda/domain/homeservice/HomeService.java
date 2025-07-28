package com.kezisoft.nyounda.domain.homeservice;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.util.List;

public record HomeService(
        HomeServiceId id,
        String title,
        String description,
        PricingType pricingType,
        BigDecimal price,
        int minimumRequirement,
        Provider provider,
        List<ServiceImage> images,
        Category category,
        List<Tag> tags,
        List<DayOfWeek> availabilityDays,
        List<Review> reviews
) {
}
