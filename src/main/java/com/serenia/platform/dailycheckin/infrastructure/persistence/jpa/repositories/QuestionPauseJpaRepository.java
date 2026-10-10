package com.serenia.platform.dailycheckin.infrastructure.persistence.jpa.repositories;

import com.serenia.platform.dailycheckin.infrastructure.persistence.jpa.entities.QuestionPausePersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link QuestionPausePersistenceEntity}.
 *
 * <p>The search of the latest pause runs over the unique {@code (older_adult_id, paused_date)} index.</p>
 */
@Repository
public interface QuestionPauseJpaRepository extends JpaRepository<QuestionPausePersistenceEntity, UUID> {

    Optional<QuestionPausePersistenceEntity> findFirstByOlderAdultIdOrderByPausedDateDesc(UUID olderAdultId);
}
