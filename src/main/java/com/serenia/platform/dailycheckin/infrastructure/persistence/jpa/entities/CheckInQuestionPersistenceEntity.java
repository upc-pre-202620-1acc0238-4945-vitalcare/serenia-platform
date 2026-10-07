package com.serenia.platform.dailycheckin.infrastructure.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

/**
 * JPA persistence entity for a row of the {@code check_in_questions} table.
 */
@Entity
@Table(name = "check_in_questions")
@Getter
@Setter
@NoArgsConstructor
public class CheckInQuestionPersistenceEntity {

    @Id
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "text", length = 200, nullable = false)
    private String text;

    @Column(name = "tone", length = 30)
    private String tone;

    @Column(name = "active", nullable = false)
    private boolean active;
}
