package com.serenia.platform.alertsandsafety.infrastructure.persistence.jpa.repositories;

import com.serenia.platform.alertsandsafety.domain.model.valueobjects.AlertType;
import com.serenia.platform.alertsandsafety.infrastructure.persistence.jpa.entities.AlertPersistenceEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link AlertPersistenceEntity}.
 *
 * <p>The history by older adult and type runs over the {@code (older_adult_id, triggered_at)}
 * index; the check by check-in, over the unique index of {@code check_in_id}.</p>
 */
@Repository
public interface AlertJpaRepository extends JpaRepository<AlertPersistenceEntity, UUID> {

    Optional<AlertPersistenceEntity> findByIdAndType(UUID id, AlertType type);

    List<AlertPersistenceEntity> findAllByOlderAdultIdAndTypeOrderByTriggeredAtDesc(UUID olderAdultId, AlertType type);

    boolean existsByCheckInId(UUID checkInId);

    /**
     * Loads the alert locking its row until the transaction ends, so the actions of relatives on
     * the same alert are applied one after another instead of deadlocking on the unique index.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from AlertPersistenceEntity a where a.id = :id")
    Optional<AlertPersistenceEntity> findByIdForUpdate(@Param("id") UUID id);
}
