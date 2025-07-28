package com.kezisoft.nyounda.persistence.homeservice.entity;

import com.kezisoft.nyounda.domain.homeservice.*;
import io.hypersistence.utils.hibernate.type.array.ListArrayType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "home_service")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HomeServiceEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private String title;
    private String description;

    @Enumerated(EnumType.STRING)
    private PricingType pricingType;

    private BigDecimal price;

    private int minimumToAcceptBooking;

    @Column(name = "provider_id", nullable = false)
    private UUID providerId;

    @Column(name = "category_id", nullable = false)
    private UUID categoryId;

    @Column(name = "availability_days", columnDefinition = "text[]")
    @Type(ListArrayType.class)
    private List<String> availabilityDays = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "home_service_tags", joinColumns = @JoinColumn(name = "service_id"))
    @Column(name = "id")
    private List<UUID> tagIds = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "home_service_images", joinColumns = @JoinColumn(name = "service_id"))
    @Column(name = "id")
    private List<UUID> imageIds = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "home_service_reviews", joinColumns = @JoinColumn(name = "service_id"))
    @Column(name = "id")
    private List<UUID> reviewIds = new ArrayList<>();

    public static HomeServiceEntity fromDomain(HomeService domain) {
        return HomeServiceEntity.builder()
                .id(domain.id().value())
                .title(domain.title())
                .description(domain.description())
                .pricingType(domain.pricingType())
                .price(domain.price())
                .minimumToAcceptBooking(domain.minimumRequirement())
                .providerId(domain.provider().id().value())
                .categoryId(domain.category().id().value())
                .tagIds(domain.tags().stream().map(Tag::id).toList())
                .imageIds(domain.images().stream().map(ServiceImage::id).toList())
                .availabilityDays(domain.availabilityDays().stream().map(Enum::name).toList())
                .reviewIds(domain.reviews().stream().map(Review::id).toList())
                .build();
    }
}
