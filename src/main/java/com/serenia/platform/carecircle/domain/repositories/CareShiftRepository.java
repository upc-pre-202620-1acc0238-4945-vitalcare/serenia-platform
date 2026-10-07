package com.serenia.platform.carecircle.domain.repositories;

import com.serenia.platform.carecircle.domain.model.aggregates.CareShift;
import com.serenia.platform.carecircle.domain.model.valueobjects.CareCircleId;
import com.serenia.platform.carecircle.domain.model.valueobjects.CareShiftId;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Persistence contract of the {@link CareShift} aggregate.
 */
public interface CareShiftRepository {

    /**
     * Persists the shift.
     *
     * @throws com.serenia.platform.carecircle.domain.exceptions.CareShiftDateAlreadyCoveredException
     *         when another shift of the circle already covers the same date
     */
    CareShift save(CareShift careShift);

    Optional<CareShift> findById(CareShiftId careShiftId);

    boolean existsByCareCircleIdAndShiftDate(CareCircleId careCircleId, LocalDate shiftDate);

    /** Returns the shifts of the circle between both dates, inclusive, ordered by date. */
    List<CareShift> findAllByCareCircleIdAndShiftDateBetween(CareCircleId careCircleId, LocalDate fromDate, LocalDate toDate);
}
