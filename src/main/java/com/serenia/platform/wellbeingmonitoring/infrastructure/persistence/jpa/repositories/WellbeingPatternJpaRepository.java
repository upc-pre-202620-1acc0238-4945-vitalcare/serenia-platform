package com.serenia.platform.wellbeingmonitoring.infrastructure.persistence.jpa.repositories;

import com.serenia.platform.wellbeingmonitoring.infrastructure.persistence.jpa.entities.WellbeingPatternPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link WellbeingPatternPersistenceEntity}.
 *
 * <p>The latest pattern of an older adult is resolved over the {@code (older_adult_id, detected_at)} index.</p>
 */
@Repository
public interface WellbeingPatternJpaRepository extends JpaRepository<WellbeingPatternPersistenceEntity, UUID> {

    Optional<WellbeingPatternPersistenceEntity> findFirstByOlderAdultIdOrderByDetectedAtDesc(UUID olderAdultId);
}
