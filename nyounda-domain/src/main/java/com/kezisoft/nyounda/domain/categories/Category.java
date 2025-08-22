package com.kezisoft.nyounda.domain.categories;

import java.util.List;

public record Category(CategoryId id,
                       String name,
                       String description,
                       String emoji,
                       List<Category> subcategories) {
}
