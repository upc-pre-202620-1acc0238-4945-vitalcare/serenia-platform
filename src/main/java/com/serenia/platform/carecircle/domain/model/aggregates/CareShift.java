package com.serenia.platform.carecircle.domain.model.aggregates;

import com.serenia.platform.carecircle.domain.exceptions.CareShiftAlreadyAssignedToRelativeException;
import com.serenia.platform.carecircle.domain.exceptions.CareShiftDateInPastException;
import com.serenia.platform.carecircle.domain.model.events.CareShiftAssigned;
import com.serenia.platform.carecircle.domain.model.events.CareShiftReassigned;
import com.serenia.platform.carecircle.domain.model.valueobjects.CareCircleId;
import com.serenia.platform.carecircle.domain.model.valueobjects.CareShiftId;
import com.serenia.platform.carecircle.domain.model.valueobjects.RelativeId;
import com.serenia.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Aggregate root representing the responsibility of a relative for following up on
 * the older adult on a given date.
 *
 * <p>Independent from {@link CareCircle}, which it references by identity, so two relatives
 * editing at the same time do not compete for the same aggregate.</p>
 */
@Getter
public class CareShift extends AbstractDomainAggregateRoot<CareShift> {

    private static final String REQUIRED_VALUE_MESSAGE_KEY = "care.shift.required.value";

    private final CareShiftId id;
    private final CareCircleId careCircleId;
    private RelativeId relativeId;
    private final LocalDate shiftDate;
    private final Instant createdAt;
    private Instant updatedAt;

    /**
     * Reconstitution constructor — used by the persistence mapper to rebuild a shift
     * from stored data.
     */
    public CareShift(CareShiftId id, CareCircleId careCircleId, RelativeId relativeId, LocalDate shiftDate,
                     Instant createdAt, Instant updatedAt) {
        if (id == null || careCircleId == null || relativeId == null || shiftDate == null
                || createdAt == null || updatedAt == null)
            throw new IllegalArgumentException(REQUIRED_VALUE_MESSAGE_KEY);
        this.id = id;
        this.careCircleId = careCircleId;
        this.relativeId = relativeId;
        this.shiftDate = shiftDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Assigns the shift to a relative, rejecting dates before the older adult's current day.
     * Registers {@link CareShiftAssigned}.
     *
     * @param today the current day in the older adult's time zone
     */
    public static CareShift assign(CareCircleId careCircleId, RelativeId relativeId, LocalDate shiftDate,
                                   LocalDate today, Instant createdAt) {
        if (shiftDate != null && shiftDate.isBefore(today)) throw new CareShiftDateInPastException();
        var careShift = new CareShift(CareShiftId.generate(), careCircleId, relativeId, shiftDate, createdAt, createdAt);
        careShift.registerDomainEvent(new CareShiftAssigned(
                careShift.id.value(), careCircleId.value(), relativeId.value(), shiftDate, createdAt));
        return careShift;
    }

    /**
     * Changes the relative in charge of the shift; rejects past shifts and reassignments to
     * the same relative. Lets other relatives cover the shifts of a relative whose link was
     * revoked. Registers {@link CareShiftReassigned}.
     *
     * @param today the current day in the older adult's time zone
     */
    public void reassignTo(RelativeId newRelativeId, LocalDate today, Instant updatedAt) {
        if (shiftDate.isBefore(today)) throw new CareShiftDateInPastException();
        if (isAssignedTo(newRelativeId)) throw new CareShiftAlreadyAssignedToRelativeException();
        var previousRelativeId = this.relativeId;
        this.relativeId = newRelativeId;
        this.updatedAt = updatedAt;
        registerDomainEvent(new CareShiftReassigned(
                id.value(), previousRelativeId.value(), newRelativeId.value(), updatedAt));
    }

    /** Indicates whether the shift is assigned to the given relative. */
    public boolean isAssignedTo(RelativeId relativeId) {
        return this.relativeId.equals(relativeId);
    }
}
