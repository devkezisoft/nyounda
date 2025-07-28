package com.kezisoft.nyounda.persistence.homeservice.entity;

import com.kezisoft.nyounda.domain.homeservice.Tag;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "tag")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String label;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EntityStatus status;

    public static TagEntity fromDomain(Tag tag) {
        return TagEntity.builder()
                .id(tag.id())
                .label(tag.label())
                .status(EntityStatus.ACTIVE) // or derive from context
                .build();
    }

    public Tag toDomain() {
        return new Tag(id, label);
    }
}
