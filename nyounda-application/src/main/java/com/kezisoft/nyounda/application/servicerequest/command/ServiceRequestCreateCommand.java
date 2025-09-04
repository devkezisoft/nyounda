package com.kezisoft.nyounda.application.servicerequest.command;

import com.kezisoft.nyounda.domain.categories.Category;
import com.kezisoft.nyounda.domain.categories.CategoryId;
import com.kezisoft.nyounda.domain.servicerequest.Image;
import com.kezisoft.nyounda.domain.servicerequest.ServiceRequest;
import com.kezisoft.nyounda.domain.servicerequest.ServiceRequestId;
import com.kezisoft.nyounda.domain.servicerequest.ServiceRequestStatus;
import com.kezisoft.nyounda.domain.user.User;

import java.util.List;
import java.util.UUID;

public record ServiceRequestCreateCommand(
        UUID userId,
        CategoryId categoryId,
        CategoryId subCategoryId,
        String title,
        String description,
        String addressText,
        List<UUID> imageIds
) {

    public static ServiceRequest toDomain(
            ServiceRequestCreateCommand command,
            ServiceRequestStatus status, Category category, Category subCategory,
            List<Image> images, User user) {
        return new ServiceRequest(
                ServiceRequestId.generate(),
                status,
                command.title,
                command.description,
                images,
                category,
                subCategory,
                user,
                command.addressText
        );

    }
}
