package com.kezisoft.nyounda.application.images.port.in;

import com.kezisoft.nyounda.application.images.command.ImageCreateCommand;
import com.kezisoft.nyounda.domain.servicerequest.Image;

import java.util.List;
import java.util.UUID;

public interface ImageUseCase {

    List<Image> upload(List<ImageCreateCommand> files) throws Exception;

    void delete(UUID id);
}
