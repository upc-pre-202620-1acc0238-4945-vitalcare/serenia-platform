package com.serenia.platform.carecircle.infrastructure.persistence.jpa.adapters;

import com.serenia.platform.carecircle.domain.model.aggregates.CareCircle;
import com.serenia.platform.carecircle.domain.model.valueobjects.CareCircleId;
import com.serenia.platform.carecircle.domain.model.valueobjects.InvitationCodeValue;
import com.serenia.platform.carecircle.domain.model.valueobjects.InvitationStatus;
import com.serenia.platform.carecircle.domain.model.valueobjects.LinkStatus;
import com.serenia.platform.carecircle.domain.model.valueobjects.OlderAdultId;
import com.serenia.platform.carecircle.domain.model.valueobjects.RelativeId;
import com.serenia.platform.carecircle.domain.repositories.CareCircleRepository;
import com.serenia.platform.carecircle.infrastructure.persistence.jpa.mappers.CareCirclePersistenceMapper;
import com.serenia.platform.carecircle.infrastructure.persistence.jpa.repositories.CareCircleJpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Adapter that implements the domain {@link CareCircleRepository} with Spring Data JPA,
 * persisting and loading the circle together with its codes and links.
 */
@Repository
public class CareCircleRepositoryImpl implements CareCircleRepository {

    private final CareCircleJpaRepository careCircleJpaRepository;

    public CareCircleRepositoryImpl(CareCircleJpaRepository careCircleJpaRepository) {
        this.careCircleJpaRepository = careCircleJpaRepository;
    }

    @Override
    public CareCircle save(CareCircle careCircle) {
        var saved = careCircleJpaRepository.save(CareCirclePersistenceMapper.toPersistenceFromDomain(careCircle));
        return CareCirclePersistenceMapper.toDomainFromPersistence(saved);
    }

    @Override
    public Optional<CareCircle> findById(CareCircleId careCircleId) {
        return careCircleJpaRepository.findById(careCircleId.value())
                .map(CareCirclePersistenceMapper::toDomainFromPersistence);
    }

    @Override
    public Optional<CareCircle> findByOlderAdultId(OlderAdultId olderAdultId) {
        return careCircleJpaRepository.findByOlderAdultId(olderAdultId.value())
                .map(CareCirclePersistenceMapper::toDomainFromPersistence);
    }

    @Override
    public Optional<CareCircle> findByInvitationCode(InvitationCodeValue code) {
        return careCircleJpaRepository.findByInvitationCode(code.value())
                .map(CareCirclePersistenceMapper::toDomainFromPersistence);
    }

    @Override
    public List<CareCircle> findAllByActiveRelativeId(RelativeId relativeId) {
        return careCircleJpaRepository.findAllByFamilyLinkRelativeIdAndStatus(relativeId.value(), LinkStatus.ACTIVE)
                .stream()
                .map(CareCirclePersistenceMapper::toDomainFromPersistence)
                .toList();
    }

    @Override
    public List<CareCircle> findDueInvitationCodes(Instant referenceTime) {
        return careCircleJpaRepository.findAllWithInvitationCodesByStatusExpiringBefore(InvitationStatus.PENDING, referenceTime)
                .stream()
                .map(CareCirclePersistenceMapper::toDomainFromPersistence)
                .toList();
    }
}
