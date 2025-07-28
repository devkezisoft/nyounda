package com.kezisoft.nyounda.application.homeservice.command;


import com.kezisoft.nyounda.domain.homeservice.CategoryId;
import com.kezisoft.nyounda.domain.homeservice.HomeServiceId;
import com.kezisoft.nyounda.domain.homeservice.PricingType;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

public record UpdateServiceCommand(
        HomeServiceId id,
        String name,
        String description,
        CategoryId categoryId,
        Set<UUID> tagIds,
        PricingType pricingType,
        BigDecimal price,
        int minimumToAcceptBooking,
        boolean active
) {
}
