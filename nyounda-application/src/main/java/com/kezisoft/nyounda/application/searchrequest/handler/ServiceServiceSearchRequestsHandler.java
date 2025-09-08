package com.kezisoft.nyounda.application.searchrequest.handler;

import com.kezisoft.nyounda.application.offer.port.out.OfferRepository;
import com.kezisoft.nyounda.application.searchrequest.ServiceSearchRequestsUseCase;
import com.kezisoft.nyounda.application.searchrequest.command.ServiceRequestSearchQuery;
import com.kezisoft.nyounda.application.servicerequest.port.out.ServiceRequestRepository;
import com.kezisoft.nyounda.domain.servicerequest.ServiceRequestSearchHit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServiceServiceSearchRequestsHandler implements ServiceSearchRequestsUseCase {
    private final ServiceRequestRepository repo;
    private final OfferRepository offerRepo;


    @Override
    public Page<ServiceRequestSearchHit> search(ServiceRequestSearchQuery q) {
        // validate & normalize
        var fixed = q.fixed();

        var page = repo.search(fixed); // Page<ServiceRequest>

        // gather the ids on this page and check offers in ONE query
        var reqIds = page.getContent().stream()
                .map(sr -> sr.id().value())
                .toList();

        var appliedIds = offerRepo.findRequestIdsAppliedByUser(q.userId(), reqIds);

        return page.map(sr -> new ServiceRequestSearchHit(
                sr,
                appliedIds.contains(sr.id().value())
        ));
    }
}
