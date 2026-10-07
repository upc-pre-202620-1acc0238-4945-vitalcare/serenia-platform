package com.serenia.platform.dailycheckin.domain.model.queries;

import java.time.Instant;

/**
 * Query of the pending check-ins whose deadline has passed.
 */
public record GetCheckInsPastDeadlineQuery(Instant referenceTime) {
}
