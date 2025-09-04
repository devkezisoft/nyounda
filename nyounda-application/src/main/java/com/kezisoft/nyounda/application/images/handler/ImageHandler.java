package com.kezisoft.nyounda.application.images.handler;

import com.kezisoft.nyounda.application.images.command.ImageCreateCommand;
import com.kezisoft.nyounda.application.images.port.in.ImageUseCase;
import com.kezisoft.nyounda.application.images.port.out.ImageRepository;
import com.kezisoft.nyounda.application.images.port.out.ImageStorage;
import com.kezisoft.nyounda.domain.servicerequest.Image;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageHandler implements ImageUseCase {

    private final ImageStorage storage;
    private final ImageRepository repo;

    private static final Set<String> ALLOWED = Set.of("image/jpeg", "image/png");

    @Override
    public List<Image> upload(List<ImageCreateCommand> files) throws Exception {
        log.debug("Uploading {} files", files.stream().map(ImageCreateCommand::name));

        List<Image> saved = new ArrayList<>();

        for (ImageCreateCommand file : files) {
            String detected = Optional.ofNullable(file.contentType()).orElseGet(() -> {
                try {
                    return storage.detect(file.in().readAllBytes());
                } catch (Exception e) {
                    return null;
                }
            });

            if (detected == null || !ALLOWED.contains(detected)) {
                throw new IllegalArgumentException("Only PNG or JPEG allowed");
            }

            String ext = detected.equals("image/png") ? ".png" : ".jpg";
            // Key pattern: uploads/YYYY/MM/<uuid><ext>
            var today = LocalDate.now();
            String key = "uploads/%d/%02d/%s%s".formatted(
                    today.getYear(), today.getMonthValue(), UUID.randomUUID(), ext
            );

            var image = storage.store(key, file.in(), detected, file.size());
            saved.add(repo.save(image));
        }
        return saved;
    }

    @Override
    public void delete(UUID id) {
        var img = repo.findById(id).orElseThrow();
        String key = img.storageKey();
        repo.delete(img);
        storage.delete(key);
    }

    @Override
    public void delete(List<UUID> ids) {
        var images = repo.findAllImages(ids);
        images.forEach(img -> storage.delete(img.storageKey()));
        images.forEach(repo::delete);
    }

    @Override
    public List<Image> findAllImages(List<UUID> imageIds) {
        return repo.findAllImages(imageIds);
    }
}
