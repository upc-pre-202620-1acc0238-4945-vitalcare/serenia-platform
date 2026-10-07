package com.serenia.platform.socialcompanionship.infrastructure.persistence.jpa.mappers;

import com.serenia.platform.socialcompanionship.domain.model.aggregates.AudioMessage;
import com.serenia.platform.socialcompanionship.domain.model.entities.MessageReceipt;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.AudioDuration;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.AudioMessageId;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.CareCircleId;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.MediaKey;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.MessageType;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.OlderAdultId;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.UserId;
import com.serenia.platform.socialcompanionship.infrastructure.persistence.jpa.entities.CompanionMessagePersistenceEntity;
import com.serenia.platform.socialcompanionship.infrastructure.persistence.jpa.entities.MessageReceiptPersistenceEntity;

import java.util.ArrayList;

/**
 * Stateless mapper that translates between the {@link AudioMessage} aggregate, with its receipts,
 * and the rows of type AUDIO of the companion messages.
 */
public final class AudioMessagePersistenceMapper {

    private AudioMessagePersistenceMapper() {
    }

    public static AudioMessage toDomainFromPersistence(CompanionMessagePersistenceEntity entity) {
        return new AudioMessage(
                new AudioMessageId(entity.getId()),
                new CareCircleId(entity.getCareCircleId()),
                new OlderAdultId(entity.getSenderId()),
                new MediaKey(entity.getMediaKey()),
                new AudioDuration(entity.getDurationSeconds()),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getSentAt(),
                entity.getReceipts().stream()
                        .map(receipt -> new MessageReceipt(new UserId(receipt.getRecipientId()), receipt.getOpenedAt()))
                        .toList());
    }

    public static CompanionMessagePersistenceEntity toPersistenceFromDomain(AudioMessage message) {
        var entity = new CompanionMessagePersistenceEntity();
        entity.setId(message.getId().value());
        entity.setType(MessageType.AUDIO);
        entity.setCareCircleId(message.getCareCircleId().value());
        entity.setSenderId(message.getSenderId().value());
        entity.setMediaKey(message.getMediaKey().value());
        entity.setDurationSeconds(message.getDuration().seconds());
        entity.setStatus(message.getStatus());
        entity.setCreatedAt(message.getCreatedAt());
        entity.setSentAt(message.getSentAt());
        entity.setReceipts(new ArrayList<>(message.getReceipts().stream()
                .map(receipt -> new MessageReceiptPersistenceEntity(receipt.getRecipientId().value(), receipt.getOpenedAt()))
                .toList()));
        return entity;
    }
}
