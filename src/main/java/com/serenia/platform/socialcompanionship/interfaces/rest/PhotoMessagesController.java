package com.serenia.platform.socialcompanionship.interfaces.rest;

import com.serenia.platform.shared.infrastructure.security.AuthenticatedUserPrincipal;
import com.serenia.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import com.serenia.platform.socialcompanionship.domain.model.commands.DiscardPhotoMessageCommand;
import com.serenia.platform.socialcompanionship.domain.model.commands.SharePhotoMessageCommand;
import com.serenia.platform.socialcompanionship.domain.model.commands.ViewPhotoMessageCommand;
import com.serenia.platform.socialcompanionship.domain.model.queries.GetPhotoMessageMediaQuery;
import com.serenia.platform.socialcompanionship.domain.model.queries.GetSharedPhotoMessagesByCareCircleIdQuery;
import com.serenia.platform.socialcompanionship.domain.services.PhotoMessageCommandService;
import com.serenia.platform.socialcompanionship.domain.services.PhotoMessageQueryService;
import com.serenia.platform.socialcompanionship.interfaces.rest.resources.PhotoMessageResource;
import com.serenia.platform.socialcompanionship.interfaces.rest.resources.SelectPhotoMessageResource;
import com.serenia.platform.socialcompanionship.interfaces.rest.transform.MediaResponseAssembler;
import com.serenia.platform.socialcompanionship.interfaces.rest.transform.PhotoMessageResourceFromEntityAssembler;
import com.serenia.platform.socialcompanionship.interfaces.rest.transform.SelectPhotoMessageCommandFromResourceAssembler;
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
 * REST controller for the upload, sharing and discarding of photos by the relatives, and the
 * gallery and viewing by the older adult.
 */
@RestController
@RequestMapping(value = "/api/v1/care-circles/{careCircleId}/photo-messages", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Photo Messages", description = "Photos the relatives share with the older adult")
public class PhotoMessagesController {

    private final PhotoMessageCommandService photoMessageCommandService;
    private final PhotoMessageQueryService photoMessageQueryService;

    public PhotoMessagesController(PhotoMessageCommandService photoMessageCommandService,
                                   PhotoMessageQueryService photoMessageQueryService) {
        this.photoMessageCommandService = photoMessageCommandService;
        this.photoMessageQueryService = photoMessageQueryService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload photo", description = "Saves a JPEG or PNG photo selected by a relative as a draft.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Photo saved as a draft.",
                    content = @Content(schema = @Schema(implementation = PhotoMessageResource.class))),
            @ApiResponse(responseCode = "400", description = "Missing file."),
            @ApiResponse(responseCode = "403", description = "The user is not an actively linked relative of the circle."),
            @ApiResponse(responseCode = "413", description = "The file exceeds the maximum size."),
            @ApiResponse(responseCode = "415", description = "The file is not a JPEG or PNG image.")})
    public ResponseEntity<?> selectPhotoMessage(
            @PathVariable UUID careCircleId,
            @Valid @ModelAttribute SelectPhotoMessageResource resource,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        var command = SelectPhotoMessageCommandFromResourceAssembler.toCommandFromResource(careCircleId, principal.userId(), resource);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                photoMessageCommandService.handle(command),
                PhotoMessageResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.CREATED);
    }

    @PostMapping("/{photoMessageId}/share")
    @Operation(summary = "Share photo", description = "Shares the draft with the older adult of the circle.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Photo shared.",
                    content = @Content(schema = @Schema(implementation = PhotoMessageResource.class))),
            @ApiResponse(responseCode = "403", description = "Only the sender, while still linked, can share the draft."),
            @ApiResponse(responseCode = "404", description = "Photo not found."),
            @ApiResponse(responseCode = "409", description = "The photo was already shared.")})
    public ResponseEntity<?> sharePhotoMessage(
            @PathVariable UUID careCircleId,
            @PathVariable UUID photoMessageId,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        var command = new SharePhotoMessageCommand(photoMessageId, principal.userId());
        return ResponseEntityAssembler.toResponseEntityFromResult(
                photoMessageCommandService.handle(command),
                PhotoMessageResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.OK);
    }

    @DeleteMapping("/{photoMessageId}")
    @Operation(summary = "Discard photo", description = "Discards a draft before sharing it, deleting its file.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Photo discarded."),
            @ApiResponse(responseCode = "403", description = "Only the sender can discard the draft."),
            @ApiResponse(responseCode = "404", description = "Photo not found."),
            @ApiResponse(responseCode = "409", description = "The photo was already shared.")})
    public ResponseEntity<?> discardPhotoMessage(
            @PathVariable UUID careCircleId,
            @PathVariable UUID photoMessageId,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        var command = new DiscardPhotoMessageCommand(photoMessageId, principal.userId());
        return ResponseEntityAssembler.toResponseEntityFromResult(
                photoMessageCommandService.handle(command), _ -> null, HttpStatus.NO_CONTENT);
    }

    @GetMapping
    @Operation(summary = "Get shared photos",
            description = "Gets the gallery for the older adult, or the photos a relative sent, from the most recent to the oldest.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Photos retrieved."),
            @ApiResponse(responseCode = "403", description = "The user does not belong to the care circle.")})
    public ResponseEntity<?> getSharedPhotoMessages(
            @PathVariable UUID careCircleId,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        var query = new GetSharedPhotoMessagesByCareCircleIdQuery(careCircleId, principal.userId());
        return ResponseEntityAssembler.toResponseEntityFromResult(
                photoMessageQueryService.handle(query),
                messages -> messages.stream().map(PhotoMessageResourceFromEntityAssembler::toResourceFromEntity).toList(),
                HttpStatus.OK);
    }

    @GetMapping(value = "/{photoMessageId}/media", produces = MediaType.ALL_VALUE)
    @Operation(summary = "Get photo file", description = "Serves the image file to its sender or recipient.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image file."),
            @ApiResponse(responseCode = "403", description = "The user is neither the sender nor the recipient."),
            @ApiResponse(responseCode = "404", description = "Photo not found.")})
    public ResponseEntity<?> getPhotoMessageMedia(
            @PathVariable UUID careCircleId,
            @PathVariable UUID photoMessageId,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        var query = new GetPhotoMessageMediaQuery(photoMessageId, principal.userId());
        return MediaResponseAssembler.toResponseEntityFromResult(photoMessageQueryService.handle(query));
    }

    @PostMapping("/{photoMessageId}/view")
    @Operation(summary = "View photo", description = "Records that the older adult saw the photo; only the first view counts.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "View recorded.",
                    content = @Content(schema = @Schema(implementation = PhotoMessageResource.class))),
            @ApiResponse(responseCode = "403", description = "The user is not the recipient of the photo."),
            @ApiResponse(responseCode = "404", description = "Photo not found.")})
    public ResponseEntity<?> viewPhotoMessage(
            @PathVariable UUID careCircleId,
            @PathVariable UUID photoMessageId,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        var command = new ViewPhotoMessageCommand(photoMessageId, principal.userId());
        return ResponseEntityAssembler.toResponseEntityFromResult(
                photoMessageCommandService.handle(command),
                PhotoMessageResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.OK);
    }
}
