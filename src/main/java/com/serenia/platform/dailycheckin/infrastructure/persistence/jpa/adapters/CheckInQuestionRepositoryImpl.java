package com.serenia.platform.dailycheckin.infrastructure.persistence.jpa.adapters;

import com.serenia.platform.dailycheckin.domain.model.entities.CheckInQuestion;
import com.serenia.platform.dailycheckin.domain.model.valueobjects.CheckInQuestionId;
import com.serenia.platform.dailycheckin.domain.repositories.CheckInQuestionRepository;
import com.serenia.platform.dailycheckin.infrastructure.persistence.jpa.mappers.CheckInQuestionPersistenceMapper;
import com.serenia.platform.dailycheckin.infrastructure.persistence.jpa.repositories.CheckInQuestionJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adapter that reads the question catalog with Spring Data JPA and stores it during the initial load.
 */
@Repository
public class CheckInQuestionRepositoryImpl implements CheckInQuestionRepository {

    private final CheckInQuestionJpaRepository checkInQuestionJpaRepository;

    public CheckInQuestionRepositoryImpl(CheckInQuestionJpaRepository checkInQuestionJpaRepository) {
        this.checkInQuestionJpaRepository = checkInQuestionJpaRepository;
    }

    @Override
    public Optional<CheckInQuestion> findById(CheckInQuestionId questionId) {
        return checkInQuestionJpaRepository.findById(questionId.value())
                .map(CheckInQuestionPersistenceMapper::toDomainFromPersistence);
    }

    @Override
    public List<CheckInQuestion> findAllActive() {
        return checkInQuestionJpaRepository.findAllByActiveTrue().stream()
                .map(CheckInQuestionPersistenceMapper::toDomainFromPersistence)
                .toList();
    }

    @Override
    public void saveAll(List<CheckInQuestion> questions) {
        checkInQuestionJpaRepository.saveAll(questions.stream()
                .map(CheckInQuestionPersistenceMapper::toPersistenceFromDomain)
                .toList());
    }

    @Override
    public long count() {
        return checkInQuestionJpaRepository.count();
    }
}
