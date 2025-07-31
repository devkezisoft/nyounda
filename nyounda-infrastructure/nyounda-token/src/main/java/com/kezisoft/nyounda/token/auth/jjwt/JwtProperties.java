package com.kezisoft.nyounda.token.auth.jjwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "jwt", ignoreUnknownFields = false)
public class JwtProperties {
    private String secret;
    private long tokenValidityInSeconds;
}
