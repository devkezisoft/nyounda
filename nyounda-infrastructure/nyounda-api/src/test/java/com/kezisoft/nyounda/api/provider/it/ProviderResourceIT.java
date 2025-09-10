package com.kezisoft.nyounda.api.provider.it;

import com.kezisoft.nyounda.api.it.AbstractIntegrationTest;
import com.kezisoft.nyounda.api.provider.request.ProviderCreateRequest;
import com.kezisoft.nyounda.api.provider.request.ProviderSkillCreateRequest;
import com.kezisoft.nyounda.api.provider.request.ProviderSkillUpdateRequest;
import com.kezisoft.nyounda.api.provider.view.ProviderView;
import com.kezisoft.nyounda.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Full-stack IT for ProviderResource (no mocks).
 * - Seeds Provider user + Category/Subcategory
 * - Creates provider through API, reads it, updates it
 * - Uses ObjectMapper to (de)serialize typed DTOs/Views
 */
@Transactional
public class ProviderResourceIT extends AbstractIntegrationTest {


    private ProviderView createProviderViaApi(User currentUser, UUID categoryId, UUID subCategoryId, String location) throws Exception {
        var req = new ProviderCreateRequest(
                List.of(new ProviderSkillCreateRequest(
                        categoryId,
                        subCategoryId,
                        "Skilled in this subcategory"
                )),
                location
        );
        String payload = objectMapper.writeValueAsString(req);

        var res = mockMvc.perform(post("/api/providers")
                        .with(user(currentUser.id().toString()).roles("PROVIDER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn();

        // Parse into the View instead of manual JSON
        return objectMapper.readValue(res.getResponse().getContentAsString(), ProviderView.class);
    }

    // ---------- Tests -------------------------------------------------------

    @Test
    @DisplayName("POST /api/providers -> creates provider; GET /api/providers returns it for current user")
    void create_then_get_ok() throws Exception {
        // Seed a provider user + categories
        User u = seedUserProvider("Alice Pro", "alice.pro@example.com", "+237610000001");
        UUID[] cats = seedCategoryHierarchy("Plumbing", "Sink");
        UUID cat = cats[0], sub = cats[1];

        // Create provider
        ProviderView created = createProviderViaApi(u, cat, sub, "Paris 11");
        assertThat(created.id()).isNotNull();
        assertThat(created.location()).isEqualTo("Paris 11");
        assertThat(created.skills()).isNotNull();
        assertThat(created.skills()).isNotEmpty();

        // GET current provider (auth as same user)
        var getRes = mockMvc.perform(get("/api/providers")
                        .with(user(u.id().toString()).roles("PROVIDER")))
                .andExpect(status().isOk())
                .andReturn();

        ProviderView fetched = objectMapper.readValue(getRes.getResponse().getContentAsString(), ProviderView.class);
        assertThat(fetched.id()).isEqualTo(created.id());
        assertThat(fetched.location()).isEqualTo("Paris 11");
    }

    @Test
    @DisplayName("GET /api/providers -> 404 when current user has no provider")
    void get_noProvider_notFound() throws Exception {
        User u = seedUserProvider("Bob Pro", "bob.pro@example.com", "+237610000002");

        mockMvc.perform(get("/api/providers")
                        .with(user(u.id().toString()).roles("PROVIDER")))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PATCH /api/providers/{id} -> updates provider (location only) and returns updated view")
    void update_location_only_ok() throws Exception {
        User u = seedUserProvider("Carol Pro", "carol.pro@example.com", "+237610000003");
        UUID[] cats = seedCategoryHierarchy("Electrical", "Lighting");

        ProviderView created = createProviderViaApi(u, cats[0], cats[1], "Paris 9");
        UUID providerId = created.id();

        // Only update location; leave skills null so they are not changed
        var updateReq = new com.kezisoft.nyounda.api.provider.request.ProviderUpdateRequest(
                null,   // keep skills unchanged
                "Paris 15"
        );
        String payload = objectMapper.writeValueAsString(updateReq);

        var res = mockMvc.perform(patch("/api/providers/{id}", providerId)
                        .with(user(u.id().toString()).roles("PROVIDER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(providerId.toString()))
                .andExpect(jsonPath("$.location").value("Paris 15"))
                .andReturn();

        ProviderView updated = objectMapper.readValue(res.getResponse().getContentAsString(), ProviderView.class);
        assertThat(updated.location()).isEqualTo("Paris 15");
    }

    @Test
    @DisplayName("PATCH /api/providers/{id} -> updates provider skill (single category + description + experience)")
    void update_skill_singleCategory_ok() throws Exception {
        // 1) Seed provider user
        User u = seedUserProvider("Ethan Pro", "ethan.pro@example.com", "+237610000006");

        // 2) Seed categories
        // Treat the *leaf* as the skill's category (since skill has only one category field)
        UUID[] catsA = seedCategoryHierarchy("Plumbing", "Pipes");      // A (root), A1 (leaf)
        UUID initialCategoryId = catsA[1];

        UUID[] catsB = seedCategoryHierarchy("Electrical", "Lighting"); // B (root), B1 (leaf)
        UUID newCategoryId = catsB[1];

        // 3) Create provider with one initial skill bound to `initialCategoryId`
        ProviderView created = createProviderViaApi(u, catsA[0], initialCategoryId, "Paris 9");
        UUID providerId = created.id();
        assertThat(created.skills()).isNotNull().isNotEmpty();

        var existingSkill = created.skills().get(0);
        UUID providerSkillId = existingSkill.id();

        // 4) Build update request (single-category model)
        var skillUpdate = new ProviderSkillUpdateRequest(
                providerSkillId,
                null,
                newCategoryId,                          // switch category
                "Lighting expert - ceiling fixtures"   // new description
        );

        var updateReq = new com.kezisoft.nyounda.api.provider.request.ProviderUpdateRequest(
                List.of(skillUpdate),
                created.location() // keep location unchanged
        );

        String payload = objectMapper.writeValueAsString(updateReq);

        // 5) PATCH and parse typed response
        var res = mockMvc.perform(patch("/api/providers/{id}", providerId)
                        .with(user(u.id().toString()).roles("PROVIDER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andReturn();

        ProviderView updated = objectMapper.readValue(res.getResponse().getContentAsString(), ProviderView.class);
        assertThat(updated.id()).isEqualTo(providerId);
        assertThat(updated.skills()).isNotEmpty();

        var updatedSkill = updated.skills().stream()
                .filter(s -> s.id().equals(providerSkillId))
                .findFirst()
                .orElseThrow();

        // Assertions (single-category):
        assertThat(updatedSkill.description()).isEqualTo("Lighting expert - ceiling fixtures");

        // Category assertion — if ProviderSkillView exposes CategoryView with id:
        assertThat(updatedSkill.category()).isNotNull();
        assertThat(updatedSkill.category().id()).isEqualTo(newCategoryId);
    }

}
