package com.kezisoft.nyounda.persistence.servicerequest.entity;

import com.kezisoft.nyounda.domain.servicerequest.ServiceRequestStatus;
import com.kezisoft.nyounda.persistence.categories.entity.CategoryEntity;
import com.kezisoft.nyounda.persistence.image.entity.ImageEntity;
import com.kezisoft.nyounda.persistence.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
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
    @GeneratedValue(strategy = GenerationType.UUID)
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
    private String addressText;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 40)
    private ServiceRequestStatus status = ServiceRequestStatus.PENDING;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ImageEntity> photos = new ArrayList<>();

    public void addPhoto(String url) {
        ImageEntity p = new ImageEntity();
        p.setId(UUID.randomUUID());
        p.setUrl(url);
        photos.add(p);
    }
}

