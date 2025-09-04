package com.kezisoft.nyounda.persistence;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@EnableJpaRepositories(basePackages = "com.kezisoft.nyounda.persistence")
@EntityScan(basePackages = "com.kezisoft.nyounda.persistence")
public class PersistenceConfiguration {

    @Value("${storage.s3.region}")
    String regionName;

    @Profile("prod")
    public S3Client s3Client() {
        return S3Client
                .builder()
                .region(Region.of(regionName))
                .build();
    }
}
