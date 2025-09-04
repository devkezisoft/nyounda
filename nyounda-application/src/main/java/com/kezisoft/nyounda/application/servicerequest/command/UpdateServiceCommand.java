package com.kezisoft.nyounda.application.servicerequest.command;


import com.kezisoft.nyounda.domain.categories.CategoryId;

import java.util.List;
import java.util.UUID;

public record UpdateServiceCommand(
        UUID id,
        UUID userId,
        CategoryId categoryId,
        CategoryId subCategoryId,
        String title,
        String description,
        String addressText,
        List<UUID> imageIds
) {
}
