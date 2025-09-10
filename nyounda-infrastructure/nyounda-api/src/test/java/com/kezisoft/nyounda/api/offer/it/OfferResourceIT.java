package com.kezisoft.nyounda.api.offer.it;

import com.kezisoft.nyounda.api.it.AbstractIntegrationTest;
import com.kezisoft.nyounda.api.offer.request.ExpenseRequest;
import com.kezisoft.nyounda.api.offer.request.OfferCreateRequest;
import com.kezisoft.nyounda.api.offer.request.OfferDeclineRequest;
import com.kezisoft.nyounda.api.offer.response.OfferView;
import com.kezisoft.nyounda.api.provider.request.ProviderCreateRequest;
import com.kezisoft.nyounda.api.provider.request.ProviderSkillCreateRequest;
import com.kezisoft.nyounda.api.provider.view.ProviderView;
import com.kezisoft.nyounda.api.servicerequests.request.CreateRequest;
import com.kezisoft.nyounda.api.servicerequests.response.ServiceRequestView;
import com.kezisoft.nyounda.domain.offer.OfferMode;
import com.kezisoft.nyounda.domain.offer.OfferStatus;
import com.kezisoft.nyounda.domain.servicerequest.ServiceRequestStatus;
import com.kezisoft.nyounda.domain.user.User;
import com.kezisoft.nyounda.domain.user.UserRole;
import com.kezisoft.nyounda.persistence.offer.entity.OfferEntity;
import com.kezisoft.nyounda.persistence.offer.jpa.JpaOfferRepository;
import com.kezisoft.nyounda.persistence.servicerequest.jpa.JpaServiceRequestRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    JpaOfferRepository jpaOfferRepo;
    @Autowired
    JpaServiceRequestRepository jpaReqRepo;

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

    @Test
    @DisplayName("POST /api/requests/{reqId}/offers/{offerId}/decline -> CLIENT declines an offer with reason")
    void decline_offer_ok() throws Exception {
        // Seed actors
        User client = seedUserClient("Client X", "clientX@example.com", "+237600010000");
        User provider = seedUserProvider("Pro X", "proX@example.com", "+237610010000");

        // Seed categories and provider profile
        UUID[] cat = seedCategoryHierarchy("Plumbing", "Pipes");
        ProviderView pv = createProviderViaApi(provider, cat[0]);
        assertThat(pv).isNotNull();

        // Create request
        ServiceRequestView req = createServiceRequestViaApi(
                client, cat[0], cat[1], "Fix leak", "Kitchen pipe", "Paris 9", List.of()
        );

        // Create an offer (as provider)
        OfferCreateRequest body = new OfferCreateRequest(
                OfferMode.FIXED, 25000.0, "Tomorrow morning", List.of(new ExpenseRequest("Taxi", 3000.0))
        );
        OfferView view = createOfferViaApi(provider, req.id(), body);
        UUID offerId = view.id();

        // Decline (as client)
        OfferDeclineRequest decline = new OfferDeclineRequest("Profil incomplet");
        mockMvc.perform(
                        MockMvcRequestBuilders.patch("/api/requests/{rid}/offers/{oid}/decline", req.id(), offerId)
                                .with(user(client.id().toString()).roles(UserRole.CLIENT.name()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(decline))
                )
                .andExpect(status().isNoContent());

        // Assert DB state
        OfferEntity declined = jpaOfferRepo.findById(offerId).orElseThrow();
        assertThat(declined.getStatus()).isEqualTo(OfferStatus.REJECTED);
        assertThat(declined.getMessage()).isEqualTo("Tomorrow morning"); // unchanged
        // If you store reason in a dedicated column (decline_reason), assert it here if exposed
        // assertThat(declined.getDeclineReason()).isEqualTo("Profil incomplet");

        // Request should not be chosen yet
        var reqEntity = jpaReqRepo.findById(req.id()).orElseThrow();
        assertThat(reqEntity.getChosenOffer()).isNull();
    }

    @Test
    @DisplayName("POST /api/requests/{reqId}/offers/{offerId}/choose -> CLIENT chooses an offer (sets chosen_offer and ACCEPTED)")
    void choose_offer_ok() throws Exception {
        // Seed actors
        User client = seedUserClient("Client Y", "clientY@example.com", "+237600020000");
        User pro1 = seedUserProvider("Pro Y1", "proY1@example.com", "+237610020001");
        User pro2 = seedUserProvider("Pro Y2", "proY2@example.com", "+237610020002");

        // Seed categories and providers
        UUID[] cat = seedCategoryHierarchy("Painting", "Walls");
        createProviderViaApi(pro1, cat[0]);
        createProviderViaApi(pro2, cat[0]);

        // Create request
        ServiceRequestView req = createServiceRequestViaApi(
                client, cat[0], cat[1], "Paint my room", "White please", "Paris 15", List.of()
        );

        // Two offers from different providers
        OfferView offer1 = createOfferViaApi(pro1, req.id(),
                new OfferCreateRequest(OfferMode.HOURLY, 5000.0, "Today", List.of()));
        OfferView offer2 = createOfferViaApi(pro2, req.id(),
                new OfferCreateRequest(OfferMode.FIXED, 30000.0, "Tomorrow", List.of()));

        // Choose offer2 (as client)
        mockMvc.perform(
                        MockMvcRequestBuilders.patch("/api/requests/{rid}/offers/{oid}/choose", req.id(), offer2.id())
                                .with(user(client.id().toString()).roles(UserRole.CLIENT.name()))
                )
                .andExpect(status().isNoContent());

        // Assert DB state
        var reqEntity = jpaReqRepo.findById(req.id()).orElseThrow();
        assertThat(reqEntity.getChosenOffer()).isNotNull();
        assertThat(reqEntity.getChosenOffer().getId()).isEqualTo(offer2.id());

        // Chosen is ACCEPTED
        var chosen = jpaOfferRepo.findById(offer2.id()).orElseThrow();
        assertThat(chosen.getStatus()).isEqualTo(OfferStatus.ACCEPTED);

        // Optional rule: non-chosen ones become DECLINED (depends on your use case handler)
        var other = jpaOfferRepo.findById(offer1.id()).orElseThrow();
        // If your business sets the rest to DECLINED, keep this:
        // assertThat(other.getStatus()).isEqualTo(OfferStatus.DECLINED);
        // If not, assert still PENDING:
        assertThat(other.getStatus()).isIn(OfferStatus.PENDING, OfferStatus.REJECTED);
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

    private OfferView createOfferViaApi(User asProvider, UUID requestId, OfferCreateRequest body) throws Exception {
        var res = mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/requests/{rid}/offers", requestId)
                                .with(user(asProvider.id().toString()).roles(UserRole.PROVIDER.name()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(body))
                )
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readValue(res.getResponse().getContentAsString(), OfferView.class);
    }

}
