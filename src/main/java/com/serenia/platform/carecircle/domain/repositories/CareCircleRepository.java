package com.serenia.platform.carecircle.domain.repositories;

import com.serenia.platform.carecircle.domain.model.aggregates.CareCircle;
import com.serenia.platform.carecircle.domain.model.valueobjects.CareCircleId;
import com.serenia.platform.carecircle.domain.model.valueobjects.InvitationCodeValue;
import com.serenia.platform.carecircle.domain.model.valueobjects.OlderAdultId;
import com.serenia.platform.carecircle.domain.model.valueobjects.RelativeId;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Persistence contract of the {@link CareCircle} aggregate together with its invitation
 * codes and family links.
 */
public interface CareCircleRepository {

    CareCircle save(CareCircle careCircle);

    Optional<CareCircle> findById(CareCircleId careCircleId);

    Optional<CareCircle> findByOlderAdultId(OlderAdultId olderAdultId);

    /** Finds the circle that issued the given invitation code. */
    Optional<CareCircle> findByInvitationCode(InvitationCodeValue code);

    /** Finds the circles in which the relative has an active link. */
    List<CareCircle> findAllByActiveRelativeId(RelativeId relativeId);

    /** Finds the circles holding pending codes whose validity ended at the reference time. */
    List<CareCircle> findDueInvitationCodes(Instant referenceTime);
}
