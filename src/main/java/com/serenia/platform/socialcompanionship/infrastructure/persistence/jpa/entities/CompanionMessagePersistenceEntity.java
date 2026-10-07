package com.serenia.platform.socialcompanionship.infrastructure.persistence.jpa.entities;

import com.serenia.platform.socialcompanionship.domain.model.valueobjects.MessageStatus;
import com.serenia.platform.socialcompanionship.domain.model.valueobjects.MessageType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * JPA persistence entity for a row of the {@code companion_messages} table, whose type tells
 * whether it is an audio or a photo.
 *
 * <p>Contains the collection of receipts, persisted in cascade with the message. The
 * {@code (care_circle_id, type, sent_at)} index resolves the listing of a circle's shared messages.</p>
 */
@Entity
@Table(name = "companion_messages",
        indexes = @Index(name = "idx_companion_messages_care_circle_type_sent_at", columnList = "care_circle_id, type, sent_at"))
@Getter
@Setter
@NoArgsConstructor
public class CompanionMessagePersistenceEntity {

    @Id
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 10, nullable = false, updatable = false)
    private MessageType type;

    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "care_circle_id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID careCircleId;

    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "sender_id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID senderId;

    @Column(name = "media_key", length = 500, nullable = false, updatable = false)
    private String mediaKey;

    /** Only filled for audio messages. */
    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private MessageStatus status;

    @Column(name = "created_at", columnDefinition = "DATETIME(6)", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "sent_at", columnDefinition = "DATETIME(6)")
    private Instant sentAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "message_receipts",
            joinColumns = @JoinColumn(name = "message_id"),
            uniqueConstraints = @UniqueConstraint(name = "uk_message_receipts_message_recipient",
                    columnNames = {"message_id", "recipient_id"}))
    private List<MessageReceiptPersistenceEntity> receipts = new ArrayList<>();
}
