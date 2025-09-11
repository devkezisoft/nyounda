package com.kezisoft.nyounda.api.servicerequests.response;

import com.kezisoft.nyounda.api.account.view.UserView;
import com.kezisoft.nyounda.api.categories.response.CategoryView;
import com.kezisoft.nyounda.api.images.response.ImageView;
import com.kezisoft.nyounda.domain.offer.Offer;
import com.kezisoft.nyounda.domain.offer.OfferExpense;
import com.kezisoft.nyounda.domain.offer.OfferMode;
import com.kezisoft.nyounda.domain.servicerequest.Money;
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
        boolean rejected,
        List<OfferCandidateView> candidates,

        // NEW:
        AssignedProviderView assignedTo,     // null when not chosen
        OfferSummary chosenOffer,            // null when not chosen
        LocalDateTime assignedAt             // createdAt of chosen offer (or time of choice)
) {
    public static ServiceRequestDetailView from(
            ServiceRequest serviceRequest,
            boolean applied,
            boolean rejected,
            List<OfferCandidateView> candidates) {
        Offer chosen = serviceRequest.chosenOffer().orElse(null);

        AssignedProviderView assignedView = null;
        OfferSummary chosenSummary = null;
        LocalDateTime assignedAt = null;

        if (chosen != null) {
            assignedView = AssignedProviderView.from(chosen, serviceRequest.address());
            chosenSummary = new OfferSummary(chosen.mode(), chosen.amount(), chosen.expenses());
            assignedAt = chosen.createdAt();
        }

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
                rejected,
                candidates,
                assignedView,
                chosenSummary,
                assignedAt
        );
    }

    public record OfferSummary(OfferMode mode, Money amount, List<OfferExpense> expenses) {
    }

    public record AssignedProviderView(UUID id, String fullName, String avatarUrl, String phone, String city) {
        public static AssignedProviderView from(Offer chosen, String address) {
            var u = chosen.user();
            // You can enrich this with provider profile if available
            return new AssignedProviderView(u.id(), u.fullName(), u.avatarUrl(), u.phone(), address);
        }
    }
}
