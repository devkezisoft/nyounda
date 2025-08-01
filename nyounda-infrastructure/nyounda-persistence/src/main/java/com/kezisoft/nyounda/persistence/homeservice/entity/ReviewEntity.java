package com.kezisoft.nyounda.persistence.homeservice.entity;

import com.kezisoft.nyounda.domain.homeservice.Review;
import com.kezisoft.nyounda.persistence.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "review")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewEntity {

    @Id
    private UUID id;

    private String comment;

    private int rating;

    private LocalDateTime date;

    @Column(name = "service_id", nullable = false)
    private UUID serviceId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    public Review toDomain() {
        return new Review(
                id,
                user.getFullName(),
                user.getAvatarUrl(),
                rating,
                comment,
                date);
    }
}
