package com.serenia.platform.dailycheckin.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(name = "AnswerCheckInRequest", description = "Answer of the older adult to the check-in")
public record AnswerCheckInResource(
        @NotBlank(message = "{validation.not-blank}")
        @Pattern(regexp = "VERY_LOW|LOW|NEUTRAL|GOOD|VERY_GOOD", message = "{check.in.mood.invalid}")
        @Schema(description = "Mood level", example = "GOOD",
                allowableValues = {"VERY_LOW", "LOW", "NEUTRAL", "GOOD", "VERY_GOOD"})
        String mood,

        @Size(max = 200, message = "{validation.size.max}")
        @Schema(description = "Positive activity of the day", example = "Caminé en el parque", nullable = true)
        String positiveActivity) {
}
