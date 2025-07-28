package com.kezisoft.nyounda.persistence.homeservice;

import com.kezisoft.nyounda.persistence.homeservice.entity.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringJpaTagRepository extends JpaRepository<TagEntity, UUID> {
}
