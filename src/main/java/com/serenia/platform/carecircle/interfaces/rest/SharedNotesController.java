package com.serenia.platform.carecircle.interfaces.rest;

import com.serenia.platform.carecircle.domain.model.queries.GetSharedNotesByCareCircleIdQuery;
import com.serenia.platform.carecircle.domain.services.SharedNoteCommandService;
import com.serenia.platform.carecircle.domain.services.SharedNoteQueryService;
import com.serenia.platform.carecircle.interfaces.rest.resources.CreateSharedNoteResource;
import com.serenia.platform.carecircle.interfaces.rest.resources.EditSharedNoteResource;
import com.serenia.platform.carecircle.interfaces.rest.resources.SharedNoteResource;
import com.serenia.platform.carecircle.interfaces.rest.transform.CreateSharedNoteCommandFromResourceAssembler;
import com.serenia.platform.carecircle.interfaces.rest.transform.EditSharedNoteCommandFromResourceAssembler;
import com.serenia.platform.carecircle.interfaces.rest.transform.SharedNoteResourceFromEntityAssembler;
import com.serenia.platform.shared.infrastructure.security.AuthenticatedUserPrincipal;
import com.serenia.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * REST controller for the creation, listing and edition of shared notes.
 */
@RestController
@RequestMapping(value = "/api/v1/care-circles/{careCircleId}/shared-notes", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Shared Notes", description = "Shared note endpoints")
public class SharedNotesController {

    private final SharedNoteCommandService sharedNoteCommandService;
    private final SharedNoteQueryService sharedNoteQueryService;

    public SharedNotesController(SharedNoteCommandService sharedNoteCommandService,
                                 SharedNoteQueryService sharedNoteQueryService) {
        this.sharedNoteCommandService = sharedNoteCommandService;
        this.sharedNoteQueryService = sharedNoteQueryService;
    }

    @PostMapping
    @Operation(summary = "Create shared note", description = "Writes a note visible to every relative of the circle.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Shared note created successfully.",
                    content = @Content(schema = @Schema(implementation = SharedNoteResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data."),
            @ApiResponse(responseCode = "403", description = "The user is not an active relative of the circle."),
            @ApiResponse(responseCode = "404", description = "Care circle not found.")})
    public ResponseEntity<?> createSharedNote(
            @PathVariable UUID careCircleId,
            @Valid @RequestBody CreateSharedNoteResource resource,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        var command = CreateSharedNoteCommandFromResourceAssembler.toCommandFromResource(careCircleId, principal.userId(), resource);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                sharedNoteCommandService.handle(command),
                SharedNoteResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get shared notes", description = "Gets the notes of the circle, from the most recent to the oldest.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Shared notes retrieved successfully."),
            @ApiResponse(responseCode = "403", description = "The user is neither the owner nor an active relative."),
            @ApiResponse(responseCode = "404", description = "Care circle not found.")})
    public ResponseEntity<?> getSharedNotes(
            @PathVariable UUID careCircleId,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        var query = new GetSharedNotesByCareCircleIdQuery(careCircleId, principal.userId());
        return ResponseEntityAssembler.toResponseEntityFromResult(
                sharedNoteQueryService.handle(query),
                sharedNotes -> sharedNotes.stream().map(SharedNoteResourceFromEntityAssembler::toResourceFromEntity).toList(),
                HttpStatus.OK);
    }

    @PutMapping("/{sharedNoteId}")
    @Operation(summary = "Edit shared note", description = "Replaces the content of a note; only its author can edit it.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Shared note edited successfully.",
                    content = @Content(schema = @Schema(implementation = SharedNoteResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data."),
            @ApiResponse(responseCode = "403", description = "The user is not the author of the note."),
            @ApiResponse(responseCode = "404", description = "Shared note not found.")})
    public ResponseEntity<?> editSharedNote(
            @PathVariable UUID careCircleId,
            @PathVariable UUID sharedNoteId,
            @Valid @RequestBody EditSharedNoteResource resource,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        var command = EditSharedNoteCommandFromResourceAssembler.toCommandFromResource(sharedNoteId, principal.userId(), resource);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                sharedNoteCommandService.handle(command),
                SharedNoteResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.OK);
    }
}
