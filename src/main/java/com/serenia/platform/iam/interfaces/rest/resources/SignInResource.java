package com.serenia.platform.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "SignInRequest", description = "Credentials sent to request a session")
public record SignInResource(
        @NotBlank(message = "{validation.not-blank}")
        @Email(message = "{validation.email}")
        @Schema(description = "Email address", example = "rosa.quispe@example.com")
        String email,

        @NotBlank(message = "{validation.not-blank}")
        @Schema(description = "Password", example = "secret123")
        String password,

        @Size(max = 200, message = "{validation.size.max}")
        @Schema(description = "Description of the device", example = "Android 14 - Samsung A54", nullable = true)
        String deviceInfo) {
}
