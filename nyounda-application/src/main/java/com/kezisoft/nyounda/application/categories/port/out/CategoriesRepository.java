package com.kezisoft.nyounda.application.categories.port.out;

import com.kezisoft.nyounda.domain.homeservice.Category;
import com.kezisoft.nyounda.domain.homeservice.CategoryId;

import java.util.List;
import java.util.Optional;

public interface CategoriesRepository {
    List<Category> findAllCategories();

    Optional<Category> findById(CategoryId categoryId);
}
