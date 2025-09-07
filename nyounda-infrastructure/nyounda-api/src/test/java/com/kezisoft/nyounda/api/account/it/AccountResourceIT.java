package com.kezisoft.nyounda.api.account.it;

import com.kezisoft.nyounda.api.account.request.AccountCreateRequest;
import com.kezisoft.nyounda.api.account.request.AccountUpdateRequest;
import com.kezisoft.nyounda.api.account.view.AccountView;
import com.kezisoft.nyounda.api.it.AbstractIntegrationTest;
import com.kezisoft.nyounda.domain.user.RegistrationType;
import com.kezisoft.nyounda.domain.user.User;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DirtiesContext
@Transactional
public class AccountResourceIT extends AbstractIntegrationTest {

    @Test
    @SneakyThrows
    @DisplayName("POST /api/register -> creates user and returns AccountView; then GET /api/accounts as that user")
    void registerAccount_createsUser_thenGet() {
        // Build request object instead of JSON string
        AccountCreateRequest request = new AccountCreateRequest(
                "Jane Doe",
                "jane.doe@example.com",
                "+237690000000",
                RegistrationType.CLIENT
        );

        String payload = objectMapper.writeValueAsString(request);

        var res = mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value("jane.doe@example.com"))
                .andReturn();

        AccountView accountView = objectMapper.readValue(res.getResponse().getContentAsString(), AccountView.class);

        mockMvc.perform(get("/api/accounts")
                        .with(user(accountView.id().toString()).roles("CLIENT"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(accountView.id().toString()))
                .andExpect(jsonPath("$.fullName").value(accountView.fullName()))
                .andExpect(jsonPath("$.email").value(accountView.email()));
    }

    @Test
    @DisplayName("PATCH /api/accounts -> updates current user and returns updated AccountView")
    void updateAccount_updatesFields() throws Exception {
        User user = seedUserClient("John Doe", "john.doe@example.com", "+237670000000");

        AccountUpdateRequest update = new AccountUpdateRequest(
                "John D.",
                "john.d@example.com",
                "+237674444444"
        );

        String payload = objectMapper.writeValueAsString(update);

        mockMvc.perform(patch("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user(user.id().toString()).roles("CLIENT"))
                        .content(payload)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.id().toString()))
                .andExpect(jsonPath("$.fullName").value("John D."))
                .andExpect(jsonPath("$.email").value("john.d@example.com"))
                .andExpect(jsonPath("$.phone").value("+237674444444"));
    }
}
