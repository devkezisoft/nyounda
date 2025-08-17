package com.kezisoft.nyounda.persistence.categories.jpa;

import com.kezisoft.nyounda.persistence.categories.entity.CategoryEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaCategoryRepository extends JpaRepository<CategoryEntity, UUID> {
    // Tout l’arbre en une requête (pour éviter N+1 sur children)
    @EntityGraph(attributePaths = {"subcategories", "subcategories.subcategories"})
    List<CategoryEntity> findAllByParentIsNullOrderByNameAsc();
}
