package com.kezisoft.nyounda.api.categories.it;

import com.kezisoft.nyounda.api.categories.response.CategoryView;
import com.kezisoft.nyounda.api.it.AbstractIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Full-stack IT for CategoriesResource using the pre-seeded Liquibase data.
 * - Does NOT insert categories in tests
 * - Verifies presence/structure of seeded roots and leaves
 */
@Transactional
public class CategoriesResourceIT extends AbstractIntegrationTest {

    // --- Known seeded IDs from db/data/categories.csv (roots & leaves) ---
    private static final UUID ROOT_PETITS_TRAVAUX =
            UUID.fromString("2a6d0e2c-0001-0000-0000-000000000001");
    private static final UUID LEAF_BRICOLEUR =
            UUID.fromString("11111111-1111-1111-1111-111111111106"); // under "Petits travaux"

    @Test
    @DisplayName("GET /api/categories -> contains seeded roots and nested subcategories")
    void getAll_containsSeededTree() throws Exception {
        var res = mockMvc.perform(get("/api/categories")
                        .with(user("any").roles("CLIENT")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // at least the 10 seeded roots (tolerate extra if other tests add more)
                .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(10)))
                // high-level presence check by name (roots)
                .andExpect(jsonPath("$..name",
                        hasItems("Petits travaux", "Aide ménagère", "Cours & Assistance")))
                .andReturn();

        // Parse to views for structural checks (no JsonNode)
        var body = res.getResponse().getContentAsString();
        CategoryView[] roots = objectMapper.readValue(body, CategoryView[].class);

        // Find "Petits travaux" and assert it has expected children like "Plombier", "Electricien"
        CategoryView petitsTravaux = Arrays.stream(roots)
                .filter(r -> "Petits travaux".equals(r.name()))
                .findFirst()
                .orElse(null);
        assertNotNull(petitsTravaux, "Expected seeded root 'Petits travaux'");

        List<String> ptChildren = petitsTravaux.subcategories().stream().map(CategoryView::name).toList();
        assertTrue(ptChildren.containsAll(List.of("Plombier", "Electricien")),
                "Expected Plombier & Electricien under 'Petits travaux'");

        // Also check another root: "Aide ménagère" has "Nettoyage" and "Cuisine"
        CategoryView aideMenagere = Arrays.stream(roots)
                .filter(r -> "Aide ménagère".equals(r.name()))
                .findFirst()
                .orElse(null);
        assertNotNull(aideMenagere, "Expected seeded root 'Aide ménagère'");
        List<String> aideChildren = aideMenagere.subcategories().stream().map(CategoryView::name).toList();
        assertTrue(aideChildren.containsAll(List.of("Nettoyage", "Cuisine")),
                "Expected Nettoyage & Cuisine under 'Aide ménagère'");
    }

    @Test
    @DisplayName("GET /api/categories/{id} -> returns a seeded root with its sub-tree")
    void getById_seededRoot_ok() throws Exception {
        var res = mockMvc.perform(get("/api/categories/{id}", ROOT_PETITS_TRAVAUX)
                        .with(user("any").roles("CLIENT")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        var view = objectMapper.readValue(res.getResponse().getContentAsString(), CategoryView.class);

        assertEquals(ROOT_PETITS_TRAVAUX, view.id());
        assertEquals("Petits travaux", view.name());
        assertFalse(view.subcategories().isEmpty(), "Root should have children");

        List<String> childNames = view.subcategories().stream().map(CategoryView::name).toList();
        assertTrue(childNames.containsAll(List.of("Plombier", "Electricien", "Peintre")),
                "Expected typical children under 'Petits travaux'");
    }

    @Test
    @DisplayName("GET /api/categories/{id} -> returns a seeded leaf (no subcategories)")
    void getById_seededLeaf_ok() throws Exception {
        var res = mockMvc.perform(get("/api/categories/{id}", LEAF_BRICOLEUR)
                        .with(user("any").roles("CLIENT")))
                .andExpect(status().isOk())
                .andReturn();

        var view = objectMapper.readValue(res.getResponse().getContentAsString(), CategoryView.class);

        assertEquals(LEAF_BRICOLEUR, view.id());
        assertEquals("Bricoleur", view.name());
        assertNotNull(view.subcategories());
        assertEquals(0, view.subcategories().size(), "Leaf should have no subcategories");
    }

    @Test
    @DisplayName("GET /api/categories/{id} -> 400 when category is not found")
    void getById_notFound_400() throws Exception {
        mockMvc.perform(get("/api/categories/{id}", UUID.randomUUID())
                        .with(user("any").roles("CLIENT")))
                .andExpect(status().isBadRequest());
    }
}
