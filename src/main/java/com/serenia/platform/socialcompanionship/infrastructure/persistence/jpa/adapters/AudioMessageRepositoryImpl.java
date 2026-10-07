package com.serenia.platform.socialcompanionship.infrastructure.persistence.jpa.adapters;

import com.serenia.platform.socialcompanionship.domain.model.aggregates.AudioMessage;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.AudioMessageId;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.CareCircleId;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.MessageStatus;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.MessageType;
import com.serenia.platform.socialcompanionship.domain.repositories.AudioMessageRepository;
import com.serenia.platform.socialcompanionship.infrastructure.persistence.jpa.mappers.AudioMessagePersistenceMapper;
import com.serenia.platform.socialcompanionship.infrastructure.persistence.jpa.repositories.CompanionMessageJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adapter that persists, loads and deletes the {@link AudioMessage} aggregate over the rows of
 * type AUDIO of the companion messages.
 */
@Repository
public class AudioMessageRepositoryImpl implements AudioMessageRepository {

    private final CompanionMessageJpaRepository companionMessageJpaRepository;

    public AudioMessageRepositoryImpl(CompanionMessageJpaRepository companionMessageJpaRepository) {
        this.companionMessageJpaRepository = companionMessageJpaRepository;
    }

    @Override
    public AudioMessage save(AudioMessage audioMessage) {
        var saved = companionMessageJpaRepository.save(AudioMessagePersistenceMapper.toPersistenceFromDomain(audioMessage));
        return AudioMessagePersistenceMapper.toDomainFromPersistence(saved);
    }

    @Override
    public Optional<AudioMessage> findById(AudioMessageId audioMessageId) {
        return companionMessageJpaRepository.findByIdAndType(audioMessageId.value(), MessageType.AUDIO)
                .map(AudioMessagePersistenceMapper::toDomainFromPersistence);
    }

    @Override
    public void delete(AudioMessage audioMessage) {
        companionMessageJpaRepository.deleteById(audioMessage.getId().value());
    }

    @Override
    public List<AudioMessage> findAllSharedByCareCircleId(CareCircleId careCircleId) {
        return companionMessageJpaRepository
                .findAllByCareCircleIdAndTypeAndStatusOrderBySentAtDesc(careCircleId.value(), MessageType.AUDIO, MessageStatus.SHARED)
                .stream()
                .map(AudioMessagePersistenceMapper::toDomainFromPersistence)
                .toList();
    }
}
