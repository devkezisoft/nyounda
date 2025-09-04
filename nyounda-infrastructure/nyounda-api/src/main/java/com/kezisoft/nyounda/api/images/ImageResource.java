// src/main/java/.../api/upload/UploadResource.java
package com.kezisoft.nyounda.api.images;

import com.kezisoft.nyounda.api.images.response.ImageView;
import com.kezisoft.nyounda.application.images.command.ImageCreateCommand;
import com.kezisoft.nyounda.application.images.port.in.ImageUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageResource {

    private final ImageUseCase imageUseCase;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<ImageView> upload(@RequestPart("files") List<MultipartFile> files) throws Exception {
        var fileCreateCommands = files.stream().map(
                file -> {
                    try {
                        return new ImageCreateCommand(
                                file.getInputStream(),
                                file.getOriginalFilename(),
                                file.getContentType(),
                                file.getSize()
                        );
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
        ).toList();
        return imageUseCase.upload(fileCreateCommands).stream().map(ImageView::of).toList();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        imageUseCase.delete(id);
    }
}
