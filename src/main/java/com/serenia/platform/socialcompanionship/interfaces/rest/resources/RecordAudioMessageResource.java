package com.serenia.platform.socialcompanionship.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

@Schema(name = "RecordAudioMessageRequest", description = "Recorded audio, sent as a multipart form")
public record RecordAudioMessageResource(
        @NotNull(message = "{validation.not-blank}")
        @Schema(description = "AAC audio file (audio/mp4)", type = "string", format = "binary")
        MultipartFile file,

        @NotNull(message = "{validation.not-blank}")
        @Schema(description = "Duration of the audio in seconds, between 1 and 180", example = "42")
        Integer durationSeconds) {
}
