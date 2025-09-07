package com.kezisoft.nyounda.api.servicerequest.it;

import com.fasterxml.jackson.databind.JsonNode;
import com.kezisoft.nyounda.api.it.AbstractIntegrationTest;
import com.kezisoft.nyounda.api.servicerequests.request.CreateRequest;
import com.kezisoft.nyounda.api.servicerequests.request.UpdateRequest;
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
 * Full-stack IT for ServiceRequestResource (no mocks).
 * Seeds User + Categories, uses Testcontainers Postgres from AbstractIntegrationTest.
 */
@Transactional
public class ServiceRequestResourceIT extends AbstractIntegrationTest {

    private UUID createRequest(UUID currentUserId, UUID categoryId, UUID subCategoryId, String title) throws Exception {
        CreateRequest dto = new CreateRequest(
                null, // controller overrides with current user id
                categoryId,
                subCategoryId,
                title,
                "Leaking sink",
                "42 Rue de Paris",
                List.of()
        );
        String payload = objectMapper.writeValueAsString(dto);

        var mvcRes = mockMvc.perform(post("/api/requests")
                        .with(user(currentUserId.toString()).roles("CLIENT"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andReturn();

        JsonNode json = objectMapper.readTree(mvcRes.getResponse().getContentAsString());
        return UUID.fromString(json.get("id").asText());
    }

    // ---- Tests -------------------------------------------------------------

    @Test
    @DisplayName("POST /api/requests -> creates a service request (201) and returns the view")
    void create_ok() throws Exception {
        UUID userId = seedUserClient("Alice", "alice@example.com", "+237600000001").id();
        UUID[] cats = seedCategoryHierarchy("Plumbing", "Sink");
        UUID categoryId = cats[0], subCategoryId = cats[1];

        UUID createdId = createRequest(userId, categoryId, subCategoryId, "Fix sink");
        assertThat(createdId).isNotNull();
    }

    @Test
    @DisplayName("GET /api/requests -> returns the list for the current user (200)")
    void findAllByCurrentUser_ok() throws Exception {
        UUID userId = seedUserClient("Bob", "bob@example.com", "+237600000002").id();
        UUID[] cats = seedCategoryHierarchy("Electrical", "Lighting");

        // create two requests
        createRequest(userId, cats[0], cats[1], "Install lamp");
        createRequest(userId, cats[0], cats[1], "Fix switch");

        mockMvc.perform(get("/api/requests")
                        .with(user(userId.toString()).roles("CLIENT")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("GET /api/requests/{id} -> returns one by id (200)")
    void findById_ok() throws Exception {
        UUID userId = seedUserClient("Carol", "carol@example.com", "+237600000003").id();
        UUID[] cats = seedCategoryHierarchy("Painting", "Interior");
        UUID id = createRequest(userId, cats[0], cats[1], "Paint bedroom");

        mockMvc.perform(get("/api/requests/{id}", id)
                        .with(user(userId.toString()).roles("CLIENT")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.title").value("Paint bedroom"));
    }

    @Test
    @DisplayName("PUT /api/requests/{id} -> updates an existing service request (200)")
    void update_ok() throws Exception {
        UUID userId = seedUserClient("Dave", "dave@example.com", "+237600000004").id();
        UUID[] cats = seedCategoryHierarchy("Plumbing", "Pipes");
        UUID id = createRequest(userId, cats[0], cats[1], "Fix pipe");

        // change title + description + address; keep categories (or change them if you want)
        UpdateRequest update = new UpdateRequest(
                id,
                cats[0],
                cats[1],
                "Fix pipe - urgent",
                "Burst pipe under sink",
                "12 Avenue de France",
                List.of()
        );
        String payload = objectMapper.writeValueAsString(update);

        mockMvc.perform(put("/api/requests/{id}", id)
                        .with(user(userId.toString()).roles("CLIENT"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.title").value("Fix pipe - urgent"))
                .andExpect(jsonPath("$.description").value("Burst pipe under sink"))
                .andExpect(jsonPath("$.address").value("12 Avenue de France"));
    }

    @Test
    @DisplayName("DELETE /api/requests/{id} -> 204 No Content; then GET by id should be 404")
    void delete_noContent_then_notFound() throws Exception {
        UUID userId = seedUserClient("Eve", "eve@example.com", "+237600000005").id();
        UUID[] cats = seedCategoryHierarchy("Cleaning", "Deep");
        UUID id = createRequest(userId, cats[0], cats[1], "Deep clean kitchen");

        mockMvc.perform(delete("/api/requests/{id}", id)
                        .with(user(userId.toString()).roles("CLIENT")))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/requests/{id}", id)
                        .with(user(userId.toString()).roles("CLIENT")))
                .andExpect(status().isNotFound());
    }
}
