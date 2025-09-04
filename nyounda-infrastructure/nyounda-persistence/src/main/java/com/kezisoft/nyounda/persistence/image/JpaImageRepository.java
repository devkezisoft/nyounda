package com.kezisoft.nyounda.persistence.image;

import com.kezisoft.nyounda.persistence.image.entity.ImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaImageRepository extends JpaRepository<ImageEntity, UUID> {
}
