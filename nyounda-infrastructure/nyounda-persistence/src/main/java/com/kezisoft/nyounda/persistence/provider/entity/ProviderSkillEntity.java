package com.kezisoft.nyounda.persistence.provider.entity;

import com.kezisoft.nyounda.domain.homeservice.Money;
import com.kezisoft.nyounda.domain.provider.ProviderSkill;
import com.kezisoft.nyounda.persistence.categories.entity.CategoryEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "provider_skill"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProviderSkillEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // works with PostgreSQL UUID; DB also has default gen_random_uuid()
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "provider_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_pskill_provider")
    )
    @OnDelete(action = OnDeleteAction.CASCADE) // mirrors ON DELETE CASCADE
    private ProviderEntity provider;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "category_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_pskill_category")
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private CategoryEntity category;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "experience_years")
    private Integer experienceYears;

    // 👇 JSONB mapping to your Money record
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "rate", columnDefinition = "jsonb")
    private Money rate;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    public ProviderSkill toDomain() {
        return new ProviderSkill(
                id,
                category.toDomain(),
                description,
                experienceYears,
                rate
        );
    }
}
