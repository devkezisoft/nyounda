package com.kezisoft.nyounda.token.auth.jjwt;

import com.kezisoft.nyounda.application.auth.port.out.JwtProvider;
import com.kezisoft.nyounda.domain.auth.Authentication;
import com.kezisoft.nyounda.domain.auth.JwtToken;
import com.kezisoft.nyounda.domain.user.User;
import com.kezisoft.nyounda.domain.user.UserRole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JJwtProvider implements JwtProvider {

    private static final String AUTHORITIES_KEY = "auth";
    public static final String SPRING_ROLE_PREFIX = "ROLE_";
    private final JwtProperties jwtProperties;
    private final JwtParser parser;

    public JJwtProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        parser = Jwts.parser().verifyWith(Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes())).build();
    }

    @Override
    public JwtToken createToken(User user) {
        String authorities = user.roles().stream()
                .map(userRole -> SPRING_ROLE_PREFIX + userRole.name())
                .collect(Collectors.joining(","));
        long now = (new Date()).getTime();
        long date = now + this.jwtProperties.getTokenValidityInSeconds() * 1000;
        Date validity = new Date(date);
        Key key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
        String compact = Jwts.builder()
                .subject(user.id().toString())
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(key)
                .expiration(validity)
                .compact();
        return new JwtToken(compact, Instant.ofEpochMilli(date));
    }

    @Override
    public boolean validateToken(String authToken) {
        try {
            parser.parse(authToken);
            return true;
        } catch (SignatureException e) {
            log.info("Invalid JWT signature.");
            log.trace("Invalid JWT signature trace: {}", e);
        } catch (MalformedJwtException e) {
            log.info("Invalid JWT token.");
            log.trace("Invalid JWT token trace: {}", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token.");
            log.trace("Expired JWT token trace: {}", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token.");
            log.trace("Unsupported JWT token trace: {}", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT token compact of handler are invalid.");
            log.trace("JWT token compact of handler are invalid trace: {}", e);
        }
        return false;
    }

    @Override
    public Optional<Authentication> retreiveAuthentication(String authToken) {
        try {
            Claims claims = parser.parseSignedClaims(authToken).getPayload();
            List<UserRole> authorities =
                    Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                            .map(r -> {
                                String domainRole = r.replaceFirst("^" + SPRING_ROLE_PREFIX, "");
                                return UserRole.valueOf(domainRole);
                            })
                            .collect(Collectors.toList());

            return Optional.of(new Authentication(claims.getSubject(), authToken, authorities));
        } catch (Exception e) {
            log.error("cannot parse JWT token: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
