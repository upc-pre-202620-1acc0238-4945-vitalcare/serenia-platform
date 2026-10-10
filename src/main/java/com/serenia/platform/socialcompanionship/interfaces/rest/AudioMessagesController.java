package com.serenia.platform.socialcompanionship.interfaces.rest;

import com.serenia.platform.shared.infrastructure.security.AuthenticatedUserPrincipal;
import com.serenia.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import com.serenia.platform.socialcompanionship.domain.model.commands.DiscardAudioMessageCommand;
import com.serenia.platform.socialcompanionship.domain.model.commands.PlayAudioMessageCommand;
import com.serenia.platform.socialcompanionship.domain.model.commands.ShareAudioMessageCommand;
import com.serenia.platform.socialcompanionship.domain.model.queries.GetAudioMessageMediaQuery;
import com.serenia.platform.socialcompanionship.domain.model.queries.GetSharedAudioMessagesByCareCircleIdQuery;
import com.serenia.platform.socialcompanionship.domain.services.AudioMessageCommandService;
import com.serenia.platform.socialcompanionship.domain.services.AudioMessageQueryService;
import com.serenia.platform.socialcompanionship.interfaces.rest.resources.AudioMessageResource;
import com.serenia.platform.socialcompanionship.interfaces.rest.resources.RecordAudioMessageResource;
import com.serenia.platform.socialcompanionship.interfaces.rest.transform.AudioMessageResourceFromEntityAssembler;
import com.serenia.platform.socialcompanionship.interfaces.rest.transform.MediaResponseAssembler;
import com.serenia.platform.socialcompanionship.interfaces.rest.transform.RecordAudioMessageCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * REST controller for the recording, sharing and discarding of audios by the older adult, and
 * their listing and playback by the relatives.
 *
 * <p>The audio is always recorded in the older adult's own circle; the {@code careCircleId} of
 * the path is used to list the circle's audios.</p>
 */
@RestController
@RequestMapping(value = "/api/v1/care-circles/{careCircleId}/audio-messages", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Audio Messages", description = "Voice messages of the older adult for the relatives")
public class AudioMessagesController {

    private final AudioMessageCommandService audioMessageCommandService;
    private final AudioMessageQueryService audioMessageQueryService;

    public AudioMessagesController(AudioMessageCommandService audioMessageCommandService,
                                   AudioMessageQueryService audioMessageQueryService) {
        this.audioMessageCommandService = audioMessageCommandService;
        this.audioMessageQueryService = audioMessageQueryService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Record audio", description = "Saves an AAC audio recorded by the older adult as a draft.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Audio saved as a draft.",
                    content = @Content(schema = @Schema(implementation = AudioMessageResource.class))),
            @ApiResponse(responseCode = "400", description = "Missing file or invalid duration."),
            @ApiResponse(responseCode = "403", description = "Only an older adult with a care circle can record audios."),
            @ApiResponse(responseCode = "413", description = "The file exceeds the maximum size."),
            @ApiResponse(responseCode = "415", description = "The file is not an AAC audio.")})
    public ResponseEntity<?> recordAudioMessage(
            @PathVariable UUID careCircleId,
            @Valid @ModelAttribute RecordAudioMessageResource resource,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        var command = RecordAudioMessageCommandFromResourceAssembler.toCommandFromResource(principal.userId(), resource);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                audioMessageCommandService.handle(command),
                message -> AudioMessageResourceFromEntityAssembler.toResourceFromEntity(message, principal.userId()),
                HttpStatus.CREATED);
    }

    @PostMapping("/{audioMessageId}/share")
    @Operation(summary = "Share audio", description = "Shares the draft with the relatives actively linked to the older adult.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Audio shared.",
                    content = @Content(schema = @Schema(implementation = AudioMessageResource.class))),
            @ApiResponse(responseCode = "403", description = "Only the sender can share the draft."),
            @ApiResponse(responseCode = "404", description = "Audio not found."),
            @ApiResponse(responseCode = "409", description = "The audio was already shared."),
            @ApiResponse(responseCode = "422", description = "The older adult has no linked relatives.")})
    public ResponseEntity<?> shareAudioMessage(
            @PathVariable UUID careCircleId,
            @PathVariable UUID audioMessageId,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        var command = new ShareAudioMessageCommand(audioMessageId, principal.userId());
        return ResponseEntityAssembler.toResponseEntityFromResult(
                audioMessageCommandService.handle(command),
                message -> AudioMessageResourceFromEntityAssembler.toResourceFromEntity(message, principal.userId()),
                HttpStatus.OK);
    }

    @DeleteMapping("/{audioMessageId}")
    @Operation(summary = "Discard audio", description = "Discards a draft before sharing it, deleting its file.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Audio discarded."),
            @ApiResponse(responseCode = "403", description = "Only the sender can discard the draft."),
            @ApiResponse(responseCode = "404", description = "Audio not found."),
            @ApiResponse(responseCode = "409", description = "The audio was already shared.")})
    public ResponseEntity<?> discardAudioMessage(
            @PathVariable UUID careCircleId,
            @PathVariable UUID audioMessageId,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        var command = new DiscardAudioMessageCommand(audioMessageId, principal.userId());
        return ResponseEntityAssembler.toResponseEntityFromResult(
                audioMessageCommandService.handle(command), _ -> null, HttpStatus.NO_CONTENT);
    }

    @GetMapping
    @Operation(summary = "Get shared audios",
            description = "Gets the shared audios the requester can play, from the most recent to the oldest.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Audios retrieved."),
            @ApiResponse(responseCode = "403", description = "The user does not belong to the care circle.")})
    public ResponseEntity<?> getSharedAudioMessages(
            @PathVariable UUID careCircleId,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        var query = new GetSharedAudioMessagesByCareCircleIdQuery(careCircleId, principal.userId());
        return ResponseEntityAssembler.toResponseEntityFromResult(
                audioMessageQueryService.handle(query),
                messages -> messages.stream()
                        .map(message -> AudioMessageResourceFromEntityAssembler.toResourceFromEntity(message, principal.userId()))
                        .toList(),
                HttpStatus.OK);
    }

    @GetMapping(value = "/{audioMessageId}/media", produces = MediaType.ALL_VALUE)
    @Operation(summary = "Get audio file",
            description = "Serves the audio file to its sender or recipients; supports byte-range requests.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Audio file."),
            @ApiResponse(responseCode = "206", description = "Requested range of the audio file."),
            @ApiResponse(responseCode = "403", description = "The user is neither the sender nor a recipient."),
            @ApiResponse(responseCode = "404", description = "Audio not found.")})
    public ResponseEntity<?> getAudioMessageMedia(
            @PathVariable UUID careCircleId,
            @PathVariable UUID audioMessageId,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        var query = new GetAudioMessageMediaQuery(audioMessageId, principal.userId());
        return MediaResponseAssembler.toResponseEntityFromResult(audioMessageQueryService.handle(query));
    }

    @PostMapping("/{audioMessageId}/play")
    @Operation(summary = "Play audio", description = "Records that the relative played the audio; only the first play counts.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Play recorded.",
                    content = @Content(schema = @Schema(implementation = AudioMessageResource.class))),
            @ApiResponse(responseCode = "403", description = "The user is not a recipient of the audio."),
            @ApiResponse(responseCode = "404", description = "Audio not found.")})
    public ResponseEntity<?> playAudioMessage(
            @PathVariable UUID careCircleId,
            @PathVariable UUID audioMessageId,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        var command = new PlayAudioMessageCommand(audioMessageId, principal.userId());
        return ResponseEntityAssembler.toResponseEntityFromResult(
                audioMessageCommandService.handle(command),
                message -> AudioMessageResourceFromEntityAssembler.toResourceFromEntity(message, principal.userId()),
                HttpStatus.OK);
    }
}
