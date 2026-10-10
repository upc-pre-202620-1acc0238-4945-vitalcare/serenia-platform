package com.serenia.platform.socialcompanionship.domain.model.aggregates;

import com.serenia.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import com.serenia.platform.socialcompanionship.domain.exceptions.MessageAlreadySharedException;
import com.serenia.platform.socialcompanionship.domain.exceptions.MessageWithoutRecipientsException;
import com.serenia.platform.socialcompanionship.domain.exceptions.NotMessageRecipientException;
import com.serenia.platform.socialcompanionship.domain.model.entities.MessageReceipt;
import com.serenia.platform.socialcompanionship.domain.model.events.AudioMessageDiscarded;
import com.serenia.platform.socialcompanionship.domain.model.events.AudioMessagePlayed;
import com.serenia.platform.socialcompanionship.domain.model.events.AudioMessageRecorded;
import com.serenia.platform.socialcompanionship.domain.model.events.AudioMessageShared;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.AudioDuration;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.AudioMessageId;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.CareCircleId;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.MediaKey;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.MessageStatus;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.OlderAdultId;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.RelativeId;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.UserId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Aggregate root representing a voice message the older adult records for their relatives.
 *
 * <p>Controls its transition from draft to shared, and records when each recipient relative
 * plays it for the first time.</p>
 */
public class AudioMessage extends AbstractDomainAggregateRoot<AudioMessage> {

    private static final String REQUIRED_VALUE_MESSAGE_KEY = "audio.message.required.value";

    private final AudioMessageId id;
    private final CareCircleId careCircleId;
    private final OlderAdultId senderId;
    private final MediaKey mediaKey;
    private final AudioDuration duration;
    private MessageStatus status;
    private final Instant createdAt;
    private Instant sentAt;
    private final List<MessageReceipt> receipts;

    /** Reconstitution constructor — used by the persistence mapper to rebuild a message with its receipts. */
    public AudioMessage(AudioMessageId id, CareCircleId careCircleId, OlderAdultId senderId, MediaKey mediaKey,
                        AudioDuration duration, MessageStatus status, Instant createdAt, Instant sentAt,
                        List<MessageReceipt> receipts) {
        if (id == null || careCircleId == null || senderId == null || mediaKey == null || duration == null
                || status == null || createdAt == null)
            throw new IllegalArgumentException(REQUIRED_VALUE_MESSAGE_KEY);
        this.id = id;
        this.careCircleId = careCircleId;
        this.senderId = senderId;
        this.mediaKey = mediaKey;
        this.duration = duration;
        this.status = status;
        this.createdAt = createdAt;
        this.sentAt = sentAt;
        this.receipts = receipts == null ? new ArrayList<>() : new ArrayList<>(receipts);
    }

    /** Creates the message as a draft from the recorded audio and registers {@link AudioMessageRecorded}. */
    public static AudioMessage record(CareCircleId careCircleId, OlderAdultId senderId, MediaKey mediaKey,
                                      AudioDuration duration, Instant recordedAt) {
        var message = new AudioMessage(AudioMessageId.generate(), careCircleId, senderId, mediaKey, duration,
                MessageStatus.DRAFT, recordedAt, null, List.of());
        message.registerDomainEvent(new AudioMessageRecorded(message.id.value(), senderId.value(), recordedAt));
        return message;
    }

    /**
     * Shares the draft with the linked relatives, creating one receipt for each of them, and
     * registers {@link AudioMessageShared}. Rejected if already shared or without recipients.
     */
    public void share(List<RelativeId> recipientIds, Instant sharedAt) {
        ensureDraft();
        if (recipientIds == null || recipientIds.isEmpty()) throw new MessageWithoutRecipientsException();
        recipientIds.stream().distinct()
                .forEach(recipientId -> receipts.add(MessageReceipt.unopenedFor(new UserId(recipientId.value()))));
        this.status = MessageStatus.SHARED;
        this.sentAt = sharedAt;
        registerDomainEvent(new AudioMessageShared(
                id.value(), careCircleId.value(),
                receipts.stream().map(receipt -> receipt.getRecipientId().value()).toList(), sharedAt));
    }

    /** Discards the draft before sharing it and registers {@link AudioMessageDiscarded}. */
    public void discard(Instant discardedAt) {
        ensureDraft();
        registerDomainEvent(new AudioMessageDiscarded(id.value(), senderId.value(), discardedAt));
    }

    /**
     * Records the first play of the audio by a recipient relative and registers
     * {@link AudioMessagePlayed}; later plays do not change the state.
     */
    public void play(RelativeId relativeId, Instant playedAt) {
        var receipt = receiptOf(relativeId.value()).orElseThrow(NotMessageRecipientException::new);
        if (receipt.markAsOpened(playedAt))
            registerDomainEvent(new AudioMessagePlayed(id.value(), relativeId.value(), playedAt));
    }

    /** Indicates whether the user is a recipient of the message. */
    public boolean isRecipient(UUID userId) {
        return receiptOf(userId).isPresent();
    }

    /** Indicates whether the user recorded the message. */
    public boolean isSentBy(UUID userId) {
        return senderId.value().equals(userId);
    }

    /** Indicates whether the given recipient already played the audio. */
    public boolean isPlayedBy(UUID userId) {
        return receiptOf(userId).map(MessageReceipt::isOpened).orElse(false);
    }

    private Optional<MessageReceipt> receiptOf(UUID userId) {
        return receipts.stream().filter(receipt -> receipt.getRecipientId().value().equals(userId)).findFirst();
    }

    private void ensureDraft() {
        if (status == MessageStatus.SHARED) throw new MessageAlreadySharedException();
    }

    public AudioMessageId getId() {
        return id;
    }

    public CareCircleId getCareCircleId() {
        return careCircleId;
    }

    public OlderAdultId getSenderId() {
        return senderId;
    }

    public MediaKey getMediaKey() {
        return mediaKey;
    }

    public AudioDuration getDuration() {
        return duration;
    }

    public MessageStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getSentAt() {
        return sentAt;
    }

    public List<MessageReceipt> getReceipts() {
        return Collections.unmodifiableList(receipts);
    }
}
