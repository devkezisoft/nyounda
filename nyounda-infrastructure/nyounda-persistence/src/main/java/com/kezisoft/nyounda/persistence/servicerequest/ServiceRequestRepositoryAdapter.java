package com.kezisoft.nyounda.persistence.servicerequest;

import com.kezisoft.nyounda.application.searchrequest.command.ServiceRequestSearchQuery;
import com.kezisoft.nyounda.application.servicerequest.port.out.ServiceRequestRepository;
import com.kezisoft.nyounda.domain.offer.OfferId;
import com.kezisoft.nyounda.domain.servicerequest.ServiceRequest;
import com.kezisoft.nyounda.domain.servicerequest.ServiceRequestId;
import com.kezisoft.nyounda.domain.user.User;
import com.kezisoft.nyounda.persistence.servicerequest.entity.ServiceRequestEntity;
import com.kezisoft.nyounda.persistence.servicerequest.jpa.JpaServiceRequestRepository;
import com.kezisoft.nyounda.persistence.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ServiceRequestRepositoryAdapter implements ServiceRequestRepository {

    private final JpaServiceRequestRepository repository;
    private final ServiceRequestSpec spec;

    @Override
    public ServiceRequest save(ServiceRequest service) {
        ServiceRequestEntity entity = ServiceRequestEntity.fromDomain(service);
        return repository.save(entity).toDomain();
    }

    @Override
    public void deleteById(ServiceRequestId id) {
        repository.deleteById(id.value());
    }

    @Override
    public Optional<ServiceRequest> findById(ServiceRequestId id) {
        return repository.findById(id.value()).map(ServiceRequestEntity::toDomain);
    }

    @Override
    public List<ServiceRequest> findAllByUser(User user) {
        return repository.findByUser(UserEntity.fromDomain(user)).stream()
                .map(ServiceRequestEntity::toDomain)
                .toList();
    }

    @Override
    public Page<ServiceRequest> search(ServiceRequestSearchQuery q) {
        Specification<ServiceRequestEntity> criterias = Specification.allOf(
                spec.notCanceled(),
                spec.excludeUser(q.userId()),
                spec.hasAnySkill(q.skillIds()),
                spec.isWithinRadius(q.address(), q.radiusKm())
        );

        Page<ServiceRequestEntity> page = repository.findAll(criterias, q.pageable());
        return page.map(ServiceRequestEntity::toDomain);

    }

    @Override
    public void choose(ServiceRequestId requestId, OfferId offerId) {
        int updatedCount = repository.choose(requestId.value(), offerId.value());
        if (updatedCount == 0) {
            log.warn("No service request found with id: {}", requestId);
        }
    }
}
