package com.serenia.platform.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "ChangePasswordRequest", description = "Request payload for changing the password")
public record ChangePasswordResource(
        @NotBlank(message = "{validation.not-blank}")
        @Schema(description = "Current password", example = "secret123")
        String currentPassword,

        @NotBlank(message = "{validation.not-blank}")
        @Size(min = 8, max = 72, message = "{user.password.size}")
        @Schema(description = "New password", example = "newSecret456")
        String newPassword) {
}
