package com.kezisoft.nyounda.application.homeservice.port.out;

import com.kezisoft.nyounda.domain.homeservice.ServiceImage;

import java.util.List;
import java.util.UUID;

public interface ServiceImageRepository {
    List<ServiceImage> findAllImages(List<UUID> uuids);
}
