package com.serenia.platform.alertsandsafety.infrastructure.persistence.jpa.adapters;

import com.serenia.platform.alertsandsafety.domain.exceptions.AlertAlreadyAcknowledgedException;
import com.serenia.platform.alertsandsafety.domain.model.aggregates.EmergencyAlert;
import com.serenia.platform.alertsandsafety.domain.model.valueobjects.AlertId;
import com.serenia.platform.alertsandsafety.domain.model.valueobjects.AlertType;
import com.serenia.platform.alertsandsafety.domain.model.valueobjects.OlderAdultId;
import com.serenia.platform.alertsandsafety.domain.repositories.EmergencyAlertRepository;
import com.serenia.platform.alertsandsafety.infrastructure.persistence.jpa.mappers.EmergencyAlertPersistenceMapper;
import com.serenia.platform.alertsandsafety.infrastructure.persistence.jpa.repositories.AlertJpaRepository;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Optional;

/**
 * Adapter that persists and loads the {@link EmergencyAlert} aggregate over the rows of type
 * EMERGENCY of the alerts.
 *
 * <p>The stored row is locked and updated in place, so if two relatives acknowledge the same
 * alert at the same time, the second one waits and then violates the unique
 * {@code (alert_id, action)} index, which is translated into an {@link AlertAlreadyAcknowledgedException}.</p>
 */
@Repository
public class EmergencyAlertRepositoryImpl implements EmergencyAlertRepository {

    private final AlertJpaRepository alertJpaRepository;
    private final TransactionTemplate transactionTemplate;

    public EmergencyAlertRepositoryImpl(AlertJpaRepository alertJpaRepository, PlatformTransactionManager transactionManager) {
        this.alertJpaRepository = alertJpaRepository;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Override
    public EmergencyAlert save(EmergencyAlert alert) {
        try {
            // Opened here, and not with @Transactional, so the violation can be translated once it rolled back
            return transactionTemplate.execute(_ -> {
                var entity = alertJpaRepository.findByIdForUpdate(alert.getId().value())
                        .orElseGet(() -> EmergencyAlertPersistenceMapper.newPersistenceFromDomain(alert));
                EmergencyAlertPersistenceMapper.applyToPersistence(alert, entity);
                return EmergencyAlertPersistenceMapper.toDomainFromPersistence(alertJpaRepository.saveAndFlush(entity));
            });
        } catch (DataIntegrityViolationException | ConcurrencyFailureException e) {
            throw new AlertAlreadyAcknowledgedException();
        }
    }

    @Override
    public Optional<EmergencyAlert> findById(AlertId alertId) {
        return alertJpaRepository.findByIdAndType(alertId.value(), AlertType.EMERGENCY)
                .map(EmergencyAlertPersistenceMapper::toDomainFromPersistence);
    }

    @Override
    public List<EmergencyAlert> findAllByOlderAdultId(OlderAdultId olderAdultId) {
        return alertJpaRepository.findAllByOlderAdultIdAndTypeOrderByTriggeredAtDesc(olderAdultId.value(), AlertType.EMERGENCY)
                .stream()
                .map(EmergencyAlertPersistenceMapper::toDomainFromPersistence)
                .toList();
    }
}
