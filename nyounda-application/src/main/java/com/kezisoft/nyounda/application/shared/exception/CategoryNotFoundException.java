package com.kezisoft.nyounda.application.shared.exception;

import com.kezisoft.nyounda.domain.homeservice.CategoryId;

public class CategoryNotFoundException extends NotFoundException {
    public CategoryNotFoundException(CategoryId id) {
        super("Category with ID " + id + " was not found");
    }
}
