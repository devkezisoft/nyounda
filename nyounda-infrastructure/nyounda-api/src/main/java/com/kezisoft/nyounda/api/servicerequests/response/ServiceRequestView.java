package com.kezisoft.nyounda.api.servicerequests.response;

import com.kezisoft.nyounda.api.categories.response.CategoryView;
import com.kezisoft.nyounda.api.images.response.ImageView;
import com.kezisoft.nyounda.domain.servicerequest.ServiceRequest;

import java.util.List;
import java.util.UUID;

public record ServiceRequestView(
        UUID id,
        String title,
        String description,
        List<ImageView> images,
        CategoryView category,
        CategoryView subcategory,
        String address
) {
    public static ServiceRequestView from(ServiceRequest serviceRequest) {
        return new ServiceRequestView(
                serviceRequest.id().value(),
                serviceRequest.title(),
                serviceRequest.description(),
                serviceRequest.images().stream()
                        .map(ImageView::of)
                        .toList(),
                CategoryView.fromDomain(serviceRequest.category()),
                CategoryView.fromDomain(serviceRequest.subcategory()),
                serviceRequest.address()
        );
    }
}
