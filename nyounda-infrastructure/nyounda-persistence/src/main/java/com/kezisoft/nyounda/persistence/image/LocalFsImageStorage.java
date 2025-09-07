package com.kezisoft.nyounda.persistence.image;

import com.kezisoft.nyounda.application.images.port.out.ImageStorage;
import com.kezisoft.nyounda.domain.servicerequest.Image;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Component
@Profile({"local", "test"})
@RequiredArgsConstructor
public class LocalFsImageStorage implements ImageStorage {

    @Value("${storage.local.base-dir}")
    private String baseDir;                 // e.g. /var/nyounda/uploads
    @Value("${storage.local.public-base-url}")
    private String publicBaseUrl;// e.g. http://localhost:8080/

    private final Tika tika = new Tika();

    @Override
    public Image store(String key, InputStream in, String contentType, long size) throws Exception {
        Path dest = Path.of(baseDir).resolve(key).normalize();
        Files.createDirectories(dest.getParent());
        Files.copy(in, dest, StandardCopyOption.REPLACE_EXISTING);
        return new Image(
                UUID.randomUUID(),
                publicBaseUrl + key,
                key,
                contentType,
                size
        );
    }

    @Override
    public void delete(String key) {
        try {
            Files.deleteIfExists(Path.of(baseDir).resolve(key).normalize());
        } catch (Exception ignored) {
        }
    }

    @Override
    public String detect(byte[] bytes) {
        return tika.detect(bytes);
    }
}
