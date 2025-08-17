package com.kezisoft.nyounda.application.categories.port.in;

import com.kezisoft.nyounda.domain.homeservice.Category;

import java.util.List;

public interface CategoriesUseCase {

    List<Category> getAllCategories();
}
