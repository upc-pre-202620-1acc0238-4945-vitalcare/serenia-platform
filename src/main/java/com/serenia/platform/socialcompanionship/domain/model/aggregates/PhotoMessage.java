package com.serenia.platform.socialcompanionship.domain.model.aggregates;

import com.serenia.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import com.serenia.platform.socialcompanionship.domain.exceptions.MessageAlreadySharedException;
import com.serenia.platform.socialcompanionship.domain.exceptions.NotMessageRecipientException;
import com.serenia.platform.socialcompanionship.domain.model.entities.MessageReceipt;
import com.serenia.platform.socialcompanionship.domain.model.events.PhotoMessageDiscarded;
import com.serenia.platform.socialcompanionship.domain.model.events.PhotoMessageSelected;
import com.serenia.platform.socialcompanionship.domain.model.events.PhotoMessageShared;
import com.serenia.platform.socialcompanionship.domain.model.events.PhotoMessageViewed;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.CareCircleId;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.MediaKey;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.MessageStatus;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.OlderAdultId;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.PhotoMessageId;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.RelativeId;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.UserId;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

/**
 * Aggregate root representing a photo a relative shares with the older adult.
 *
 * <p>Controls its transition from draft to shared, and records when the older adult sees it
 * for the first time.</p>
 */
@Getter
public class PhotoMessage extends AbstractDomainAggregateRoot<PhotoMessage> {

    private static final String REQUIRED_VALUE_MESSAGE_KEY = "photo.message.required.value";

    private final PhotoMessageId id;
    private final CareCircleId careCircleId;
    private final RelativeId senderId;
    private final MediaKey mediaKey;
    private MessageStatus status;
    private final Instant createdAt;
    private Instant sentAt;
    private MessageReceipt receipt;

    /** Reconstitution constructor — used by the persistence mapper to rebuild a message with its receipt. */
    public PhotoMessage(PhotoMessageId id, CareCircleId careCircleId, RelativeId senderId, MediaKey mediaKey,
                        MessageStatus status, Instant createdAt, Instant sentAt, MessageReceipt receipt) {
        if (id == null || careCircleId == null || senderId == null || mediaKey == null || status == null
                || createdAt == null)
            throw new IllegalArgumentException(REQUIRED_VALUE_MESSAGE_KEY);
        this.id = id;
        this.careCircleId = careCircleId;
        this.senderId = senderId;
        this.mediaKey = mediaKey;
        this.status = status;
        this.createdAt = createdAt;
        this.sentAt = sentAt;
        this.receipt = receipt;
    }

    /** Creates the message as a draft from the selected photo and registers {@link PhotoMessageSelected}. */
    public static PhotoMessage select(CareCircleId careCircleId, RelativeId senderId, MediaKey mediaKey, Instant selectedAt) {
        var message = new PhotoMessage(PhotoMessageId.generate(), careCircleId, senderId, mediaKey,
                MessageStatus.DRAFT, selectedAt, null, null);
        message.registerDomainEvent(new PhotoMessageSelected(message.id.value(), senderId.value(), selectedAt));
        return message;
    }

    /**
     * Shares the draft with the older adult of the circle, creating their receipt, and registers
     * {@link PhotoMessageShared}. Rejected if already shared.
     */
    public void share(OlderAdultId olderAdultId, Instant sharedAt) {
        ensureDraft();
        if (olderAdultId == null) throw new IllegalArgumentException(REQUIRED_VALUE_MESSAGE_KEY);
        this.receipt = MessageReceipt.unopenedFor(new UserId(olderAdultId.value()));
        this.status = MessageStatus.SHARED;
        this.sentAt = sharedAt;
        registerDomainEvent(new PhotoMessageShared(id.value(), careCircleId.value(), olderAdultId.value(), sharedAt));
    }

    /** Discards the draft before sharing it and registers {@link PhotoMessageDiscarded}. */
    public void discard(Instant discardedAt) {
        ensureDraft();
        registerDomainEvent(new PhotoMessageDiscarded(id.value(), senderId.value(), discardedAt));
    }

    /**
     * Records the first view of the photo by the older adult and registers
     * {@link PhotoMessageViewed}; later views do not change the state.
     */
    public void view(OlderAdultId olderAdultId, Instant viewedAt) {
        if (!isRecipient(olderAdultId.value())) throw new NotMessageRecipientException();
        if (receipt.markAsOpened(viewedAt))
            registerDomainEvent(new PhotoMessageViewed(id.value(), olderAdultId.value(), viewedAt));
    }

    /** Indicates whether the user is the recipient of the photo. */
    public boolean isRecipient(UUID userId) {
        return receipt != null && receipt.getRecipientId().value().equals(userId);
    }

    /** Indicates whether the user uploaded the photo. */
    public boolean isSentBy(UUID userId) {
        return senderId.value().equals(userId);
    }

    /** Indicates whether the older adult already saw the photo. */
    public boolean isViewed() {
        return receipt != null && receipt.isOpened();
    }

    private void ensureDraft() {
        if (status == MessageStatus.SHARED) throw new MessageAlreadySharedException();
    }
}
