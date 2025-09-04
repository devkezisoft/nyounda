package com.kezisoft.nyounda.application.homeservice.command;

import com.kezisoft.nyounda.domain.categories.Category;
import com.kezisoft.nyounda.domain.provider.Provider;
import com.kezisoft.nyounda.domain.servicerequest.*;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;

public record CreateServiceCommand(
        String title,
        String description,
        BigDecimal price,
        PricingType pricingType,
        UUID providerId,
        int minimumRequirement,
        UUID categoryId,
        List<UUID> tags,
        List<DayOfWeek> availabilityDays,
        List<UUID> imageIds
) {

    public static ServiceRequest toDomain(CreateServiceCommand command, Provider provider, Category category, List<Tag> tags, List<Image> images) {
        return new ServiceRequest(
                ServiceRequestId.valueOf(UUID.randomUUID()), // generate a new ID when creating
                command.title(),
                command.description(),
                command.pricingType(),
                command.price(),
                command.minimumRequirement(),
                provider,
                images,
                category,
                tags,
                command.availabilityDays(),
                List.of()
        );

    }
}
