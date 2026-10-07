package com.serenia.platform.carecircle.domain.model.events;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Domain event raised when a care shift has been assigned to a relative.
 *
 * @param careShiftId  the identifier of the shift
 * @param careCircleId the circle of the shift
 * @param relativeId   the relative in charge of the shift
 * @param shiftDate    the date of the shift, in the older adult's time zone
 * @param occurredAt   the instant of the assignment, in UTC
 */
public record CareShiftAssigned(UUID careShiftId, UUID careCircleId, UUID relativeId, LocalDate shiftDate,
                                Instant occurredAt) {
}
