package com.serenia.platform.socialcompanionship.domain.model.entities;

import com.serenia.platform.socialcompanionship.domain.model.valueobjects.UserId;
import lombok.Getter;

import java.time.Instant;

/**
 * Reception of a message by one recipient and the moment they opened it.
 *
 * <p>Belongs to the {@link com.serenia.platform.socialcompanionship.domain.model.aggregates.AudioMessage}
 * and {@link com.serenia.platform.socialcompanionship.domain.model.aggregates.PhotoMessage} aggregates;
 * together with the message, the recipient identifies the receipt.</p>
 */
@Getter
public class MessageReceipt {

    private static final String RECIPIENT_REQUIRED_MESSAGE_KEY = "companion.message.recipient.required";

    private final UserId recipientId;
    private Instant openedAt;

    public MessageReceipt(UserId recipientId, Instant openedAt) {
        if (recipientId == null) throw new IllegalArgumentException(RECIPIENT_REQUIRED_MESSAGE_KEY);
        this.recipientId = recipientId;
        this.openedAt = openedAt;
    }

    /** Creates the receipt of a recipient who has not opened the message yet. */
    public static MessageReceipt unopenedFor(UserId recipientId) {
        return new MessageReceipt(recipientId, null);
    }

    /**
     * Records the first opening of the message by the recipient.
     *
     * @return {@code true} if this was the first opening
     */
    public boolean markAsOpened(Instant openedAt) {
        if (isOpened()) return false;
        this.openedAt = openedAt;
        return true;
    }

    /** Indicates whether the recipient already opened the message. */
    public boolean isOpened() {
        return openedAt != null;
    }
}
