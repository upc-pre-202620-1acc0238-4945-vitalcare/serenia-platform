package com.serenia.platform.wellbeingmonitoring.domain.model.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event raised when a relative dismissed a suggestion, for every relative.
 *
 * @param olderAdultId the older adult
 * @param suggestionId the identifier of the suggestion
 * @param relativeId the relative who dismissed it
 * @param occurredAt the instant of the dismissal, in UTC
 */
public record WellbeingSuggestionDismissed(UUID olderAdultId, UUID suggestionId, UUID relativeId, Instant occurredAt) {
}
