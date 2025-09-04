package com.kezisoft.nyounda.application.images.port.out;

import com.kezisoft.nyounda.domain.servicerequest.Image;

import java.io.InputStream;

public interface ImageStorage {

    /**
     * Store a binary object under a storage key; returns key + public URL.
     */
    Image store(String key, InputStream in, String contentType, long size) throws Exception;

    /**
     * Idempotent delete; should not fail if already gone.
     */
    void delete(String key);

    String detect(byte[] bytes);
}
