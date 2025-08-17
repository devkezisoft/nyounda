package com.kezisoft.nyounda.api.categories.response;

import com.kezisoft.nyounda.domain.homeservice.Category;

import java.util.List;
import java.util.UUID;

public record CategoryView(
        UUID id,
        String name,
        String description,
        String emoji,
        List<CategoryView> subcategories
) {
    public static CategoryView fromDomain(Category category) {
        return new CategoryView(
                category.id().value(),
                category.name(),
                category.description(),
                category.emoji(),
                category.subcategories().stream()
                        .map(CategoryView::fromDomain)
                        .toList()
        );
    }
}
