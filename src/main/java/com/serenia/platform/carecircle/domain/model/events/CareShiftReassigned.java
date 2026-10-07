package com.serenia.platform.carecircle.domain.model.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event raised when the relative in charge of a care shift has changed.
 *
 * @param careShiftId        the identifier of the shift
 * @param previousRelativeId the relative who was in charge
 * @param newRelativeId      the relative now in charge
 * @param occurredAt         the instant of the reassignment, in UTC
 */
public record CareShiftReassigned(UUID careShiftId, UUID previousRelativeId, UUID newRelativeId, Instant occurredAt) {
}
