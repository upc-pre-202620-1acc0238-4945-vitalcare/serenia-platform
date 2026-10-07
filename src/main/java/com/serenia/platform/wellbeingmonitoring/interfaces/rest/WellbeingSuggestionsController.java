package com.serenia.platform.wellbeingmonitoring.interfaces.rest;

import com.serenia.platform.shared.infrastructure.security.AuthenticatedUserPrincipal;
import com.serenia.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import com.serenia.platform.wellbeingmonitoring.domain.model.commands.DismissWellbeingSuggestionCommand;
import com.serenia.platform.wellbeingmonitoring.domain.model.queries.GetActiveWellbeingSuggestionsQuery;
import com.serenia.platform.wellbeingmonitoring.domain.services.WellbeingInsightCommandService;
import com.serenia.platform.wellbeingmonitoring.domain.services.WellbeingInsightQueryService;
import com.serenia.platform.wellbeingmonitoring.interfaces.rest.resources.WellbeingSuggestionResource;
import com.serenia.platform.wellbeingmonitoring.interfaces.rest.transform.WellbeingSuggestionResourceFromEntityAssembler;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * REST controller for the active wellbeing suggestions and their dismissal, reserved to
 * actively linked relatives.
 */
@RestController
@RequestMapping(value = "/api/v1/wellbeing-suggestions", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Wellbeing Suggestions", description = "Wellbeing suggestion endpoints")
public class WellbeingSuggestionsController {

    private final WellbeingInsightCommandService wellbeingInsightCommandService;
    private final WellbeingInsightQueryService wellbeingInsightQueryService;

    public WellbeingSuggestionsController(WellbeingInsightCommandService wellbeingInsightCommandService,
                                          WellbeingInsightQueryService wellbeingInsightQueryService) {
        this.wellbeingInsightCommandService = wellbeingInsightCommandService;
        this.wellbeingInsightQueryService = wellbeingInsightQueryService;
    }

    @GetMapping(params = "olderAdultId")
    @Operation(summary = "Get active suggestions", description = "Gets the active suggestions about an older adult.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Suggestions retrieved successfully."),
            @ApiResponse(responseCode = "403", description = "The user is not an actively linked relative.")})
    public ResponseEntity<?> getActiveSuggestions(
            @RequestParam UUID olderAdultId,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        var query = new GetActiveWellbeingSuggestionsQuery(olderAdultId, principal.userId());
        return ResponseEntityAssembler.toResponseEntityFromResult(
                wellbeingInsightQueryService.handle(query),
                suggestions -> suggestions.stream()
                        .map(suggestion -> WellbeingSuggestionResourceFromEntityAssembler.toResourceFromEntity(olderAdultId, suggestion))
                        .toList(),
                HttpStatus.OK);
    }

    @PostMapping(value = "/{suggestionId}/dismiss", params = "olderAdultId")
    @Operation(summary = "Dismiss suggestion", description = "Dismisses a suggestion for every relative of the older adult.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Suggestion dismissed successfully.",
                    content = @Content(schema = @Schema(implementation = WellbeingSuggestionResource.class))),
            @ApiResponse(responseCode = "403", description = "The user is not an actively linked relative."),
            @ApiResponse(responseCode = "409", description = "The suggestion is no longer active.")})
    public ResponseEntity<?> dismissSuggestion(
            @PathVariable UUID suggestionId,
            @RequestParam UUID olderAdultId,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        var command = new DismissWellbeingSuggestionCommand(olderAdultId, suggestionId, principal.userId());
        return ResponseEntityAssembler.toResponseEntityFromResult(
                wellbeingInsightCommandService.handle(command),
                suggestion -> WellbeingSuggestionResourceFromEntityAssembler.toResourceFromEntity(olderAdultId, suggestion),
                HttpStatus.OK);
    }
}
