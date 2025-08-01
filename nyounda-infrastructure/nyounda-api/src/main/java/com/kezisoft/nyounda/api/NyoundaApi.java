package com.kezisoft.nyounda.api;

import com.kezisoft.nyounda.token.auth.jjwt.JwtProperties;
import com.kezisoft.nyounda.token.auth.twilio.TwilioProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(
        scanBasePackages = {
                "com.kezisoft.nyounda.api",
                "com.kezisoft.nyounda.application",
                "com.kezisoft.nyounda.persistence",
                "com.kezisoft.nyounda.token"
        }
)
@EnableConfigurationProperties({TwilioProperties.class, JwtProperties.class})
public class NyoundaApi {

    public static void main(String[] args) {
        SpringApplication.run(NyoundaApi.class, args);
    }

}
