package com.serenia.platform.dailycheckin.domain.model.queries;

import java.time.Instant;

/**
 * Query of the pending check-ins, not prompted yet, whose presentation time has arrived.
 */
public record GetCheckInsDueForPromptQuery(Instant referenceTime) {
}
