// domain/offer/Offer.java
package com.kezisoft.nyounda.domain.offer;

import com.kezisoft.nyounda.domain.servicerequest.Money;
import com.kezisoft.nyounda.domain.servicerequest.ServiceRequest;
import com.kezisoft.nyounda.domain.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public record Offer(
        OfferId id,
        ServiceRequest request,
        User user,
        OfferMode mode,
        Money amount, // euros (or cents)
        List<OfferExpense> expenses,
        String message,
        OfferStatus status,
        LocalDateTime createdAt,
        Optional<LocalDateTime> assignedAt // time when this offer was chosen
) {

    public Money totalAmount() {
        Money total = amount;
        if (expenses != null) {
            for (OfferExpense expense : expenses) {
                total = total.add(expense.price());
            }
        }
        return total;
    }
}
