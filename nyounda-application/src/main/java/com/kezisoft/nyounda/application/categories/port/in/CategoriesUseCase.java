package com.kezisoft.nyounda.application.categories.port.in;

import com.kezisoft.nyounda.domain.categories.Category;

import java.util.List;

public interface CategoriesUseCase {

    List<Category> getAllCategories();
}
