package com.serenia.platform.dailycheckin.infrastructure.persistence.jpa.repositories;

import com.serenia.platform.dailycheckin.infrastructure.persistence.jpa.entities.CheckInPreferencesPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link CheckInPreferencesPersistenceEntity}.
 *
 * <p>The lookup by older adult runs over the unique index of {@code older_adult_id}.</p>
 */
@Repository
public interface CheckInPreferencesJpaRepository extends JpaRepository<CheckInPreferencesPersistenceEntity, UUID> {

    Optional<CheckInPreferencesPersistenceEntity> findByOlderAdultId(UUID olderAdultId);
}
