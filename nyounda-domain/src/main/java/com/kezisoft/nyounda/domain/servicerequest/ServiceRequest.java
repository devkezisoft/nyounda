package com.kezisoft.nyounda.domain.servicerequest;

import com.kezisoft.nyounda.domain.categories.Category;

import java.util.List;

public record ServiceRequest(
        ServiceRequestId id,
        String title,
        String description,
        List<Image> images,
        Category category,
        Category subcategory,
        String address
) {
}
