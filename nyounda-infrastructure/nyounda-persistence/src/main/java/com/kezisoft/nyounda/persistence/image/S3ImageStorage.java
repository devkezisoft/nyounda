// src/main/java/.../image/S3ImageStorage.java
package com.kezisoft.nyounda.persistence.image;

import com.kezisoft.nyounda.application.images.port.out.ImageStorage;
import com.kezisoft.nyounda.domain.servicerequest.Image;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.InputStream;

@Service
@Profile("prod")
@RequiredArgsConstructor
public class S3ImageStorage implements ImageStorage {

    private final S3Client s3;
    @Value("${storage.s3.bucket}")
    String bucket;
    @Value("${storage.s3.public-base-url}")
    String publicBaseUrl; // e.g. https://cdn.example.com/
    private final Tika tika = new Tika();

    @Override
    public Image store(String key, InputStream in, String contentType, long size) {
        s3.putObject(b -> b.bucket(bucket).key(key).contentType(contentType),
                RequestBody.fromInputStream(in, size));
        return new Stored(key, publicBaseUrl + key);
    }

    @Override
    public void delete(String key) {
        try {
            s3.deleteObject(b -> b.bucket(bucket).key(key));
        } catch (Exception ignored) {
        }
    }

    @Override
    public String detect(byte[] bytes) {
        return tika.detect(bytes);
    }
}
