package com.serenia.platform.dailycheckin.infrastructure.persistence.jpa.repositories;

import com.serenia.platform.dailycheckin.infrastructure.persistence.jpa.entities.CheckInQuestionPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link CheckInQuestionPersistenceEntity}.
 */
@Repository
public interface CheckInQuestionJpaRepository extends JpaRepository<CheckInQuestionPersistenceEntity, UUID> {

    List<CheckInQuestionPersistenceEntity> findAllByActiveTrue();
}
