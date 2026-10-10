package com.serenia.platform.carecircle.infrastructure.persistence.jpa.adapters;

import com.serenia.platform.carecircle.domain.exceptions.CareShiftDateAlreadyCoveredException;
import com.serenia.platform.carecircle.domain.model.aggregates.CareShift;
import com.serenia.platform.carecircle.domain.model.valueobjects.CareCircleId;
import com.serenia.platform.carecircle.domain.model.valueobjects.CareShiftId;
import com.serenia.platform.carecircle.domain.repositories.CareShiftRepository;
import com.serenia.platform.carecircle.infrastructure.persistence.jpa.mappers.CareShiftPersistenceMapper;
import com.serenia.platform.carecircle.infrastructure.persistence.jpa.repositories.CareShiftJpaRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Adapter that implements the domain {@link CareShiftRepository} with Spring Data JPA.
 *
 * <p>If two relatives take the same date at the same time, the violation of the unique
 * index is translated into a {@link CareShiftDateAlreadyCoveredException}.</p>
 */
@Repository
public class CareShiftRepositoryImpl implements CareShiftRepository {

    private final CareShiftJpaRepository careShiftJpaRepository;

    public CareShiftRepositoryImpl(CareShiftJpaRepository careShiftJpaRepository) {
        this.careShiftJpaRepository = careShiftJpaRepository;
    }

    @Override
    public CareShift save(CareShift careShift) {
        try {
            // Flush immediately so a unique index violation surfaces here and not at commit time
            var saved = careShiftJpaRepository.saveAndFlush(CareShiftPersistenceMapper.toPersistenceFromDomain(careShift));
            return CareShiftPersistenceMapper.toDomainFromPersistence(saved);
        } catch (DataIntegrityViolationException e) {
            throw new CareShiftDateAlreadyCoveredException();
        }
    }

    @Override
    public Optional<CareShift> findById(CareShiftId careShiftId) {
        return careShiftJpaRepository.findById(careShiftId.value())
                .map(CareShiftPersistenceMapper::toDomainFromPersistence);
    }

    @Override
    public boolean existsByCareCircleIdAndShiftDate(CareCircleId careCircleId, LocalDate shiftDate) {
        return careShiftJpaRepository.existsByCareCircleIdAndShiftDate(careCircleId.value(), shiftDate);
    }

    @Override
    public List<CareShift> findAllByCareCircleIdAndShiftDateBetween(CareCircleId careCircleId,
                                                                   LocalDate fromDate, LocalDate toDate) {
        return careShiftJpaRepository
                .findAllByCareCircleIdAndShiftDateBetweenOrderByShiftDateAsc(careCircleId.value(), fromDate, toDate)
                .stream()
                .map(CareShiftPersistenceMapper::toDomainFromPersistence)
                .toList();
    }
}
