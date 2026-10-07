package com.serenia.platform.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Schema(name = "UpdateProfileDataRequest", description = "Request payload for updating the personal data of a user")
public record UpdateProfileDataResource(
        @NotBlank(message = "{validation.not-blank}")
        @Size(max = 120, message = "{validation.size.max}")
        @Schema(description = "Full name", example = "Rosa Quispe Mamani")
        String fullName,

        @Schema(description = "Phone number in E.164 format", example = "+51987654321", nullable = true)
        String phoneNumber,

        @PastOrPresent(message = "{user.birth.date.future}")
        @Schema(description = "Birth date", example = "1948-05-12", nullable = true)
        LocalDate birthDate,

        @NotBlank(message = "{validation.not-blank}")
        @Schema(description = "Interface language and region", example = "es-PE")
        String locale,

        @NotBlank(message = "{validation.not-blank}")
        @Schema(description = "IANA time zone", example = "America/Lima")
        String timeZone) {
}
