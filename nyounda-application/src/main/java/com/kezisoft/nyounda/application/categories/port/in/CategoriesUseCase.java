package com.kezisoft.nyounda.application.categories.port.in;

import com.kezisoft.nyounda.domain.categories.Category;
import com.kezisoft.nyounda.domain.categories.CategoryId;

import java.util.List;
import java.util.Optional;

public interface CategoriesUseCase {

    List<Category> getAllCategories();

    Optional<Category> getCategoryById(CategoryId id);
}
