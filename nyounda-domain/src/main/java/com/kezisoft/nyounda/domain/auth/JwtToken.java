package com.kezisoft.nyounda.domain.auth;

import java.time.Instant;

public record JwtToken(String token, Instant expiresAt) {
}