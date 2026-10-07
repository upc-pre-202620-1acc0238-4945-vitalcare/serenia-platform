package com.serenia.platform.wellbeingmonitoring.domain.model.commands;

import java.util.UUID;

/**
 * Intention of a relative to dismiss a suggestion.
 */
public record DismissWellbeingSuggestionCommand(UUID olderAdultId, UUID suggestionId, UUID relativeId) {
}
