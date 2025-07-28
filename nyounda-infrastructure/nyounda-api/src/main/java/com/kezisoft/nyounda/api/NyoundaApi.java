package com.kezisoft.nyounda.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
        scanBasePackages = {
                "com.kezisoft.nyounda.application",
                "com.kezisoft.nyounda.persistence"
        }
)
public class NyoundaApi {

    public static void main(String[] args) {
        SpringApplication.run(NyoundaApi.class, args);
    }

}
