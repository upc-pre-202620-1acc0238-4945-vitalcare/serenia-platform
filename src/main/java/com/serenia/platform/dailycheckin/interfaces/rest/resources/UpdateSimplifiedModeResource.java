package com.serenia.platform.dailycheckin.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(name = "UpdateSimplifiedModeRequest", description = "Request payload for enabling or disabling the simplified mode")
public record UpdateSimplifiedModeResource(
        @NotNull(message = "{validation.not-blank}")
        @Schema(description = "Whether the simplified mode must be enabled", example = "true")
        Boolean enabled) {
}
