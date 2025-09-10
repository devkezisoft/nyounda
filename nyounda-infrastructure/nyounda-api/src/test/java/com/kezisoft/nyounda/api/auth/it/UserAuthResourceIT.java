package com.kezisoft.nyounda.api.auth.it;

import com.kezisoft.nyounda.api.auth.request.VerifyPinRequest;
import com.kezisoft.nyounda.api.it.AbstractIntegrationTest;
import com.kezisoft.nyounda.domain.auth.Channel;
import com.kezisoft.nyounda.domain.auth.JwtToken;
import com.kezisoft.nyounda.domain.auth.VerificationStatus;
import com.kezisoft.nyounda.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
public class UserAuthResourceIT extends AbstractIntegrationTest {

    // ---------- /api/authenticate ------------------------------------------------

    @Test
    @DisplayName("GET /api/authenticate -> magic +23799999* shortcut returns PENDING (no provider call)")
    void authenticate_magicPrefix_pending() throws Exception {
        var phone = "+237999990001";

        var res = mockMvc.perform(get("/api/authenticate")
                        .param("phone", phone)
                        .param("channel", "SMS"))
                .andExpect(status().isOk())
                .andReturn();

        // parse enum from JSON string
        VerificationStatus status =
                objectMapper.readValue(res.getResponse().getContentAsString(), VerificationStatus.class);
        assertEquals(VerificationStatus.PENDING, status);

        // provider MUST NOT be called in magic path
        verify(pinCodeProvider, times(0)).send(anyString(), any());
    }

    @Test
    @DisplayName("GET /api/authenticate -> delegates to PinCodeProvider and returns APPROVED")
    void authenticate_providerApproved() throws Exception {
        var phone = "+237600000001";
        when(pinCodeProvider.send(eq(phone), eq(Channel.SMS)))
                .thenReturn(VerificationStatus.APPROVED);

        var res = mockMvc.perform(get("/api/authenticate")
                        .param("phone", phone)
                        .param("channel", "SMS"))
                .andExpect(status().isOk())
                .andReturn();

        VerificationStatus status =
                objectMapper.readValue(res.getResponse().getContentAsString(), VerificationStatus.class);
        assertEquals(VerificationStatus.APPROVED, status);

        verify(pinCodeProvider, times(1)).send(eq(phone), eq(Channel.SMS));
    }

    @Test
    @DisplayName("GET /api/authenticate -> provider canceled => 422 problem+json")
    void authenticate_providerCanceled_unprocessable() throws Exception {
        var phone = "+237600000002";
        when(pinCodeProvider.send(eq(phone), any()))
                .thenReturn(VerificationStatus.CANCELED);

        mockMvc.perform(get("/api/authenticate")
                        .param("phone", phone)
                        .param("channel", "SMS"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.title").value("PIN code generation was canceled by the provider"))
                .andExpect(jsonPath("$.type").value("https://www.nyounda.tech/problem/problem-with-message"))
                .andExpect(jsonPath("$.instance").value("/api/authenticate"))
                .andExpect(jsonPath("$.message").value("error.invalidPinCode"))
                .andExpect(jsonPath("$.params").value("auth"));

        verify(pinCodeProvider, times(1)).send(eq(phone), any());
    }


    // ---------- /api/verify -----------------------------------------------------

    @Test
    @DisplayName("POST /api/verify -> magic (+23799999*, pin 9999*) path returns JWT and Authorization header")
    void verify_magicPath_returnsJwt() throws Exception {
        var phone = "+237999990010";
        // must exist to create token
        seedUserClient("Magic User", "magic@example.com", phone);

        var req = new VerifyPinRequest(phone, "999912");
        var payload = objectMapper.writeValueAsString(req);

        var res = mockMvc.perform(post("/api/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(header().string("Authorization", startsWith("Bearer ")))
                .andReturn();

        JwtToken token = objectMapper.readValue(res.getResponse().getContentAsString(), JwtToken.class);
        assertNotNull(token);
        assertNotNull(token.token());
        assertFalse(token.token().isBlank());
        assertNotNull(token.expiresAt());

        // magic path should NOT hit provider.verify
        verify(pinCodeProvider, times(0)).verify(anyString(), anyString());
    }

    @Test
    @DisplayName("POST /api/verify -> normal flow calls provider.verify=true then returns JWT")
    void verify_normal_ok() throws Exception {
        var phone = "+237600000010";
        User u = seedUserClient("Alice Normal", "alice.normal@example.com", phone);

        when(pinCodeProvider.verify(eq(phone), eq("123456"))).thenReturn(true);

        var req = new VerifyPinRequest(phone, "123456");
        var payload = objectMapper.writeValueAsString(req);

        var res = mockMvc.perform(post("/api/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(header().string("Authorization", startsWith("Bearer ")))
                .andReturn();

        JwtToken token = objectMapper.readValue(res.getResponse().getContentAsString(), JwtToken.class);
        assertNotNull(token.token());
        assertNotNull(token.expiresAt());

        // header token should match body token
        String authHeader = res.getResponse().getHeader("Authorization");
        assertNotNull(authHeader);
        assertTrue(authHeader.startsWith("Bearer "));
        assertEquals("Bearer " + token.token(), authHeader);

        verify(pinCodeProvider, times(1)).verify(eq(phone), eq("123456"));
        // no send() expected here
        verify(pinCodeProvider, never()).send(anyString(), any());
    }

    @Test
    @DisplayName("POST /api/verify -> invalid pin (provider.verify=false) returns 400")
    void verify_invalidPin_badRequest() throws Exception {
        var phone = "+237600000011";
        seedUserClient("Bob Invalid", "bob.invalid@example.com", phone);

        when(pinCodeProvider.verify(eq(phone), eq("000000"))).thenReturn(false);

        var req = new VerifyPinRequest(phone, "000000");
        var payload = objectMapper.writeValueAsString(req);

        mockMvc.perform(post("/api/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest());

        verify(pinCodeProvider, times(1)).verify(eq(phone), eq("000000"));
    }
}
