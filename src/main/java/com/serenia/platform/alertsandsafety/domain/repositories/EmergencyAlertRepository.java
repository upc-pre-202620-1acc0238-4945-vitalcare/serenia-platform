package com.serenia.platform.alertsandsafety.domain.repositories;

import com.serenia.platform.alertsandsafety.domain.model.aggregates.EmergencyAlert;
import com.serenia.platform.alertsandsafety.domain.model.valueobjects.AlertId;
import com.serenia.platform.alertsandsafety.domain.model.valueobjects.OlderAdultId;

import java.util.List;
import java.util.Optional;

/**
 * Persistence contract of the {@link EmergencyAlert} aggregate together with its notifications
 * and attention actions.
 */
public interface EmergencyAlertRepository {

    /**
     * Persists the alert.
     *
     * @throws com.serenia.platform.alertsandsafety.domain.exceptions.AlertAlreadyAcknowledgedException
     *         when another relative acknowledged or resolved the alert at the same time
     */
    EmergencyAlert save(EmergencyAlert alert);

    Optional<EmergencyAlert> findById(AlertId alertId);

    /** Returns the emergencies of the older adult, from the most recent to the oldest. */
    List<EmergencyAlert> findAllByOlderAdultId(OlderAdultId olderAdultId);
}
