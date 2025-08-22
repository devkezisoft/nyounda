package com.kezisoft.nyounda.persistence.provider.entity;

import com.kezisoft.nyounda.domain.provider.Provider;
import com.kezisoft.nyounda.domain.provider.ProviderId;
import com.kezisoft.nyounda.domain.provider.ProviderSkill;
import com.kezisoft.nyounda.persistence.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "provider")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProviderEntity {

    @Id
    private UUID id;

    private double averageRating;

    private int numberOfReviews;

    private String location;

    private int yearsExperience;

    private int jobsDone;

    private String responseTime;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    public Provider toDomain(List<ProviderSkill> skills) {
        return new Provider(
                ProviderId.valueOf(id),
                user.getFullName(),
                user.getAvatarUrl(),
                averageRating,
                numberOfReviews,
                location,
                yearsExperience,
                jobsDone,
                responseTime,
                skills
        );
    }
}
