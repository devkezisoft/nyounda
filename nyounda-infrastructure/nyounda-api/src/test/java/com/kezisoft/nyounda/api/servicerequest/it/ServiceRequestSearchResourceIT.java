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
 * NOTE: Search excludes requests owned by the caller.
 */
@Transactional
public class ServiceRequestSearchResourceIT extends AbstractIntegrationTest {

    private UUID createRequestViaApi(
            User asUser, UUID categoryId, UUID subCategoryId,
            String title, String description, String address
    ) throws Exception {
        CreateRequest dto = new CreateRequest(
                null, categoryId, subCategoryId, title, description, address, List.of()
        );

        var res = mockMvc.perform(post("/api/requests")
                        .with(user(asUser.id().toString()).roles("CLIENT"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andReturn();

        ServiceRequestView view = objectMapper.readValue(res.getResponse().getContentAsString(), ServiceRequestView.class);
        return view.id();
    }

    @Test
    @DisplayName("GET /api/requests-search -> returns page but excludes current user's own requests")
    void search_noFilters_excludesCallerOwnRequests() throws Exception {
        // OWNER creates requests
        User owner = seedUserClient("Alice", "alice@example.com", "+237600000001");
        UUID[] cats = seedCategoryHierarchy("Plumbing", "Sink");
        UUID cat = cats[0], sub = cats[1];

        createRequestViaApi(owner, cat, sub, "Fix sink", "Leaking sink", "Paris 11");
        createRequestViaApi(owner, cat, sub, "Install faucet", "New kitchen faucet", "Paris 15");

        // VIEWER searches — should see owner’s 2 requests
        User viewer = seedUserClient("Viewer", "viewer@example.com", "+237600009999");

        mockMvc.perform(get("/api/requests-search")
                        .with(user(viewer.id().toString()).roles("CLIENT"))
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "createdAt,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(2));

        // OWNER searching their own should get 0 because of exclusion
        mockMvc.perform(get("/api/requests-search")
                        .with(user(owner.id().toString()).roles("CLIENT"))
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    @DisplayName("GET /api/requests-search -> filter by skillIds returns only matching (excluding caller’s own)")
    void search_filterBySkillIds_returnsOnlyMatching_notOwned() throws Exception {
        User owner = seedUserClient("Bob", "bob@example.com", "+237600000002");
        User viewer = seedUserClient("Eve", "eve@example.com", "+237600000012");

        UUID[] plumb = seedCategoryHierarchy("Plumbing", "Sink");
        UUID[] electric = seedCategoryHierarchy("Electrical", "Lighting");

        createRequestViaApi(owner, plumb[0], plumb[1], "Fix sink", "Leaking", "Paris 10");
        createRequestViaApi(owner, electric[0], electric[1], "Install lamp", "Ceiling", "Paris 9");

        mockMvc.perform(get("/api/requests-search")
                        .with(user(viewer.id().toString()).roles("CLIENT"))
                        .param("skillIds", plumb[1].toString())
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Fix sink"));
    }

    @Test
    @DisplayName("GET /api/requests-search -> pagination works (size=1) and excludes caller’s own")
    void search_pagination_size1_excludesCaller() throws Exception {
        User owner = seedUserClient("Carol", "carol@example.com", "+237600000003");
        User viewer = seedUserClient("Mallory", "mallory@example.com", "+237600000013");
        UUID[] cats = seedCategoryHierarchy("Painting", "Interior");

        createRequestViaApi(owner, cats[0], cats[1], "Paint living room", "Beige", "Paris 12");
        createRequestViaApi(owner, cats[0], cats[1], "Paint bedroom", "White", "Paris 13");

        // As viewer, should see 2 results, page by 1
        mockMvc.perform(get("/api/requests-search")
                        .with(user(viewer.id().toString()).roles("CLIENT"))
                        .param("page", "0")
                        .param("size", "1")
                        .param("sort", "createdAt,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.size").value(1))
                .andExpect(jsonPath("$.number").value(0));

        mockMvc.perform(get("/api/requests-search")
                        .with(user(viewer.id().toString()).roles("CLIENT"))
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
    @DisplayName("GET /api/requests-search -> address+radius accepted and results exclude caller’s own")
    void search_withAddressAndRadius_paramsAccepted_excludesCaller() throws Exception {
        User owner = seedUserClient("Dave", "dave@example.com", "+237600000004");
        User viewer = seedUserClient("Oscar", "oscar@example.com", "+237600000014");
        UUID[] cats = seedCategoryHierarchy("Cleaning", "Deep");

        createRequestViaApi(owner, cats[0], cats[1], "Deep clean kitchen", "Floor+oven", "Paris 5");

        mockMvc.perform(get("/api/requests-search")
                        .with(user(viewer.id().toString()).roles("CLIENT"))
                        .param("address", "Paris")
                        .param("radiusKm", "10")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1));
    }

    @Test
    @DisplayName("GET /api/requests-search -> combines criteria (skillIds + address + radius) and excludes caller’s own")
    void search_combinedCriteria_excludesCaller() throws Exception {
        User owner = seedUserClient("Frank", "frank@example.com", "+237600000005");
        User viewer = seedUserClient("Zoe", "zoe@example.com", "+237600000015");

        UUID[] plumbing = seedCategoryHierarchy("Plumbing", "Pipes");
        UUID[] painting = seedCategoryHierarchy("Painting", "Exterior");

        // Two requests in different categories/addresses
        createRequestViaApi(owner, plumbing[0], plumbing[1], "Fix pipes", "Bathroom pipes", "Paris 9");
        createRequestViaApi(owner, painting[0], painting[1], "Paint facade", "Outdoor", "Versailles");

        // Filter to plumbing subcategory + around Paris
        mockMvc.perform(get("/api/requests-search")
                        .with(user(viewer.id().toString()).roles("CLIENT"))
                        .param("skillIds", plumbing[1].toString())
                        .param("address", "Paris")
                        .param("radiusKm", "30")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Fix pipes"));
    }
}
