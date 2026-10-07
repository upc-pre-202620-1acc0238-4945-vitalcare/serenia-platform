package com.serenia.platform.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "UpdateUserPhotoRequest", description = "Request payload for updating the profile photo")
public record UpdateUserPhotoResource(
        @NotBlank(message = "{user.photo.url.blank}")
        @Schema(description = "Profile photo URL", example = "https://example.com/photo.jpg")
        String photoUrl) {
}
