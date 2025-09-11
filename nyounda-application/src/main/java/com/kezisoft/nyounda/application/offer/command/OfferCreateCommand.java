// domain/offer/SubmitOfferCommand.java
package com.kezisoft.nyounda.application.offer.command;

import com.kezisoft.nyounda.domain.offer.*;
import com.kezisoft.nyounda.domain.servicerequest.Money;
import com.kezisoft.nyounda.domain.servicerequest.ServiceRequest;
import com.kezisoft.nyounda.domain.servicerequest.ServiceRequestId;
import com.kezisoft.nyounda.domain.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public record OfferCreateCommand(
        ServiceRequestId requestId,
        UUID userId,
        OfferMode mode,
        Money amount,
        List<OfferExpense> expenses,
        String message
) {
    public Offer toDomain(ServiceRequest req, User user, OfferStatus status) {
        return new Offer(
                OfferId.generate(),
                req,
                user,
                mode,
                amount,
                expenses,
                message,
                status,
                LocalDateTime.now(),
                Optional.empty()
        );
    }
}
