package com.kezisoft.nyounda.application.categories.port.out;

import com.kezisoft.nyounda.domain.categories.Category;
import com.kezisoft.nyounda.domain.categories.CategoryId;

import java.util.List;
import java.util.Optional;

public interface CategoriesRepository {
    List<Category> findAllCategories();

    Optional<Category> findById(CategoryId categoryId);
}
