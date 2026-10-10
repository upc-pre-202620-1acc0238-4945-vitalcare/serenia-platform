package com.serenia.platform.carecircle.interfaces.rest;

import com.serenia.platform.carecircle.domain.model.queries.GetCareCircleByIdQuery;
import com.serenia.platform.carecircle.domain.model.queries.GetCareCircleByOlderAdultIdQuery;
import com.serenia.platform.carecircle.domain.services.CareCircleQueryService;
import com.serenia.platform.carecircle.interfaces.rest.resources.CareCircleResource;
import com.serenia.platform.carecircle.interfaces.rest.transform.CareCircleResourceFromEntityAssembler;
import com.serenia.platform.shared.application.result.ApplicationError;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * REST controller for querying care circles.
 *
 * <p>It does not expose the creation of circles: a circle is created automatically with
 * the registration of its older adult.</p>
 */
@RestController
@RequestMapping(value = "/api/v1/care-circles", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Care Circles", description = "Care circle endpoints")
public class CareCirclesController {

    private static final String NO_ACCESS = "care.circle.access.denied";

    private final CareCircleQueryService careCircleQueryService;

    public CareCirclesController(CareCircleQueryService careCircleQueryService) {
        this.careCircleQueryService = careCircleQueryService;
    }

    @GetMapping("/{careCircleId}")
    @Operation(summary = "Get care circle by id", description = "Gets a care circle the authenticated user has access to.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Care circle retrieved successfully.",
                    content = @Content(schema = @Schema(implementation = CareCircleResource.class))),
            @ApiResponse(responseCode = "403", description = "The user is neither the owner nor an active relative."),
            @ApiResponse(responseCode = "404", description = "Care circle not found.")})
    public ResponseEntity<?> getCareCircleById(
            @PathVariable UUID careCircleId,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        var query = new GetCareCircleByIdQuery(careCircleId, principal.userId());
        return ResponseEntityAssembler.toResponseEntityFromResult(
                careCircleQueryService.handle(query),
                CareCircleResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.OK);
    }

    @GetMapping(params = "olderAdultId")
    @Operation(summary = "Get care circle by older adult", description = "Gets the care circle of an older adult.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Care circle retrieved successfully.",
                    content = @Content(schema = @Schema(implementation = CareCircleResource.class))),
            @ApiResponse(responseCode = "403", description = "The user is neither the owner nor an active relative."),
            @ApiResponse(responseCode = "404", description = "Care circle not found.")})
    public ResponseEntity<?> getCareCircleByOlderAdultId(
            @RequestParam UUID olderAdultId,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        var careCircle = careCircleQueryService.handle(new GetCareCircleByOlderAdultIdQuery(olderAdultId));
        if (careCircle.isEmpty())
            return ErrorResponseAssembler.toErrorResponseFromApplicationError(
                    ApplicationError.notFound("care_circle", olderAdultId.toString()));
        if (!careCircle.get().hasAccess(principal.userId()))
            return ErrorResponseAssembler.toErrorResponseFromApplicationError(ApplicationError.forbidden(NO_ACCESS));
        return ResponseEntity.ok(CareCircleResourceFromEntityAssembler.toResourceFromEntity(careCircle.get()));
    }
}
