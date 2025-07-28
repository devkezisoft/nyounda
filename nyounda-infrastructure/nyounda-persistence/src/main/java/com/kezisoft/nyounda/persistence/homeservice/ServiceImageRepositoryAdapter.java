package com.kezisoft.nyounda.persistence.homeservice;

import com.kezisoft.nyounda.application.homeservice.port.out.ServiceImageRepository;
import com.kezisoft.nyounda.domain.homeservice.ServiceImage;
import com.kezisoft.nyounda.persistence.homeservice.entity.ServiceImageEntity;
import com.kezisoft.nyounda.persistence.homeservice.jpa.JpaServiceImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ServiceImageRepositoryAdapter implements ServiceImageRepository {

    private final JpaServiceImageRepository repository;

    @Override
    public List<ServiceImage> findAllImages(List<UUID> ids) {
        return repository.findAllById(ids)
                .stream()
                .map(ServiceImageEntity::toDomain)
                .toList();
    }
}
