package com.serenia.platform.socialcompanionship.infrastructure.persistence.jpa.mappers;

import com.serenia.platform.socialcompanionship.domain.model.aggregates.PhotoMessage;
import com.serenia.platform.socialcompanionship.domain.model.entities.MessageReceipt;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.CareCircleId;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.MediaKey;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.MessageType;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.PhotoMessageId;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.RelativeId;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.UserId;
import com.serenia.platform.socialcompanionship.infrastructure.persistence.jpa.entities.CompanionMessagePersistenceEntity;
import com.serenia.platform.socialcompanionship.infrastructure.persistence.jpa.entities.MessageReceiptPersistenceEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Stateless mapper that translates between the {@link PhotoMessage} aggregate, with its receipt,
 * and the rows of type PHOTO of the companion messages.
 */
public final class PhotoMessagePersistenceMapper {

    private PhotoMessagePersistenceMapper() {
    }

    public static PhotoMessage toDomainFromPersistence(CompanionMessagePersistenceEntity entity) {
        var receipt = entity.getReceipts().stream()
                .findFirst()
                .map(row -> new MessageReceipt(new UserId(row.getRecipientId()), row.getOpenedAt()))
                .orElse(null);
        return new PhotoMessage(
                new PhotoMessageId(entity.getId()),
                new CareCircleId(entity.getCareCircleId()),
                new RelativeId(entity.getSenderId()),
                new MediaKey(entity.getMediaKey()),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getSentAt(),
                receipt);
    }

    public static CompanionMessagePersistenceEntity toPersistenceFromDomain(PhotoMessage message) {
        var entity = new CompanionMessagePersistenceEntity();
        entity.setId(message.getId().value());
        entity.setType(MessageType.PHOTO);
        entity.setCareCircleId(message.getCareCircleId().value());
        entity.setSenderId(message.getSenderId().value());
        entity.setMediaKey(message.getMediaKey().value());
        entity.setStatus(message.getStatus());
        entity.setCreatedAt(message.getCreatedAt());
        entity.setSentAt(message.getSentAt());
        var receipt = message.getReceipt();
        entity.setReceipts(receipt == null
                ? new ArrayList<>()
                : new ArrayList<>(List.of(new MessageReceiptPersistenceEntity(receipt.getRecipientId().value(), receipt.getOpenedAt()))));
        return entity;
    }
}
