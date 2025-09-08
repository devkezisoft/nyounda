// api/offers/OfferResource.java
package com.kezisoft.nyounda.api.offer;

import com.kezisoft.nyounda.api.offer.request.OfferCreateRequest;
import com.kezisoft.nyounda.api.offer.response.OfferView;
import com.kezisoft.nyounda.api.security.SecurityUtils;
import com.kezisoft.nyounda.application.offer.port.in.OfferUseCase;
import com.kezisoft.nyounda.application.shared.exception.ProviderNotFoundException;
import com.kezisoft.nyounda.domain.offer.Offer;
import com.kezisoft.nyounda.domain.servicerequest.ServiceRequestId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;


@RestController
@PreAuthorize("hasRole('PROVIDER')")
@RequiredArgsConstructor
@RequestMapping("/api/requests/{requestId}/offers")
public class OfferResource {

    private final OfferUseCase offerUseCase;

    @PostMapping
    public ResponseEntity<OfferView> create(
            @PathVariable("requestId") UUID requestId,
            @RequestBody OfferCreateRequest body
    ) {
        UUID currentUserId = SecurityUtils.getCurrentUserLogin()
                .map(UUID::fromString)
                .orElseThrow(ProviderNotFoundException::new);

        var cmd = body.toCommand(
                ServiceRequestId.valueOf(requestId),
                currentUserId
        );

        Offer offer = offerUseCase.create(cmd);

        var view = OfferView.from(offer);
        return ResponseEntity
                .created(URI.create("/api/requests/" + requestId + "/offers" + view.id()))
                .body(view);
    }
}
