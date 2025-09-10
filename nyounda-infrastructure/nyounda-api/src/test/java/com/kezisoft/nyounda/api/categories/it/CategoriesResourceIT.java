package com.kezisoft.nyounda.api.categories.it;

import com.kezisoft.nyounda.api.it.AbstractIntegrationTest;
import com.kezisoft.nyounda.persistence.categories.entity.CategoryEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.UUID;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Full-stack IT for CategoriesResource (no mocks).
 */
@Transactional
public class CategoriesResourceIT extends AbstractIntegrationTest {

    /**
     * Seeds a 2-level tree:
     * root -> child -> grandChild
     * Returns [rootId, childId, grandChildId].
     */
    private UUID[] seedCategoryTree(String rootName, String childName, String grandChildName) {
        var root = new CategoryEntity();
        root.setName(rootName);
        root.setEmoji("🔧");
        root.setDescription(rootName + " desc");
        root.setSubcategories(new ArrayList<>());

        var child = new CategoryEntity();
        child.setName(childName);
        child.setEmoji("🧩");
        child.setDescription(childName + " desc");
        child.setParent(root);
        child.setSubcategories(new ArrayList<>());

        var grand = new CategoryEntity();
        grand.setName(grandChildName);
        grand.setEmoji("🧱");
        grand.setDescription(grandChildName + " desc");
        grand.setParent(child);
        grand.setSubcategories(new ArrayList<>());

        child.getSubcategories().add(grand);
        root.getSubcategories().add(child);

        em.persist(root);      // cascade persists child & grandchild
        em.flush();

        return new UUID[]{root.getId(), child.getId(), grand.getId()};
    }

    @Test
    @DisplayName("GET /api/categories -> returns roots with nested subcategories")
    void getAll_returnsTree() throws Exception {
        // Two roots with their own chains
        seedCategoryTree("Cleaning", "Deep", "Kitchen");
        seedCategoryTree("Plumbing", "Sink", "Leak");

        mockMvc.perform(get("/api/categories")
                        .with(user("any").roles("CLIENT")))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                // 2 roots
                .andExpect(jsonPath("$.length()", is(2)))
                // Names anywhere in the tree
                .andExpect(jsonPath("$..name",
                        hasItems("Cleaning", "Plumbing", "Deep", "Sink", "Kitchen", "Leak")));
    }

    @Test
    @DisplayName("GET /api/categories/{id} -> returns a root with its sub-tree")
    void getById_root() throws Exception {
        UUID[] ids = seedCategoryTree("Plumbing", "Sink", "Leak");
        UUID rootId = ids[0];

        mockMvc.perform(get("/api/categories/{id}", rootId)
                        .with(user("any").roles("CLIENT")))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(rootId.toString()))
                .andExpect(jsonPath("$.name").value("Plumbing"))
                .andExpect(jsonPath("$.subcategories.length()", is(1)))
                .andExpect(jsonPath("$.subcategories[0].name").value("Sink"))
                .andExpect(jsonPath("$.subcategories[0].subcategories[0].name").value("Leak"));
    }

    @Test
    @DisplayName("GET /api/categories/{id} -> returns a leaf (no subcategories)")
    void getById_leaf() throws Exception {
        UUID[] ids = seedCategoryTree("Cleaning", "Deep", "Kitchen");
        UUID leafId = ids[2];

        mockMvc.perform(get("/api/categories/{id}", leafId)
                        .with(user("any").roles("CLIENT")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(leafId.toString()))
                .andExpect(jsonPath("$.name").value("Kitchen"))
                .andExpect(jsonPath("$.subcategories.length()", is(0)));
    }

    @Test
    @DisplayName("GET /api/categories/{id} -> 400 when not found")
    void getById_notFound() throws Exception {
        mockMvc.perform(get("/api/categories/{id}", UUID.randomUUID())
                        .with(user("any").roles("CLIENT")))
                .andExpect(status().isBadRequest());
    }
}
