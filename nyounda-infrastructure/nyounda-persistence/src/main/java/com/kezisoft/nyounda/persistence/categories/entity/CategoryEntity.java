package com.kezisoft.nyounda.persistence.categories.entity;

import com.kezisoft.nyounda.domain.categories.Category;
import com.kezisoft.nyounda.domain.categories.CategoryId;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(length = 10)
    private String emoji;

    @Column(columnDefinition = "TEXT")
    private String description;

    // 🔗 Self reference to parent
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private CategoryEntity parent;

    // 🔗 Children (subcategories)
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CategoryEntity> subcategories = new ArrayList<>();

    public static CategoryEntity fromDomain(Category category) {
        return CategoryEntity.builder()
                .id(category.id().value())
                .name(category.name())
                .description(category.description())
                .emoji(category.emoji())
                .subcategories(category.subcategories().stream()
                        .map(CategoryEntity::fromDomain)
                        .toList())
                .build();
    }


    public Category toDomain() {
        return new Category(
                CategoryId.valueOf(this.id),
                this.name,
                this.description,
                this.emoji,
                this.subcategories == null ? List.of() : this.subcategories.stream()
                        .map(CategoryEntity::toDomain)
                        .toList()
        );
    }

    public boolean isRoot() {
        return this.parent == null;
    }
}
