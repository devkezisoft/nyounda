package com.kezisoft.nyounda.api.images.it;

import com.fasterxml.jackson.core.type.TypeReference;
import com.kezisoft.nyounda.api.images.response.ImageView;
import com.kezisoft.nyounda.api.it.AbstractIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * IT for ImageResource:
 * - POST /api/images (multipart) -> returns List<ImageView>
 * - DELETE /api/images/{id}
 */
@Transactional
public class ImageResourceIT extends AbstractIntegrationTest {

    @Test
    @DisplayName("POST /api/images (multipart) -> uploads multiple files and returns ImageView list; then DELETE one")
    void upload_and_delete_ok() throws Exception {
        // If your security requires auth, attach a principal (not used by controller, but avoids 401)
        UUID principal = UUID.randomUUID();

        // --- prepare mock files
        byte[] pngBytes = "fake-png".getBytes(StandardCharsets.UTF_8);
        byte[] jpegBytes = "fake-jpeg".getBytes(StandardCharsets.UTF_8);

        MockMultipartFile file1 = new MockMultipartFile(
                "files",                      // request part name must be "files"
                "avatar.png",
                MediaType.IMAGE_PNG_VALUE,
                pngBytes
        );
        MockMultipartFile file2 = new MockMultipartFile(
                "files",
                "cover.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                jpegBytes
        );

        // --- call upload
        var uploadResult = mockMvc.perform(
                        MockMvcRequestBuilders.multipart("/api/images")
                                .file(file1)
                                .file(file2)
                                .with(user(principal.toString()).roles("CLIENT"))
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        // --- parse typed response into List<ImageView>
        String body = uploadResult.getResponse().getContentAsString();
        List<ImageView> views = objectMapper.readValue(body, new TypeReference<List<ImageView>>() {
        });
        assertThat(views).hasSize(2);
        assertThat(views.get(0).id()).isNotNull();
        assertThat(views.get(0).url()).isNotBlank();
        assertThat(views.get(1).id()).isNotNull();
        assertThat(views.get(1).url()).isNotBlank();

        // --- delete the first image
        UUID toDelete = views.get(0).id();
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/images/{id}", toDelete)
                        .with(user(principal.toString()).roles("CLIENT")))
                .andExpect(status().isOk()); // controller returns void -> 200 OK (no body)
    }

    @Test
    @DisplayName("POST /api/images with empty payload -> 400 Bad Request")
    void upload_empty_badRequest() throws Exception {
        UUID principal = UUID.randomUUID();

        // Missing "files" part -> Spring will complain with 400
        mockMvc.perform(
                        MockMvcRequestBuilders.multipart("/api/images")
                                .with(user(principal.toString()).roles("CLIENT"))
                        // no .file(...) added
                )
                .andExpect(status().isBadRequest());
    }
}
