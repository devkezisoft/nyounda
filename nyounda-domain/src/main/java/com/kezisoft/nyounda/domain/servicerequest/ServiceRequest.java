package com.kezisoft.nyounda.domain.servicerequest;

import com.kezisoft.nyounda.domain.categories.Category;
import com.kezisoft.nyounda.domain.offer.Offer;
import com.kezisoft.nyounda.domain.user.User;
import lombok.With;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
        String address,
        Optional<Offer> chosenOffer,
        LocalDateTime createdAt
) {

    public List<UUID> imageIds() {
        return images.stream().map(Image::id).toList();
    }

    public boolean isOwnedBy(UUID userId) {
        return user.id().equals(userId);
    }

    public boolean isNotOwnedBy(UUID userId) {
        return !isOwnedBy(userId);
    }
}
