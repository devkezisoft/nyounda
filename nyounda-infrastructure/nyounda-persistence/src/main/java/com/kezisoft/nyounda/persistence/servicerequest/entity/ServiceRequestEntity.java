package com.kezisoft.nyounda.persistence.servicerequest.entity;

import com.kezisoft.nyounda.domain.servicerequest.ServiceRequest;
import com.kezisoft.nyounda.domain.servicerequest.ServiceRequestId;
import com.kezisoft.nyounda.domain.servicerequest.ServiceRequestStatus;
import com.kezisoft.nyounda.persistence.categories.entity.CategoryEntity;
import com.kezisoft.nyounda.persistence.image.entity.ImageEntity;
import com.kezisoft.nyounda.persistence.offer.entity.OfferEntity;
import com.kezisoft.nyounda.persistence.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "service_request")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceRequestEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity category;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subcategory_id", nullable = false)
    private CategoryEntity subcategory;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "address", length = 500)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 40)
    private ServiceRequestStatus status = ServiceRequestStatus.PENDING;

    // Attach uploaded images via join table
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "request_image",
            joinColumns = @JoinColumn(name = "request_id"),
            inverseJoinColumns = @JoinColumn(name = "image_id")
    )
    private List<ImageEntity> images = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chosen_offer_id")
    private OfferEntity chosenOffer;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;


    public static ServiceRequestEntity fromDomain(ServiceRequest service) {
        return ServiceRequestEntity.builder()
                .id(service.id().value())
                .user(UserEntity.fromDomain(service.user()))
                .category(CategoryEntity.fromDomain(service.category()))
                .subcategory(CategoryEntity.fromDomain(service.subcategory()))
                .title(service.title())
                .description(service.description())
                .address(service.address())
                .status(service.status())
                .images(service.images() != null ? service.images().stream()
                        .map(ImageEntity::fromDomain)
                        .toList() : null)
                .build();
    }

    public void addPhoto(String url) {
        ImageEntity p = new ImageEntity();
        p.setId(UUID.randomUUID());
        p.setUrl(url);
        images.add(p);
    }

    public ServiceRequest toDomain() {
        return new ServiceRequest(
                ServiceRequestId.valueOf(this.id),
                this.status,
                this.title,
                this.description,
                this.images != null ? this.images.stream()
                        .map(ImageEntity::toDomain)
                        .toList() : null,
                this.category.toDomain(),
                this.subcategory.toDomain(),
                this.user.toDomain(),
                this.address,
                Optional.ofNullable(this.chosenOffer != null ? this.chosenOffer.toDomain(true) : null),
                this.createdAt != null ? this.createdAt : null
        );
    }
}

