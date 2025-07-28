package com.kezisoft.nyounda.persistence.homeservice.entity;

import com.kezisoft.nyounda.domain.homeservice.ServiceImage;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "service_image")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceImageEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String url;

    private boolean isPrimary;

    @Enumerated(EnumType.STRING)
    private EntityStatus status;

    public static ServiceImageEntity fromDomain(ServiceImage image) {
        return ServiceImageEntity.builder()
                .id(image.id())
                .url(image.url())
                .status(EntityStatus.ACTIVE) // or context-based
                .build();
    }

    public ServiceImage toDomain() {
        return new ServiceImage(id, url, isPrimary);
    }
}
