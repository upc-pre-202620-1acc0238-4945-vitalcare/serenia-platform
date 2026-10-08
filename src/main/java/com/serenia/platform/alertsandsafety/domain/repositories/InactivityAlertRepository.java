package com.serenia.platform.alertsandsafety.domain.repositories;

import com.serenia.platform.alertsandsafety.domain.model.aggregates.InactivityAlert;
import com.serenia.platform.alertsandsafety.domain.model.valueobjects.AlertId;
import com.serenia.platform.alertsandsafety.domain.model.valueobjects.CheckInId;
import com.serenia.platform.alertsandsafety.domain.model.valueobjects.OlderAdultId;

import java.util.List;
import java.util.Optional;

/**
 * Persistence contract of the {@link InactivityAlert} aggregate together with its attention actions.
 */
public interface InactivityAlertRepository {

    /**
     * Persists the alert.
     *
     * @throws com.serenia.platform.alertsandsafety.domain.exceptions.InactivityAlertAlreadyRaisedException
     *         when the check-in already produced an alert
     * @throws com.serenia.platform.alertsandsafety.domain.exceptions.AlertAlreadyAcknowledgedException
     *         when another relative acknowledged or resolved the alert at the same time
     */
    InactivityAlert save(InactivityAlert alert);

    Optional<InactivityAlert> findById(AlertId alertId);

    boolean existsByCheckInId(CheckInId checkInId);

    /** Returns the inactivity alerts of the older adult, from the most recent to the oldest. */
    List<InactivityAlert> findAllByOlderAdultId(OlderAdultId olderAdultId);
}
