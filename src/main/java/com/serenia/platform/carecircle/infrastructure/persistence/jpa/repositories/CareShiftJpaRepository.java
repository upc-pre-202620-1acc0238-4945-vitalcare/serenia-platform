package com.serenia.platform.carecircle.infrastructure.persistence.jpa.repositories;

import com.serenia.platform.carecircle.infrastructure.persistence.jpa.entities.CareShiftPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link CareShiftPersistenceEntity}.
 *
 * <p>The queries by circle and date run over the unique {@code (care_circle_id, shift_date)} index.</p>
 */
@Repository
public interface CareShiftJpaRepository extends JpaRepository<CareShiftPersistenceEntity, UUID> {

    boolean existsByCareCircleIdAndShiftDate(UUID careCircleId, LocalDate shiftDate);

    List<CareShiftPersistenceEntity> findAllByCareCircleIdAndShiftDateBetweenOrderByShiftDateAsc(
            UUID careCircleId, LocalDate fromDate, LocalDate toDate);
}
