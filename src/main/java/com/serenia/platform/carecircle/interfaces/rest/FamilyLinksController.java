package com.serenia.platform.carecircle.interfaces.rest;

import com.serenia.platform.carecircle.domain.model.commands.RevokeFamilyLinkCommand;
import com.serenia.platform.carecircle.domain.model.queries.GetActiveFamilyLinksByCareCircleIdQuery;
import com.serenia.platform.carecircle.domain.model.queries.GetActiveFamilyLinksByRelativeIdQuery;
import com.serenia.platform.carecircle.domain.model.valueobjects.CareCircleId;
import com.serenia.platform.carecircle.domain.model.valueobjects.RelativeId;
import com.serenia.platform.carecircle.domain.services.CareCircleCommandService;
import com.serenia.platform.carecircle.domain.services.CareCircleQueryService;
import com.serenia.platform.carecircle.interfaces.rest.resources.FamilyLinkResource;
import com.serenia.platform.carecircle.interfaces.rest.resources.RedeemInvitationCodeResource;
import com.serenia.platform.carecircle.interfaces.rest.transform.FamilyLinkResourceFromEntityAssembler;
import com.serenia.platform.carecircle.interfaces.rest.transform.RedeemInvitationCodeCommandFromResourceAssembler;
import com.serenia.platform.shared.application.result.ApplicationError;
import com.serenia.platform.shared.application.result.Result;
import com.serenia.platform.shared.infrastructure.security.AuthenticatedUserPrincipal;
import com.serenia.platform.shared.interfaces.rest.transform.ErrorResponseAssembler;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.function.Function;

/**
 * REST controller for redeeming invitation codes, querying the circles of a relative and
 * the relatives of a circle, and revoking family links.
 */
@RestController
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Family Links", description = "Family link endpoints")
public class FamilyLinksController {

    private static final String NOT_OWN_LINKS = "family.link.not.own.links";
    private static final String LINK_NOT_ESTABLISHED = "family.link.not.established";

    private final CareCircleCommandService careCircleCommandService;
    private final CareCircleQueryService careCircleQueryService;

    public FamilyLinksController(CareCircleCommandService careCircleCommandService,
                                 CareCircleQueryService careCircleQueryService) {
        this.careCircleCommandService = careCircleCommandService;
        this.careCircleQueryService = careCircleQueryService;
    }

    @PostMapping("/family-links")
    @Operation(summary = "Redeem invitation code", description = "Links the authenticated relative to the circle that issued the code.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Family link established successfully.",
                    content = @Content(schema = @Schema(implementation = FamilyLinkResource.class))),
            @ApiResponse(responseCode = "403", description = "The user is not an active distant relative."),
            @ApiResponse(responseCode = "404", description = "Invitation code not found."),
            @ApiResponse(responseCode = "409", description = "The code was already used or expired, or the relative is already linked.")})
    public ResponseEntity<?> redeemInvitationCode(
            @Valid @RequestBody RedeemInvitationCodeResource resource,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        var command = RedeemInvitationCodeCommandFromResourceAssembler.toCommandFromResource(principal.userId(), resource);
        var relativeId = new RelativeId(principal.userId());
        var result = careCircleCommandService.handle(command).flatMap(careCircle ->
                careCircle.findActiveFamilyLinkOf(relativeId)
                        .<Result<FamilyLinkResource, ApplicationError>>map(familyLink -> Result.success(
                                FamilyLinkResourceFromEntityAssembler.toResourceFromEntity(careCircle.getId(), familyLink)))
                        .orElseGet(() -> Result.failure(ApplicationError.unexpected("family_link", LINK_NOT_ESTABLISHED))));
        return ResponseEntityAssembler.toResponseEntityFromResult(result, Function.identity(), HttpStatus.CREATED);
    }

    @GetMapping(value = "/family-links", params = "relativeId")
    @Operation(summary = "Get family links of a relative", description = "Gets the active links of the authenticated relative.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Family links retrieved successfully."),
            @ApiResponse(responseCode = "403", description = "A relative can only query their own links.")})
    public ResponseEntity<?> getFamilyLinksByRelativeId(
            @RequestParam UUID relativeId,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        if (!relativeId.equals(principal.userId()))
            return ErrorResponseAssembler.toErrorResponseFromApplicationError(ApplicationError.forbidden(NOT_OWN_LINKS));
        var relative = new RelativeId(relativeId);
        var resources = careCircleQueryService.handle(new GetActiveFamilyLinksByRelativeIdQuery(relativeId)).stream()
                .flatMap(careCircle -> careCircle.findActiveFamilyLinkOf(relative).stream()
                        .map(familyLink -> FamilyLinkResourceFromEntityAssembler.toResourceFromEntity(careCircle.getId(), familyLink)))
                .toList();
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/care-circles/{careCircleId}/family-links")
    @Operation(summary = "Get family links of a circle", description = "Gets the relatives with an active link in the circle.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Family links retrieved successfully."),
            @ApiResponse(responseCode = "403", description = "The user is neither the owner nor an active relative."),
            @ApiResponse(responseCode = "404", description = "Care circle not found.")})
    public ResponseEntity<?> getFamilyLinksByCareCircleId(
            @PathVariable UUID careCircleId,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        var query = new GetActiveFamilyLinksByCareCircleIdQuery(careCircleId, principal.userId());
        var circleId = new CareCircleId(careCircleId);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                careCircleQueryService.handle(query),
                familyLinks -> familyLinks.stream()
                        .map(familyLink -> FamilyLinkResourceFromEntityAssembler.toResourceFromEntity(circleId, familyLink))
                        .toList(),
                HttpStatus.OK);
    }

    @DeleteMapping("/care-circles/{careCircleId}/family-links/{familyLinkId}")
    @Operation(summary = "Revoke family link",
            description = "The older adult can revoke any link of the circle; a relative, only their own.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Family link revoked successfully."),
            @ApiResponse(responseCode = "403", description = "The user cannot revoke this link."),
            @ApiResponse(responseCode = "404", description = "Care circle or family link not found."),
            @ApiResponse(responseCode = "409", description = "The family link was already revoked.")})
    public ResponseEntity<?> revokeFamilyLink(
            @PathVariable UUID careCircleId,
            @PathVariable UUID familyLinkId,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        var command = new RevokeFamilyLinkCommand(careCircleId, familyLinkId, principal.userId());
        return ResponseEntityAssembler.toResponseEntityFromResult(
                careCircleCommandService.handle(command),
                _ -> null,
                HttpStatus.NO_CONTENT);
    }
}
