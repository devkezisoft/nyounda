package com.kezisoft.nyounda.persistence.homeservice.jpa;

import com.kezisoft.nyounda.persistence.homeservice.entity.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaTagRepository extends JpaRepository<TagEntity, UUID> {
}
