package com.kezisoft.nyounda.application.images.port.out;

import com.kezisoft.nyounda.domain.servicerequest.Image;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ImageRepository {
    List<Image> findAllImages(List<UUID> uuids);

    Image save(Image image);

    Optional<Image> findById(UUID id);

    void delete(Image img);
}
