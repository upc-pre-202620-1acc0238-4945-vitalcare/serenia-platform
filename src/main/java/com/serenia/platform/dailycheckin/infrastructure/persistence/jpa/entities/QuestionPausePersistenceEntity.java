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
import java.time.LocalDate;
import java.util.UUID;

/**
 * JPA persistence entity for a row of the {@code question_pauses} table.
 *
 * <p>Keeps the history of the pauses of each older adult; the unique
 * {@code (older_adult_id, paused_date)} index resolves the search of the latest one.</p>
 */
@Entity
@Table(name = "question_pauses",
        uniqueConstraints = @UniqueConstraint(name = "uk_question_pauses_older_adult_paused_date",
                columnNames = {"older_adult_id", "paused_date"}))
@Getter
@Setter
@NoArgsConstructor
public class QuestionPausePersistenceEntity {

    @Id
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID id;

    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "older_adult_id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID olderAdultId;

    @Column(name = "paused_date", nullable = false, updatable = false)
    private LocalDate pausedDate;

    @Column(name = "created_at", columnDefinition = "DATETIME(6)", nullable = false, updatable = false)
    private Instant createdAt;
}
