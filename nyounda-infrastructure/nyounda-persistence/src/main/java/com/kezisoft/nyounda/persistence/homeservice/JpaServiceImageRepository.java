package com.kezisoft.nyounda.persistence.homeservice;

import com.kezisoft.nyounda.application.homeservice.port.out.ServiceImageRepository;
import com.kezisoft.nyounda.domain.homeservice.ServiceImage;
import com.kezisoft.nyounda.persistence.homeservice.entity.ServiceImageEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaServiceImageRepository implements ServiceImageRepository {

    private final SpringJpaServiceImageRepository repository;

    @Override
    public List<ServiceImage> findAllImages(List<UUID> ids) {
        return repository.findAllById(ids)
                .stream()
                .map(ServiceImageEntity::toDomain)
                .toList();
    }
}
