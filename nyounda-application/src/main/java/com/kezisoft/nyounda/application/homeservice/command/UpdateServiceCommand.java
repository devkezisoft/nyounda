package com.kezisoft.nyounda.application.homeservice.command;


import com.kezisoft.nyounda.domain.categories.CategoryId;
import com.kezisoft.nyounda.domain.servicerequest.PricingType;
import com.kezisoft.nyounda.domain.servicerequest.ServiceRequestId;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

public record UpdateServiceCommand(
        ServiceRequestId id,
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
