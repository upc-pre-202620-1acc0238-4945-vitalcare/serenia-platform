package com.serenia.platform.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

@Schema(name = "UploadUserPhotoRequest", description = "Profile photo, sent as a multipart form")
public record UploadUserPhotoResource(
        @NotNull(message = "{validation.not-blank}")
        @Schema(description = "JPEG or PNG image", type = "string", format = "binary")
        MultipartFile file) {
}
