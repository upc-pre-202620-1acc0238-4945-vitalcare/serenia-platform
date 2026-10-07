package com.serenia.platform.socialcompanionship.infrastructure.persistence.jpa.repositories;

import com.serenia.platform.socialcompanionship.domain.model.valueobjects.MessageStatus;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.MessageType;
import com.serenia.platform.socialcompanionship.infrastructure.persistence.jpa.entities.CompanionMessagePersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link CompanionMessagePersistenceEntity}.
 *
 * <p>The listing of a circle's shared messages of one type, by send date, runs over the
 * {@code (care_circle_id, type, sent_at)} index.</p>
 */
@Repository
public interface CompanionMessageJpaRepository extends JpaRepository<CompanionMessagePersistenceEntity, UUID> {

    Optional<CompanionMessagePersistenceEntity> findByIdAndType(UUID id, MessageType type);

    List<CompanionMessagePersistenceEntity> findAllByCareCircleIdAndTypeAndStatusOrderBySentAtDesc(
            UUID careCircleId, MessageType type, MessageStatus status);
}
