package com.serenia.platform.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Schema(name = "RegisterUserRequest", description = "Request payload for registering an older adult or a distant relative")
public record RegisterUserResource(
        @NotBlank(message = "{validation.not-blank}")
        @Email(message = "{validation.email}")
        @Size(max = 160, message = "{validation.size.max}")
        @Schema(description = "Email address", example = "rosa.quispe@example.com")
        String email,

        @NotBlank(message = "{validation.not-blank}")
        @Size(min = 8, max = 72, message = "{user.password.size}")
        @Schema(description = "Password", example = "secret123")
        String password,

        @NotBlank(message = "{validation.not-blank}")
        @Pattern(regexp = "OLDER_ADULT|DISTANT_RELATIVE", message = "{user.role.invalid}")
        @Schema(description = "Requested role", example = "OLDER_ADULT", allowableValues = {"OLDER_ADULT", "DISTANT_RELATIVE"})
        String role,

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
