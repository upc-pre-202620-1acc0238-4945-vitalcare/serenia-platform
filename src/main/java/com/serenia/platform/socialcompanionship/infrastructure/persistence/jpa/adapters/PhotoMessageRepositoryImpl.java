package com.serenia.platform.socialcompanionship.infrastructure.persistence.jpa.adapters;

import com.serenia.platform.socialcompanionship.domain.model.aggregates.PhotoMessage;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.CareCircleId;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.MessageStatus;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.MessageType;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.PhotoMessageId;
import com.serenia.platform.socialcompanionship.domain.repositories.PhotoMessageRepository;
import com.serenia.platform.socialcompanionship.infrastructure.persistence.jpa.mappers.PhotoMessagePersistenceMapper;
import com.serenia.platform.socialcompanionship.infrastructure.persistence.jpa.repositories.CompanionMessageJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adapter that persists, loads and deletes the {@link PhotoMessage} aggregate over the rows of
 * type PHOTO of the companion messages.
 */
@Repository
public class PhotoMessageRepositoryImpl implements PhotoMessageRepository {

    private final CompanionMessageJpaRepository companionMessageJpaRepository;

    public PhotoMessageRepositoryImpl(CompanionMessageJpaRepository companionMessageJpaRepository) {
        this.companionMessageJpaRepository = companionMessageJpaRepository;
    }

    @Override
    public PhotoMessage save(PhotoMessage photoMessage) {
        var saved = companionMessageJpaRepository.save(PhotoMessagePersistenceMapper.toPersistenceFromDomain(photoMessage));
        return PhotoMessagePersistenceMapper.toDomainFromPersistence(saved);
    }

    @Override
    public Optional<PhotoMessage> findById(PhotoMessageId photoMessageId) {
        return companionMessageJpaRepository.findByIdAndType(photoMessageId.value(), MessageType.PHOTO)
                .map(PhotoMessagePersistenceMapper::toDomainFromPersistence);
    }

    @Override
    public void delete(PhotoMessage photoMessage) {
        companionMessageJpaRepository.deleteById(photoMessage.getId().value());
    }

    @Override
    public List<PhotoMessage> findAllSharedByCareCircleId(CareCircleId careCircleId) {
        return companionMessageJpaRepository
                .findAllByCareCircleIdAndTypeAndStatusOrderBySentAtDesc(careCircleId.value(), MessageType.PHOTO, MessageStatus.SHARED)
                .stream()
                .map(PhotoMessagePersistenceMapper::toDomainFromPersistence)
                .toList();
    }
}
