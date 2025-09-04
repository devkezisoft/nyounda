package com.kezisoft.nyounda.persistence.servicerequest.jpa;

import com.kezisoft.nyounda.persistence.servicerequest.entity.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaTagRepository extends JpaRepository<TagEntity, UUID> {
}
