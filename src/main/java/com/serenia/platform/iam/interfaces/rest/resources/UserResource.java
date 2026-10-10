package com.serenia.platform.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.UUID;

@Schema(name = "UserResponse", description = "Public representation of an account, without sensitive data")
public record UserResource(
        @Schema(description = "User unique identifier", example = "3f2b8c9e-6a1d-4f0e-9b7a-2c5d8e1f4a6b")
        UUID id,

        @Schema(description = "Email address", example = "rosa.quispe@example.com")
        String email,

        @Schema(description = "Role", example = "OLDER_ADULT")
        String role,

        @Schema(description = "Full name", example = "Rosa Quispe Mamani")
        String fullName,

        @Schema(description = "Phone number in E.164 format", example = "+51987654321", nullable = true)
        String phoneNumber,

        @Schema(description = "Birth date", example = "1948-05-12", nullable = true)
        LocalDate birthDate,

        @Schema(description = "Profile photo URL", example = "https://example.com/photo.jpg", nullable = true)
        String photoUrl,

        @Schema(description = "Interface language and region", example = "es-PE")
        String locale,

        @Schema(description = "IANA time zone", example = "America/Lima")
        String timeZone,

        @Schema(description = "Account status", example = "ACTIVE")
        String status) {
}
