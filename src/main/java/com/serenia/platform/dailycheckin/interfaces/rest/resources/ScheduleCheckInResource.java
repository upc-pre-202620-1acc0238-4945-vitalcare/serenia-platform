package com.serenia.platform.dailycheckin.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

@Schema(name = "ScheduleCheckInRequest", description = "Request payload for changing the time of the daily check-in")
public record ScheduleCheckInResource(
        @NotNull(message = "{validation.not-blank}")
        @Schema(description = "Local time, between 06:00 and 20:00", type = "string", example = "09:30")
        LocalTime reminderTime) {
}
