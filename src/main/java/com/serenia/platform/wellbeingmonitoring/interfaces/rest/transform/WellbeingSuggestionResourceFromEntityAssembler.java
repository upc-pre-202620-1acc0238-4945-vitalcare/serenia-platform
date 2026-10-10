package com.serenia.platform.wellbeingmonitoring.interfaces.rest.transform;

import com.serenia.platform.wellbeingmonitoring.domain.model.entities.WellbeingSuggestion;
import com.serenia.platform.wellbeingmonitoring.interfaces.rest.resources.WellbeingSuggestionResource;

import java.util.UUID;

/**
 * Converts a {@link WellbeingSuggestion} entity into its {@link WellbeingSuggestionResource} REST
 * representation. The older adult is passed apart because it is the identity of the aggregate.
 */
public class WellbeingSuggestionResourceFromEntityAssembler {
    public static WellbeingSuggestionResource toResourceFromEntity(UUID olderAdultId, WellbeingSuggestion suggestion) {
        return new WellbeingSuggestionResource(
                suggestion.getId().value(),
                olderAdultId,
                suggestion.getPatternId().value(),
                suggestion.getMessage().value(),
                suggestion.getStatus().name(),
                suggestion.getIssuedAt());
    }
}
