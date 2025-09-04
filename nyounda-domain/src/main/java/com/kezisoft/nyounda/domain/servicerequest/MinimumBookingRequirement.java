package com.kezisoft.nyounda.domain.servicerequest;

public record MinimumBookingRequirement(
        int quantity,
        PricingType pricingType
) {
    public boolean isSatisfiedBy(int requestedQuantity) {
        return requestedQuantity >= quantity;
    }
}
