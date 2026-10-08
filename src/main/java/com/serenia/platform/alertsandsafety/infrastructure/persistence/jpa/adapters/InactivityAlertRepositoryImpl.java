package com.serenia.platform.alertsandsafety.infrastructure.persistence.jpa.adapters;

import com.serenia.platform.alertsandsafety.domain.exceptions.AlertAlreadyAcknowledgedException;
import com.serenia.platform.alertsandsafety.domain.exceptions.InactivityAlertAlreadyRaisedException;
import com.serenia.platform.alertsandsafety.domain.model.aggregates.InactivityAlert;
import com.serenia.platform.alertsandsafety.domain.model.valueobjects.AlertId;
import com.serenia.platform.alertsandsafety.domain.model.valueobjects.AlertType;
import com.serenia.platform.alertsandsafety.domain.model.valueobjects.CheckInId;
import com.serenia.platform.alertsandsafety.domain.model.valueobjects.OlderAdultId;
import com.serenia.platform.alertsandsafety.domain.repositories.InactivityAlertRepository;
import com.serenia.platform.alertsandsafety.infrastructure.persistence.jpa.mappers.InactivityAlertPersistenceMapper;
import com.serenia.platform.alertsandsafety.infrastructure.persistence.jpa.repositories.AlertJpaRepository;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Optional;

/**
 * Adapter that persists and loads the {@link InactivityAlert} aggregate over the rows of type
 * INACTIVITY of the alerts.
 *
 * <p>If the same check-in is evaluated twice, the unique index of {@code check_in_id} prevents a
 * duplicated alert; if two relatives act on the alert at the same time, the unique
 * {@code (alert_id, action)} index rejects the second one. Both are translated into domain exceptions.</p>
 */
@Repository
public class InactivityAlertRepositoryImpl implements InactivityAlertRepository {

    private final AlertJpaRepository alertJpaRepository;
    private final TransactionTemplate transactionTemplate;

    public InactivityAlertRepositoryImpl(AlertJpaRepository alertJpaRepository, PlatformTransactionManager transactionManager) {
        this.alertJpaRepository = alertJpaRepository;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Override
    public InactivityAlert save(InactivityAlert alert) {
        try {
            // Opened here, and not with @Transactional, so the violation can be translated once it rolled back
            return transactionTemplate.execute(_ -> {
                var entity = alertJpaRepository.findByIdForUpdate(alert.getId().value())
                        .orElseGet(() -> InactivityAlertPersistenceMapper.newPersistenceFromDomain(alert));
                InactivityAlertPersistenceMapper.applyToPersistence(alert, entity);
                return InactivityAlertPersistenceMapper.toDomainFromPersistence(alertJpaRepository.saveAndFlush(entity));
            });
        } catch (DataIntegrityViolationException | ConcurrencyFailureException e) {
            // A new alert can only collide on its check-in; an existing one, on its attention actions
            if (alert.getAttentions().isEmpty()) throw new InactivityAlertAlreadyRaisedException();
            throw new AlertAlreadyAcknowledgedException();
        }
    }

    @Override
    public Optional<InactivityAlert> findById(AlertId alertId) {
        return alertJpaRepository.findByIdAndType(alertId.value(), AlertType.INACTIVITY)
                .map(InactivityAlertPersistenceMapper::toDomainFromPersistence);
    }

    @Override
    public boolean existsByCheckInId(CheckInId checkInId) {
        return alertJpaRepository.existsByCheckInId(checkInId.value());
    }

    @Override
    public List<InactivityAlert> findAllByOlderAdultId(OlderAdultId olderAdultId) {
        return alertJpaRepository.findAllByOlderAdultIdAndTypeOrderByTriggeredAtDesc(olderAdultId.value(), AlertType.INACTIVITY)
                .stream()
                .map(InactivityAlertPersistenceMapper::toDomainFromPersistence)
                .toList();
    }
}
