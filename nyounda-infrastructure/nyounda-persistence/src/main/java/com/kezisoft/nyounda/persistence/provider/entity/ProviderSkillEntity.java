package com.kezisoft.nyounda.persistence.provider.entity;

import com.kezisoft.nyounda.domain.provider.ProviderSkill;
import com.kezisoft.nyounda.persistence.categories.entity.CategoryEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    public static ProviderSkillEntity fromDomain(ProviderSkill skill, UUID value) {
        return ProviderSkillEntity.builder()
                .id(skill.id())
                .provider(ProviderEntity.builder().id(value).build())
                .category(CategoryEntity.builder().id(skill.category().id().value()).build())
                .description(skill.description())
                .experienceYears(skill.experienceYears())
                .build();
    }

    public ProviderSkill toDomain() {
        return new ProviderSkill(
                id,
                category.toDomain(),
                description,
                experienceYears
        );
    }
}
