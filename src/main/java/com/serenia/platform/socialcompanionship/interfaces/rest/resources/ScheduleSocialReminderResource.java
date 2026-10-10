package com.serenia.platform.socialcompanionship.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Schema(name = "ScheduleSocialReminderRequest", description = "Reminder at a local date and time of the older adult")
public record ScheduleSocialReminderResource(
        @NotBlank(message = "{validation.not-blank}")
        @Size(max = 120, message = "{validation.size.max}")
        @Schema(description = "Title", example = "Llamar a Marta")
        String title,

        @Size(max = 300, message = "{validation.size.max}")
        @Schema(description = "Description", example = "Preguntarle cómo le fue en su viaje", nullable = true)
        String description,

        @NotNull(message = "{validation.not-blank}")
        @Schema(description = "Local date and time, in the older adult's time zone", example = "2026-10-08T17:30:00")
        LocalDateTime remindAt) {
}
