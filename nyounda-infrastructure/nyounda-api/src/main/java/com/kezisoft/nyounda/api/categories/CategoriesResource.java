package com.kezisoft.nyounda.api.categories;

import com.kezisoft.nyounda.api.categories.response.CategoryView;
import com.kezisoft.nyounda.api.errors.BadRequestAlertException;
import com.kezisoft.nyounda.application.categories.port.in.CategoriesUseCase;
import com.kezisoft.nyounda.domain.categories.Category;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * CategoriesResource is a REST controller for managing categories.
 */
@RestController
@RequestMapping("/api/categories")
@AllArgsConstructor
@Slf4j
public class CategoriesResource {
    private final CategoriesUseCase categoriesUseCase;

    /**
     * Fetches all categories.
     *
     * @return ResponseEntity containing a list of CategoryView objects
     * @throws BadRequestAlertException if there is an error fetching categories
     */
    @GetMapping
    public ResponseEntity<List<CategoryView>> categories() throws BadRequestAlertException {
        log.debug("Fetching all categories");
        List<Category> categories = categoriesUseCase.getAllCategories();
        List<CategoryView> categoryViews = categories.stream()
                .map(CategoryView::fromDomain)
                .toList();
        return ResponseEntity.ok(categoryViews);
    }

    /**
     * Fetches all categories.
     *
     * @return ResponseEntity containing a list of CategoryView objects
     * @throws BadRequestAlertException if there is an error fetching categories
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategoryView> category(@PathVariable UUID id) throws BadRequestAlertException {
        log.debug("Fetching category with id: {}", id);
        Category category = categoriesUseCase.getCategoryById(id)
                .orElseThrow(() -> new BadRequestAlertException("Category not found", "nyounda-api", "categorynotfound"));
        return ResponseEntity.ok(CategoryView.fromDomain(category));
    }
}
