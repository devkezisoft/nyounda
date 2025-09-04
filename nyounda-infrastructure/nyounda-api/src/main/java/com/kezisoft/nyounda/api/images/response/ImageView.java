package com.kezisoft.nyounda.api.images.response;

import com.kezisoft.nyounda.domain.servicerequest.Image;

import java.util.UUID;

public record ImageView(UUID id, String url) {
    public static ImageView of(Image image) {
        return new ImageView(image.id(), image.url());
    }
}
