package com.kezisoft.nyounda.persistence.image.entity;

import com.kezisoft.nyounda.domain.servicerequest.Image;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "image")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImageEntity {
    @Id
    private UUID id;

    @Column(name = "storage_key", nullable = false, unique = true)
    private String storageKey;

    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = "mime_type", nullable = false)
    private String mimeType;

    @Column(name = "size_bytes", nullable = false)
    private long sizeBytes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    public static ImageEntity fromDomain(Image image) {
        return new ImageEntity(
                image.id(),
                image.storageKey(),
                image.url(),
                image.mimeType(),
                image.sizeBytes(),
                null
        );
    }

    public Image toDomain() {
        return new Image(id, url, storageKey, mimeType, sizeBytes);
    }
}
