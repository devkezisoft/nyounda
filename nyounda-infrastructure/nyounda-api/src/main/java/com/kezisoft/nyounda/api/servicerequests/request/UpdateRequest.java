package com.kezisoft.nyounda.api.servicerequests.request;

import com.kezisoft.nyounda.application.servicerequest.command.UpdateServiceCommand;
import com.kezisoft.nyounda.domain.categories.CategoryId;

import java.util.List;
import java.util.UUID;

public record UpdateRequest(
        UUID id,
        UUID categoryId,
        UUID subCategoryId,
        String title,
        String description,
        String addressText,
        List<UUID> imageIds
) {
    public UpdateServiceCommand toCommand(UUID userId) {
        return new UpdateServiceCommand(
                id,
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