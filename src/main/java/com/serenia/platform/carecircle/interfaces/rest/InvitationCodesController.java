package com.serenia.platform.carecircle.interfaces.rest;

import com.serenia.platform.carecircle.domain.model.commands.GenerateInvitationCodeCommand;
import com.serenia.platform.carecircle.domain.model.queries.GetPendingInvitationCodesByCareCircleIdQuery;
import com.serenia.platform.carecircle.domain.services.CareCircleCommandService;
import com.serenia.platform.carecircle.domain.services.CareCircleQueryService;
import com.serenia.platform.carecircle.interfaces.rest.resources.InvitationCodeResource;
import com.serenia.platform.carecircle.interfaces.rest.transform.InvitationCodeResourceFromEntityAssembler;
import com.serenia.platform.shared.infrastructure.security.AuthenticatedUserPrincipal;
import com.serenia.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * REST controller for the generation of invitation codes by the older adult and the
 * query of the codes that can still be redeemed.
 */
@RestController
@RequestMapping(value = "/api/v1/care-circles/{careCircleId}/invitation-codes", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Invitation Codes", description = "Invitation code endpoints")
public class InvitationCodesController {

    private final CareCircleCommandService careCircleCommandService;
    private final CareCircleQueryService careCircleQueryService;

    public InvitationCodesController(CareCircleCommandService careCircleCommandService,
                                     CareCircleQueryService careCircleQueryService) {
        this.careCircleCommandService = careCircleCommandService;
        this.careCircleQueryService = careCircleQueryService;
    }

    @PostMapping
    @Operation(summary = "Generate invitation code", description = "Generates a code the older adult shares with a relative.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Invitation code generated successfully.",
                    content = @Content(schema = @Schema(implementation = InvitationCodeResource.class))),
            @ApiResponse(responseCode = "403", description = "Only the owner older adult can generate codes."),
            @ApiResponse(responseCode = "404", description = "Care circle not found.")})
    public ResponseEntity<?> generateInvitationCode(
            @PathVariable UUID careCircleId,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        var command = new GenerateInvitationCodeCommand(careCircleId, principal.userId());
        return ResponseEntityAssembler.toResponseEntityFromResult(
                careCircleCommandService.handle(command),
                InvitationCodeResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get pending invitation codes", description = "Gets the codes of the circle that can still be redeemed.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Invitation codes retrieved successfully."),
            @ApiResponse(responseCode = "403", description = "The user is neither the owner nor an active relative."),
            @ApiResponse(responseCode = "404", description = "Care circle not found.")})
    public ResponseEntity<?> getPendingInvitationCodes(
            @PathVariable UUID careCircleId,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        var query = new GetPendingInvitationCodesByCareCircleIdQuery(careCircleId, principal.userId());
        return ResponseEntityAssembler.toResponseEntityFromResult(
                careCircleQueryService.handle(query),
                codes -> codes.stream().map(InvitationCodeResourceFromEntityAssembler::toResourceFromEntity).toList(),
                HttpStatus.OK);
    }
}
