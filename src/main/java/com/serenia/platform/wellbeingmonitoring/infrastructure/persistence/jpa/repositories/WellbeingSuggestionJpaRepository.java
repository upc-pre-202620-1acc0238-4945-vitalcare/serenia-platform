package com.serenia.platform.wellbeingmonitoring.infrastructure.persistence.jpa.repositories;

import com.serenia.platform.wellbeingmonitoring.domain.model.valueobjects.SuggestionStatus;
import com.serenia.platform.wellbeingmonitoring.infrastructure.persistence.jpa.entities.WellbeingSuggestionPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link WellbeingSuggestionPersistenceEntity}.
 *
 * <p>The active suggestions are resolved over the {@code (older_adult_id, status)} index.</p>
 */
@Repository
public interface WellbeingSuggestionJpaRepository extends JpaRepository<WellbeingSuggestionPersistenceEntity, UUID> {

    List<WellbeingSuggestionPersistenceEntity> findAllByOlderAdultIdAndStatusOrderByIssuedAtDesc(UUID olderAdultId,
                                                                                               SuggestionStatus status);
}
