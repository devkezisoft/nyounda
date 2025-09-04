package com.kezisoft.nyounda.api.servicerequests.request;

import com.kezisoft.nyounda.application.servicerequest.command.ServiceRequestCreateCommand;
import com.kezisoft.nyounda.domain.categories.CategoryId;

import java.util.List;
import java.util.UUID;

public record CreateRequest(
        UUID userId,
        UUID categoryId,
        UUID subCategoryId,
        String title,
        String description,
        String addressText,
        List<UUID> imageIds
) {
    public ServiceRequestCreateCommand toCommand(UUID userId) {
        return new ServiceRequestCreateCommand(
                userId,
                CategoryId.valueOf(categoryId),
                CategoryId.valueOf(subCategoryId),
                title,
                description,
                addressText,
                imageIds
        );
    }
}