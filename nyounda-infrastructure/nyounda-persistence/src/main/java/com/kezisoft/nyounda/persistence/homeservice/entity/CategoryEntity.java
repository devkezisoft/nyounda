package com.kezisoft.nyounda.persistence.homeservice.entity;

import com.kezisoft.nyounda.domain.homeservice.Category;
import com.kezisoft.nyounda.domain.homeservice.CategoryId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

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
    private UUID id;

    private String name;

    private String description;

    public static CategoryEntity fromDomain(Category category) {
        return CategoryEntity.builder()
                .id(category.id().value())
                .name(category.name())
                .description(category.description())
                .build();
    }

    public Category toDomain() {
        return new Category(
                new CategoryId(this.id),
                this.name,
                this.description
        );
    }
}
