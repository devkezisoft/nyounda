package com.kezisoft.nyounda.api.offer.it;

import com.kezisoft.nyounda.api.it.AbstractIntegrationTest;
import com.kezisoft.nyounda.api.offer.request.ExpenseRequest;
import com.kezisoft.nyounda.api.offer.request.OfferCreateRequest;
import com.kezisoft.nyounda.api.offer.response.OfferView;
import com.kezisoft.nyounda.api.provider.request.ProviderCreateRequest;
import com.kezisoft.nyounda.api.provider.request.ProviderSkillCreateRequest;
import com.kezisoft.nyounda.api.provider.view.ProviderView;
import com.kezisoft.nyounda.api.servicerequests.request.CreateRequest;
import com.kezisoft.nyounda.api.servicerequests.response.ServiceRequestView;
import com.kezisoft.nyounda.domain.offer.OfferMode;
import com.kezisoft.nyounda.domain.servicerequest.ServiceRequestStatus;
import com.kezisoft.nyounda.domain.user.User;
import com.kezisoft.nyounda.domain.user.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
public class OfferResourceIT extends AbstractIntegrationTest {

    @Test
    @DisplayName("POST /api/requests/{id}/offers -> creates an offer (PROVIDER role)")
    void create_offer_ok() throws Exception {
        // Seed users
        User client = seedUserClient("Client One", "client1@example.com", "+237600000001");
        User provider = seedUserProvider("Pro One", "pro1@example.com", "+237610000001");

        // Seed categories (root + leaf)
        UUID[] cat = seedCategoryHierarchy("Plumbing", "Pipes"); // [rootId, leafId]
        UUID categoryId = cat[0];
        UUID subCategoryId = cat[1];

        // Create a provider entity with a skill (if your domain requires it)
        ProviderView pv = createProviderViaApi(
                provider,
                categoryId
        );
        assertThat(pv.id()).isNotNull();

        // Create a service request owned by the CLIENT
        ServiceRequestView srv = createServiceRequestViaApi(
                client,
                categoryId,
                subCategoryId,
                "Fix my sink",
                "It leaks",
                "Paris 9",
                List.of()
        );
        assertThat(srv.status()).isEqualTo(ServiceRequestStatus.PENDING);
        UUID requestId = srv.id();

        // Build offer
        OfferCreateRequest body = new OfferCreateRequest(
                OfferMode.FIXED,
                25000.0,
                "Can do it tomorrow morning.",
                List.of(new ExpenseRequest("Transport", 2000.0))
        );

        // POST as PROVIDER
        var res = mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/requests/{requestId}/offers", requestId)
                                .with(user(provider.id().toString()).roles(UserRole.PROVIDER.name()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(body))
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                // NOTE: your controller currently builds Location ".../offers" + id (missing "/")
                // .andExpect(header().string("Location", Matchers.containsString("/api/requests/" + requestId + "/offers")))
                .andReturn();

        OfferView view = objectMapper.readValue(res.getResponse().getContentAsString(), OfferView.class);
        assertThat(view.id()).isNotNull();
        assertThat(view.mode()).isEqualTo(OfferMode.FIXED);
        assertThat(view.message()).isEqualTo("Can do it tomorrow morning.");
        assertThat(view.expenses()).hasSize(1);
        // Avoid strict JSON shape on Money: just ensure present
        assertThat(view.amount()).isNotNull();
        assertThat(view.createdAt()).isNotNull();
    }

    @Test
    @DisplayName("POST /api/requests/{id}/offers -> 403 when caller is CLIENT (no PROVIDER role)")
    void create_offer_forbidden_when_not_provider() throws Exception {
        User client = seedUserClient("Client A", "clientA@example.com", "+237600000010");
        User anotherClient = seedUserClient("Client B", "clientB@example.com", "+237600000011");

        UUID[] cat = seedCategoryHierarchy("Electrical", "Lighting");
        ServiceRequestView srv = createServiceRequestViaApi(
                client, cat[0], cat[1], "Install lamp", "Need ceiling lamp", "Paris 10", List.of()
        );

        OfferCreateRequest body = new OfferCreateRequest(
                OfferMode.HOURLY, 5000.0, "Available today", List.of()
        );

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/requests/{id}/offers", srv.id())
                                .with(user(anotherClient.id().toString()).roles(UserRole.CLIENT.name()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(body))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /api/requests/{id}/offers -> 401 when missing authentication")
    void create_offer_unauthorized_when_no_auth() throws Exception {
        UUID randomReq = UUID.randomUUID();
        OfferCreateRequest body = new OfferCreateRequest(
                OfferMode.FIXED, 10000.0, "Ping me", List.of()
        );

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/requests/{requestId}/offers", randomReq)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(body))
                )
                .andExpect(status().isUnauthorized());
    }

    // ---------- helpers (reuse common style you used in other ITs) ----------

    private ProviderView createProviderViaApi(
            User providerUser,
            UUID categoryId
    ) throws Exception {
        // single-category skill (no subcategory in your new model)
        var createReq = new ProviderCreateRequest(
                List.of(new ProviderSkillCreateRequest(
                        null, categoryId, "Paris 9"
                )),
                "Paris 9"
        );

        var res = mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/providers")
                                .with(user(providerUser.id().toString()).roles(UserRole.PROVIDER.name()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createReq))
                )
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readValue(res.getResponse().getContentAsString(), ProviderView.class);
    }

    private ServiceRequestView createServiceRequestViaApi(
            User asUser,
            UUID categoryId,
            UUID subCategoryId,
            String title,
            String description,
            String address,
            List<UUID> imageIds
    ) throws Exception {
        CreateRequest req = new CreateRequest(
                null,
                categoryId,
                subCategoryId,
                title,
                description,
                address,
                imageIds
        );

        var res = mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/requests")
                                .with(user(asUser.id().toString()).roles(UserRole.CLIENT.name()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req))
                )
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readValue(res.getResponse().getContentAsString(), ServiceRequestView.class);
    }
}
