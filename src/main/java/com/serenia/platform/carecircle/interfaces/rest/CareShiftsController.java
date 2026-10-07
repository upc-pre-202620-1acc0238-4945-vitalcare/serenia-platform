package com.serenia.platform.carecircle.interfaces.rest;

import com.serenia.platform.carecircle.domain.model.queries.GetCareShiftsByDateRangeQuery;
import com.serenia.platform.carecircle.domain.services.CareShiftCommandService;
import com.serenia.platform.carecircle.domain.services.CareShiftQueryService;
import com.serenia.platform.carecircle.interfaces.rest.resources.AssignCareShiftResource;
import com.serenia.platform.carecircle.interfaces.rest.resources.CareShiftResource;
import com.serenia.platform.carecircle.interfaces.rest.resources.ReassignCareShiftResource;
import com.serenia.platform.carecircle.interfaces.rest.transform.AssignCareShiftCommandFromResourceAssembler;
import com.serenia.platform.carecircle.interfaces.rest.transform.CareShiftResourceFromEntityAssembler;
import com.serenia.platform.carecircle.interfaces.rest.transform.ReassignCareShiftCommandFromResourceAssembler;
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
import org.springframework.format.annotation.DateTimeFormat;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.UUID;

/**
 * REST controller for the assignment, calendar and reassignment of care shifts.
 */
@RestController
@RequestMapping(value = "/api/v1/care-circles/{careCircleId}/care-shifts", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Care Shifts", description = "Care shift endpoints")
public class CareShiftsController {

    private final CareShiftCommandService careShiftCommandService;
    private final CareShiftQueryService careShiftQueryService;

    public CareShiftsController(CareShiftCommandService careShiftCommandService,
                                CareShiftQueryService careShiftQueryService) {
        this.careShiftCommandService = careShiftCommandService;
        this.careShiftQueryService = careShiftQueryService;
    }

    @PostMapping
    @Operation(summary = "Assign care shift", description = "Assigns the shift of a date to the authenticated relative.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Care shift assigned successfully.",
                    content = @Content(schema = @Schema(implementation = CareShiftResource.class))),
            @ApiResponse(responseCode = "403", description = "The user is not an active relative of the circle."),
            @ApiResponse(responseCode = "404", description = "Care circle not found."),
            @ApiResponse(responseCode = "409", description = "The date is already covered."),
            @ApiResponse(responseCode = "422", description = "The date is before the older adult's current day.")})
    public ResponseEntity<?> assignCareShift(
            @PathVariable UUID careCircleId,
            @Valid @RequestBody AssignCareShiftResource resource,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        var command = AssignCareShiftCommandFromResourceAssembler.toCommandFromResource(careCircleId, principal.userId(), resource);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                careShiftCommandService.handle(command),
                CareShiftResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get care shifts", description = "Gets the shifts of the circle between two dates, inclusive.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Care shifts retrieved successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid date range."),
            @ApiResponse(responseCode = "403", description = "The user is neither the owner nor an active relative."),
            @ApiResponse(responseCode = "404", description = "Care circle not found.")})
    public ResponseEntity<?> getCareShifts(
            @PathVariable UUID careCircleId,
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        var query = new GetCareShiftsByDateRangeQuery(careCircleId, principal.userId(), fromDate, toDate);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                careShiftQueryService.handle(query),
                careShifts -> careShifts.stream().map(CareShiftResourceFromEntityAssembler::toResourceFromEntity).toList(),
                HttpStatus.OK);
    }

    @PutMapping("/{careShiftId}")
    @Operation(summary = "Reassign care shift", description = "Changes the relative in charge of a shift.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Care shift reassigned successfully.",
                    content = @Content(schema = @Schema(implementation = CareShiftResource.class))),
            @ApiResponse(responseCode = "403", description = "The user is neither the owner nor an active relative."),
            @ApiResponse(responseCode = "404", description = "Care shift not found."),
            @ApiResponse(responseCode = "422", description = "Past shift, same relative or relative not linked.")})
    public ResponseEntity<?> reassignCareShift(
            @PathVariable UUID careCircleId,
            @PathVariable UUID careShiftId,
            @Valid @RequestBody ReassignCareShiftResource resource,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        var command = ReassignCareShiftCommandFromResourceAssembler.toCommandFromResource(careShiftId, principal.userId(), resource);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                careShiftCommandService.handle(command),
                CareShiftResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.OK);
    }
}
