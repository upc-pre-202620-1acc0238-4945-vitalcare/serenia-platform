package com.serenia.platform.wellbeingmonitoring.domain.model.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event raised when an action suggestion was issued for the relatives.
 *
 * @param olderAdultId the older adult
 * @param suggestionId the identifier of the suggestion
 * @param patternId the pattern that originated the suggestion
 * @param occurredAt the instant of the issue, in UTC
 */
public record WellbeingSuggestionIssued(UUID olderAdultId, UUID suggestionId, UUID patternId, Instant occurredAt) {
}
