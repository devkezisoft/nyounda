package com.kezisoft.nyounda.persistence.servicerequest;

import com.kezisoft.nyounda.application.servicerequest.port.out.ServiceRequestRepository;
import com.kezisoft.nyounda.domain.servicerequest.ServiceRequest;
import com.kezisoft.nyounda.domain.servicerequest.ServiceRequestId;
import com.kezisoft.nyounda.domain.user.User;
import com.kezisoft.nyounda.persistence.servicerequest.entity.ServiceRequestEntity;
import com.kezisoft.nyounda.persistence.servicerequest.jpa.JpaServiceRequestRepository;
import com.kezisoft.nyounda.persistence.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ServiceRequestRequestRepositoryAdapter implements ServiceRequestRepository {

    private final JpaServiceRequestRepository repository;

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
}
