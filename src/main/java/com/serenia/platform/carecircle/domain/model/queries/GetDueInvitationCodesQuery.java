package com.serenia.platform.carecircle.domain.model.queries;

import java.time.Instant;

/**
 * Query of the pending invitation codes whose validity has already ended.
 *
 * @param referenceTime the instant used to decide whether a code is due
 */
public record GetDueInvitationCodesQuery(Instant referenceTime) {
}
