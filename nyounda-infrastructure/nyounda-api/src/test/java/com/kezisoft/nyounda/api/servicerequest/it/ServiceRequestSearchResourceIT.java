package com.kezisoft.nyounda.api.servicerequest.it;

import com.kezisoft.nyounda.api.it.AbstractIntegrationTest;
import com.kezisoft.nyounda.api.servicerequests.request.CreateRequest;
import com.kezisoft.nyounda.api.servicerequests.response.ServiceRequestView;
import com.kezisoft.nyounda.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Full-stack IT for ServiceRequestSearchResource (no mocks).
 * - Seeds User + Category/Subcategory with JPA
 * - Creates requests through real API (POST /api/requests)
 * - Calls GET /api/requests-search with various params
 */
@Transactional
public class ServiceRequestSearchResourceIT extends AbstractIntegrationTest {

    /**
     * Create a request via API, returning the created request id
     */
    private UUID createRequestViaApi(User currentUser, UUID categoryId, UUID subCategoryId,
                                     String title, String description, String address) throws Exception {
        CreateRequest dto = new CreateRequest(
                null,                // controller ignores body.userId in favor of SecurityUtils
                categoryId,
                subCategoryId,
                title,
                description,
                address,
                List.of()            // image ids
        );
        String payload = objectMapper.writeValueAsString(dto);

        var res = mockMvc.perform(post("/api/requests")
                        .with(user(currentUser.id().toString()).roles("CLIENT"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andReturn();

        ServiceRequestView view = objectMapper.readValue(res.getResponse().getContentAsString(), ServiceRequestView.class);
        return view.id();
    }

    // ---------- Tests -------------------------------------------------------

    @Test
    @DisplayName("GET /api/requests-search -> returns a page of results (no filters)")
    void search_noFilters_returnsPage() throws Exception {
        // Seed user & categories
        User u = seedUserClient("Alice", "alice@example.com", "+237600000001");
        UUID[] cats = seedCategoryHierarchy("Plumbing", "Sink");
        UUID cat = cats[0], sub = cats[1];

        // Create a couple of requests
        createRequestViaApi(u, cat, sub, "Fix sink", "Leaking sink", "Paris 11");
        createRequestViaApi(u, cat, sub, "Install faucet", "New kitchen faucet", "Paris 15");

        // Search without filters (just page/size/sort)
        mockMvc.perform(get("/api/requests-search")
                        .with(user(u.id().toString()).roles("CLIENT"))
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "createdAt,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").exists())
                .andExpect(jsonPath("$.content[0].title").exists())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.number").value(0));
    }

    @Test
    @DisplayName("GET /api/requests-search -> filter by skillIds (subcategory) returns only matching")
    void search_filterBySkillIds_returnsOnlyMatching() throws Exception {
        User u = seedUserClient("Bob", "bob@example.com", "+237600000002");
        UUID[] plumb = seedCategoryHierarchy("Plumbing", "Sink");
        UUID[] electric = seedCategoryHierarchy("Electrical", "Lighting");

        // Create one per subcategory
        UUID r1 = createRequestViaApi(u, plumb[0], plumb[1], "Fix sink", "Leaking", "Paris 10");
        UUID r2 = createRequestViaApi(u, electric[0], electric[1], "Install lamp", "Ceiling", "Paris 9");

        // Filter by "Sink" subcategory id (treating skillIds as subcategory ids)
        mockMvc.perform(get("/api/requests-search")
                        .with(user(u.id().toString()).roles("CLIENT"))
                        .param("skillIds", plumb[1].toString()) // ?skillIds=<subId>
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Fix sink"));
    }

    @Test
    @DisplayName("GET /api/requests-search -> pagination works (size=1)")
    void search_pagination_size1() throws Exception {
        User u = seedUserClient("Carol", "carol@example.com", "+237600000003");
        UUID[] cats = seedCategoryHierarchy("Painting", "Interior");

        createRequestViaApi(u, cats[0], cats[1], "Paint living room", "Beige", "Paris 12");
        createRequestViaApi(u, cats[0], cats[1], "Paint bedroom", "White", "Paris 13");

        // First page (size=1)
        mockMvc.perform(get("/api/requests-search")
                        .with(user(u.id().toString()).roles("CLIENT"))
                        .param("page", "0")
                        .param("size", "1")
                        .param("sort", "createdAt,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.size").value(1))
                .andExpect(jsonPath("$.number").value(0));

        // Second page (size=1)
        mockMvc.perform(get("/api/requests-search")
                        .with(user(u.id().toString()).roles("CLIENT"))
                        .param("page", "1")
                        .param("size", "1")
                        .param("sort", "createdAt,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.size").value(1))
                .andExpect(jsonPath("$.number").value(1));
    }

    @Test
    @DisplayName("GET /api/requests-search -> address+radius params are accepted (no assertion on geo)")
    void search_withAddressAndRadius_paramsAccepted() throws Exception {
        User u = seedUserClient("Dave", "dave@example.com", "+237600000004");
        UUID[] cats = seedCategoryHierarchy("Cleaning", "Deep");

        createRequestViaApi(u, cats[0], cats[1], "Deep clean kitchen", "Floor+oven", "Paris 5");

        mockMvc.perform(get("/api/requests-search")
                        .with(user(u.id().toString()).roles("CLIENT"))
                        .param("address", "Paris")
                        .param("radiusKm", "10")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1));
    }
}
