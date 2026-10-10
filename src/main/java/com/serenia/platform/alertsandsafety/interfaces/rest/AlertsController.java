package com.serenia.platform.alertsandsafety.interfaces.rest;

import com.serenia.platform.alertsandsafety.domain.model.queries.GetAlertByIdQuery;
import com.serenia.platform.alertsandsafety.domain.model.queries.GetAlertsByOlderAdultIdQuery;
import com.serenia.platform.alertsandsafety.domain.services.AlertQueryService;
import com.serenia.platform.alertsandsafety.interfaces.rest.resources.AlertResource;
import com.serenia.platform.alertsandsafety.interfaces.rest.transform.AlertResourceFromEntityAssembler;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * REST controller for the alerts view, with their type and status, for the older adult and
 * their linked relatives.
 */
@RestController
@RequestMapping(value = "/api/v1/alerts", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Alerts", description = "Alerts view of both types")
public class AlertsController {

    private final AlertQueryService alertQueryService;

    public AlertsController(AlertQueryService alertQueryService) {
        this.alertQueryService = alertQueryService;
    }

    @GetMapping(params = "olderAdultId")
    @Operation(summary = "Get alerts", description = "Gets the alerts of the older adult, of both types, from the most recent to the oldest.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Alerts retrieved."),
            @ApiResponse(responseCode = "403", description = "The user is neither the older adult nor an actively linked relative.")})
    public ResponseEntity<?> getAlerts(
            @RequestParam UUID olderAdultId,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        return ResponseEntityAssembler.toResponseEntityFromResult(
                alertQueryService.handle(new GetAlertsByOlderAdultIdQuery(olderAdultId, principal.userId())),
                alerts -> alerts.stream().map(AlertResourceFromEntityAssembler::toResourceFromEntity).toList(),
                HttpStatus.OK);
    }

    @GetMapping("/{alertId}")
    @Operation(summary = "Get alert", description = "Gets the detail of an alert.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Alert retrieved.",
                    content = @Content(schema = @Schema(implementation = AlertResource.class))),
            @ApiResponse(responseCode = "403", description = "The user is neither the older adult nor an actively linked relative."),
            @ApiResponse(responseCode = "404", description = "Alert not found.")})
    public ResponseEntity<?> getAlert(
            @PathVariable UUID alertId,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        return ResponseEntityAssembler.toResponseEntityFromResult(
                alertQueryService.handle(new GetAlertByIdQuery(alertId, principal.userId())),
                AlertResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.OK);
    }
}
