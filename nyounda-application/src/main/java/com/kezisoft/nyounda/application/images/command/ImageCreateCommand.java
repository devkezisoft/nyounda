package com.kezisoft.nyounda.application.images.command;

import java.io.InputStream;

public record ImageCreateCommand(
        InputStream in, String name, String contentType, long size
) {
}
