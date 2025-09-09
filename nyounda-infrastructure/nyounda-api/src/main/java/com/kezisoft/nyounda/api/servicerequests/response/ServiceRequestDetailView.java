package com.kezisoft.nyounda.api.servicerequests.response;

import com.kezisoft.nyounda.api.account.view.UserView;
import com.kezisoft.nyounda.api.categories.response.CategoryView;
import com.kezisoft.nyounda.api.images.response.ImageView;
import com.kezisoft.nyounda.domain.servicerequest.OfferCandidateView;
import com.kezisoft.nyounda.domain.servicerequest.ServiceRequest;
import com.kezisoft.nyounda.domain.servicerequest.ServiceRequestStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ServiceRequestDetailView(
        UUID id,
        ServiceRequestStatus status,
        String title,
        String description,
        List<ImageView> images,
        CategoryView category,
        CategoryView subcategory,
        String address,
        LocalDateTime createdAt,
        UserView user,
        boolean applied,
        List<OfferCandidateView> candidates
) {
    public static ServiceRequestDetailView from(
            ServiceRequest serviceRequest,
            boolean applied,
            List<OfferCandidateView> candidates) {
        return new ServiceRequestDetailView(
                serviceRequest.id().value(),
                serviceRequest.status(),
                serviceRequest.title(),
                serviceRequest.description(),
                serviceRequest.images().stream()
                        .map(ImageView::of)
                        .toList(),
                CategoryView.fromDomain(serviceRequest.category()),
                CategoryView.fromDomain(serviceRequest.subcategory()),
                serviceRequest.address(),
                serviceRequest.createdAt(),
                UserView.fromDomain(serviceRequest.user()),
                applied,
                candidates
        );
    }
}
