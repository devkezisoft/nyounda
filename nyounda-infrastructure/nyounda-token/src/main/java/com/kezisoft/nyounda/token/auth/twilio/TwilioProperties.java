package com.kezisoft.nyounda.token.auth.twilio;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "twilio", ignoreUnknownFields = false)
public class TwilioProperties {
    private String accountSid;
    private String authToken;
    private String verifySid;
    private String apiSecret;
    private String apiSid;
}