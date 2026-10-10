package com.serenia.platform.socialcompanionship.infrastructure.persistence.jpa.entities;

import com.serenia.platform.socialcompanionship.domain.model.valueobjects.ReminderStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

/**
 * JPA persistence entity for a row of the {@code social_reminders} table.
 *
 * <p>The {@code (older_adult_id, remind_at)} index resolves the active reminders of an older
 * adult; the {@code (status, remind_at)} index, the searches of the periodic jobs.</p>
 */
@Entity
@Table(name = "social_reminders", indexes = {
        @Index(name = "idx_social_reminders_older_adult_remind_at", columnList = "older_adult_id, remind_at"),
        @Index(name = "idx_social_reminders_status_remind_at", columnList = "status, remind_at")})
@Getter
@Setter
@NoArgsConstructor
public class SocialReminderPersistenceEntity {

    @Id
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID id;

    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "older_adult_id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID olderAdultId;

    @Column(name = "title", length = 120, nullable = false, updatable = false)
    private String title;

    @Column(name = "description", length = 300, updatable = false)
    private String description;

    @Column(name = "remind_at", columnDefinition = "DATETIME(6)", nullable = false)
    private Instant remindAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private ReminderStatus status;

    @Column(name = "completed_at", columnDefinition = "DATETIME(6)")
    private Instant completedAt;

    @Column(name = "created_at", columnDefinition = "DATETIME(6)", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", columnDefinition = "DATETIME(6)", nullable = false)
    private Instant updatedAt;
}
