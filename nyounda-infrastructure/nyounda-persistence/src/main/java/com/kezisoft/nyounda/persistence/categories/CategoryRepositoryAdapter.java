package com.kezisoft.nyounda.persistence.categories;

import com.kezisoft.nyounda.application.categories.port.out.CategoriesRepository;
import com.kezisoft.nyounda.domain.categories.Category;
import com.kezisoft.nyounda.domain.categories.CategoryId;
import com.kezisoft.nyounda.persistence.categories.entity.CategoryEntity;
import com.kezisoft.nyounda.persistence.categories.jpa.JpaCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryAdapter implements CategoriesRepository {

    private final JpaCategoryRepository repository;

    @Override
    public List<Category> findAllCategories() {
        return repository.findAll().stream()
                .filter(CategoryEntity::isRoot)
                .map(CategoryEntity::toDomain)
                .toList();
    }

    @Override
    public Optional<Category> findById(CategoryId id) {
        return repository.findById(id.value())
                .map(CategoryEntity::toDomain);
    }
}
