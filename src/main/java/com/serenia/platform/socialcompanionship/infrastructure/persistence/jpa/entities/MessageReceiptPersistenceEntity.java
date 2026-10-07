package com.serenia.platform.socialcompanionship.infrastructure.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

/**
 * Persistence representation of a row of the {@code message_receipts} table.
 *
 * <p>Contained in {@link CompanionMessagePersistenceEntity}; the message and the recipient
 * identify the row, so it is mapped as an element of the message's collection.</p>
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageReceiptPersistenceEntity {

    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "recipient_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID recipientId;

    @Column(name = "opened_at", columnDefinition = "DATETIME(6)")
    private Instant openedAt;
}
