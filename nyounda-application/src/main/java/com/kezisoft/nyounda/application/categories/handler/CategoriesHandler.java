package com.kezisoft.nyounda.application.categories.handler;

import com.kezisoft.nyounda.application.categories.port.in.CategoriesUseCase;
import com.kezisoft.nyounda.application.categories.port.out.CategoriesRepository;
import com.kezisoft.nyounda.domain.homeservice.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriesHandler implements CategoriesUseCase {
    private final CategoriesRepository categoriesRepository;

    @Override
    public List<Category> getAllCategories() {
        return categoriesRepository.findAllCategories();
    }
}
