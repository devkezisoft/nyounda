package com.kezisoft.nyounda.persistence.image;

import com.kezisoft.nyounda.application.images.port.out.ImageRepository;
import com.kezisoft.nyounda.domain.servicerequest.Image;
import com.kezisoft.nyounda.persistence.image.entity.ImageEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ImageRepositoryAdapter implements ImageRepository {

    private final JpaImageRepository repository;

    @Override
    public List<Image> findAllImages(List<UUID> ids) {
        return repository.findAllById(ids)
                .stream()
                .map(ImageEntity::toDomain)
                .toList();
    }

    @Override
    public Image save(Image image) {
        return repository.save(ImageEntity.fromDomain(image)).toDomain();
    }

    @Override
    public Optional<Image> findById(UUID id) {
        return repository.findById(id).map(ImageEntity::toDomain);
    }

    @Override
    public void delete(Image img) {
        repository.deleteById(img.id());
    }
}
