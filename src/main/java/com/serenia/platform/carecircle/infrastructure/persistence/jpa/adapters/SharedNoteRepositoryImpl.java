package com.serenia.platform.carecircle.infrastructure.persistence.jpa.adapters;

import com.serenia.platform.carecircle.domain.model.aggregates.SharedNote;
import com.serenia.platform.carecircle.domain.model.valueobjects.CareCircleId;
import com.serenia.platform.carecircle.domain.model.valueobjects.SharedNoteId;
import com.serenia.platform.carecircle.domain.repositories.SharedNoteRepository;
import com.serenia.platform.carecircle.infrastructure.persistence.jpa.mappers.SharedNotePersistenceMapper;
import com.serenia.platform.carecircle.infrastructure.persistence.jpa.repositories.SharedNoteJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adapter that implements the domain {@link SharedNoteRepository} with Spring Data JPA.
 */
@Repository
public class SharedNoteRepositoryImpl implements SharedNoteRepository {

    private final SharedNoteJpaRepository sharedNoteJpaRepository;

    public SharedNoteRepositoryImpl(SharedNoteJpaRepository sharedNoteJpaRepository) {
        this.sharedNoteJpaRepository = sharedNoteJpaRepository;
    }

    @Override
    public SharedNote save(SharedNote sharedNote) {
        var saved = sharedNoteJpaRepository.save(SharedNotePersistenceMapper.toPersistenceFromDomain(sharedNote));
        return SharedNotePersistenceMapper.toDomainFromPersistence(saved);
    }

    @Override
    public Optional<SharedNote> findById(SharedNoteId sharedNoteId) {
        return sharedNoteJpaRepository.findById(sharedNoteId.value())
                .map(SharedNotePersistenceMapper::toDomainFromPersistence);
    }

    @Override
    public List<SharedNote> findAllByCareCircleId(CareCircleId careCircleId) {
        return sharedNoteJpaRepository.findAllByCareCircleIdOrderByCreatedAtDesc(careCircleId.value()).stream()
                .map(SharedNotePersistenceMapper::toDomainFromPersistence)
                .toList();
    }
}
