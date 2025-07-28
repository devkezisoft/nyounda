package com.kezisoft.nyounda.persistence.homeservice;

import com.kezisoft.nyounda.application.homeservice.port.out.CategoryRepository;
import com.kezisoft.nyounda.domain.homeservice.Category;
import com.kezisoft.nyounda.domain.homeservice.CategoryId;
import com.kezisoft.nyounda.persistence.homeservice.entity.CategoryEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaCategoryRepository implements CategoryRepository {

    private final SpringJpaCategoryRepository repository;

    @Override
    public Optional<Category> findById(CategoryId id) {
        return repository.findById(id.value())
                .map(CategoryEntity::toDomain);
    }
}
