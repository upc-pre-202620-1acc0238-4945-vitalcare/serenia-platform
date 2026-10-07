package com.serenia.platform.carecircle.infrastructure.persistence.jpa.repositories;

import com.serenia.platform.carecircle.infrastructure.persistence.jpa.entities.SharedNotePersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link SharedNotePersistenceEntity}.
 *
 * <p>The listing by circle runs over the {@code (care_circle_id, created_at)} index.</p>
 */
@Repository
public interface SharedNoteJpaRepository extends JpaRepository<SharedNotePersistenceEntity, UUID> {

    List<SharedNotePersistenceEntity> findAllByCareCircleIdOrderByCreatedAtDesc(UUID careCircleId);
}
