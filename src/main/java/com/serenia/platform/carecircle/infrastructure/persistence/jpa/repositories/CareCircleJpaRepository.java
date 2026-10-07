package com.serenia.platform.carecircle.infrastructure.persistence.jpa.repositories;

import com.serenia.platform.carecircle.domain.model.valueobjects.InvitationStatus;
import com.serenia.platform.carecircle.domain.model.valueobjects.LinkStatus;
import com.serenia.platform.carecircle.infrastructure.persistence.jpa.entities.CareCirclePersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link CareCirclePersistenceEntity}.
 *
 * <p>The lookups by older adult and by code run over the unique indexes of
 * {@code older_adult_id} and {@code code}; the search of due codes, over the
 * {@code (status, expires_at)} index.</p>
 */
@Repository
public interface CareCircleJpaRepository extends JpaRepository<CareCirclePersistenceEntity, UUID> {

    Optional<CareCirclePersistenceEntity> findByOlderAdultId(UUID olderAdultId);

    @Query("""
            select c from CareCirclePersistenceEntity c
            join c.invitationCodes i
            where i.code = :code
            """)
    Optional<CareCirclePersistenceEntity> findByInvitationCode(@Param("code") String code);

    @Query("""
            select distinct c from CareCirclePersistenceEntity c
            join c.familyLinks l
            where l.relativeId = :relativeId
              and l.status = :status
            """)
    List<CareCirclePersistenceEntity> findAllByFamilyLinkRelativeIdAndStatus(@Param("relativeId") UUID relativeId,
                                                                            @Param("status") LinkStatus status);

    @Query("""
            select distinct c from CareCirclePersistenceEntity c
            join c.invitationCodes i
            where i.status = :status
              and i.expiresAt <= :referenceTime
            """)
    List<CareCirclePersistenceEntity> findAllWithInvitationCodesByStatusExpiringBefore(
            @Param("status") InvitationStatus status,
            @Param("referenceTime") Instant referenceTime);
}
