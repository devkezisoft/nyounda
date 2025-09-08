package com.kezisoft.nyounda.api.servicerequests;

import com.kezisoft.nyounda.api.security.SecurityUtils;
import com.kezisoft.nyounda.api.servicerequests.response.ServiceRequestSearchHitView;
import com.kezisoft.nyounda.application.searchrequest.ServiceSearchRequestsUseCase;
import com.kezisoft.nyounda.application.searchrequest.command.ServiceRequestSearchQuery;
import com.kezisoft.nyounda.application.shared.exception.AccountNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/requests-search")
@RequiredArgsConstructor
public class ServiceRequestSearchResource {

    private final ServiceSearchRequestsUseCase useCase;

    @GetMapping
    public Page<ServiceRequestSearchHitView> search(
            @RequestParam(required = false) List<UUID> skillIds,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) Integer radiusKm,
            Pageable pageable // page, size, sort=createdAt,desc
    ) {
        UUID currentUserId = SecurityUtils.getCurrentUserLogin()
                .map(UUID::fromString)
                .orElseThrow(AccountNotFoundException::new);
        var query = new ServiceRequestSearchQuery(
                currentUserId,
                skillIds == null ? List.of() : skillIds,
                address,
                radiusKm,
                pageable
        );
        return useCase.search(query).map(ServiceRequestSearchHitView::from);
    }
}
