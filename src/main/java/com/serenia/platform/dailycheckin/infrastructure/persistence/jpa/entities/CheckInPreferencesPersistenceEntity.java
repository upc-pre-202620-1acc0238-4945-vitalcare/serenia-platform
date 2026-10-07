package com.serenia.platform.dailycheckin.infrastructure.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.time.LocalTime;
import java.util.UUID;

/**
 * JPA persistence entity for a row of the {@code check_in_preferences} table.
 *
 * <p>Each older adult has a single row, guaranteed by the unique index on {@code older_adult_id}.</p>
 */
@Entity
@Table(name = "check_in_preferences",
        uniqueConstraints = @UniqueConstraint(name = "uk_check_in_preferences_older_adult_id", columnNames = "older_adult_id"))
@Getter
@Setter
@NoArgsConstructor
public class CheckInPreferencesPersistenceEntity {

    @Id
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID id;

    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "older_adult_id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID olderAdultId;

    @Column(name = "reminder_time", nullable = false)
    private LocalTime reminderTime;

    @Column(name = "time_limit_minutes", nullable = false)
    private int timeLimitMinutes;

    @Column(name = "simplified_mode", nullable = false)
    private boolean simplifiedMode;

    @Column(name = "updated_at", columnDefinition = "DATETIME(6)", nullable = false)
    private Instant updatedAt;
}
