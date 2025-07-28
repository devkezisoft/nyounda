package com.kezisoft.nyounda.application.homeservice.port.out;

import com.kezisoft.nyounda.domain.homeservice.Category;
import com.kezisoft.nyounda.domain.homeservice.CategoryId;

import java.util.Optional;

public interface CategoryRepository {
    Optional<Category> findById(CategoryId categoryId);
}
