package com.kezisoft.nyounda.domain.homeservice;

public record MinimumBookingRequirement(
        int quantity,
        PricingType pricingType
) {
    public boolean isSatisfiedBy(int requestedQuantity) {
        return requestedQuantity >= quantity;
    }
}
