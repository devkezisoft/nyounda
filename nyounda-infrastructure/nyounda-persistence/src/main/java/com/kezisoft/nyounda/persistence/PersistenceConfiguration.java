package com.kezisoft.nyounda.persistence;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.kezisoft.nyounda.persistence")
@EntityScan(basePackages = "com.kezisoft.nyounda.persistence")
public class PersistenceConfiguration {
}
