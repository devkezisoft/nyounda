package com.kezisoft.nyounda.api.offer.request;

import com.kezisoft.nyounda.application.offer.command.OfferCreateCommand;
import com.kezisoft.nyounda.domain.offer.OfferExpense;
import com.kezisoft.nyounda.domain.offer.OfferMode;
import com.kezisoft.nyounda.domain.servicerequest.Money;
import com.kezisoft.nyounda.domain.servicerequest.ServiceRequestId;

import java.util.List;
import java.util.UUID;

public record OfferCreateRequest(
        OfferMode mode,            // "hourly" | "fixed"
        double amount,
        String message,
        List<ExpenseRequest> expenses
) {
    public OfferCreateCommand toCommand(ServiceRequestId serviceRequestId, UUID currentUserId) {
        return new OfferCreateCommand(
                serviceRequestId,
                currentUserId,
                mode,
                Money.xaf(amount),
                expenses.stream()
                        .map(e -> OfferExpense.of(e.label(), Money.xaf(e.price())))
                        .toList(),
                message
        );
    }
}
