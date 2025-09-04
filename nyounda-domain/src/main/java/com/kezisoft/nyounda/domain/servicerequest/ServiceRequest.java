package com.kezisoft.nyounda.domain.servicerequest;

import com.kezisoft.nyounda.domain.categories.Category;
import com.kezisoft.nyounda.domain.user.User;
import lombok.With;

import java.util.List;

@With
public record ServiceRequest(
        ServiceRequestId id,
        ServiceRequestStatus status,
        String title,
        String description,
        List<Image> images,
        Category category,
        Category subcategory,
        User user,
        String address
) {
}
