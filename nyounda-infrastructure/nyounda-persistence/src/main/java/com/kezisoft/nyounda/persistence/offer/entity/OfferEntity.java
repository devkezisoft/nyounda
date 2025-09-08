// persistence/offer/entity/OfferEntity.java
package com.kezisoft.nyounda.persistence.offer.entity;

import com.kezisoft.nyounda.domain.offer.*;
import com.kezisoft.nyounda.domain.servicerequest.Money;
import com.kezisoft.nyounda.persistence.servicerequest.entity.ServiceRequestEntity;
import com.kezisoft.nyounda.persistence.user.entity.UserEntity;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "offer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OfferEntity {

    @Id
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    private ServiceRequestEntity request;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode", length = 20, nullable = false)
    private OfferMode mode;

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb", nullable = false)
    private Money amount;

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private List<OfferExpense> expenses;

    @Column(name = "message", columnDefinition = "text")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private OfferStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public Offer toDomain() {
        return new Offer(
                OfferId.of(this.id),
                request.toDomain(),
                user.toDomain(),
                this.mode,
                this.amount,
                this.expenses,
                this.message,
                this.status,
                this.createdAt
        );
    }

    public static OfferEntity fromDomain(Offer o, ServiceRequestEntity req, UserEntity user) {
        return OfferEntity.builder()
                .id(o.id().value())
                .request(req)
                .user(user)
                .mode(o.mode())
                .amount(o.amount())
                .expenses(o.expenses())
                .message(o.message())
                .status(o.status())
                .createdAt(o.createdAt())
                .build();
    }
}
