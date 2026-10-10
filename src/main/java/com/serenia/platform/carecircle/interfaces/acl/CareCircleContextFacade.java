package com.serenia.platform.carecircle.interfaces.acl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Anti-Corruption Layer facade exposed by the Care Circle bounded context.
 *
 * <p>Alerts and Safety uses it to decide which relatives to notify; Daily Check-in,
 * Wellbeing Monitoring and Alerts and Safety, to check that a relative is linked before
 * showing information; Social Companionship, to resolve message recipients. Only primitive
 * types cross this boundary so no domain class is exposed.</p>
 */
public interface CareCircleContextFacade {

    /** Returns the identifier of the older adult's circle, or empty if it does not exist. */
    Optional<UUID> fetchCareCircleIdByOlderAdultId(UUID olderAdultId);

    /** Returns the older adult who owns the circle, or empty if the circle does not exist. */
    Optional<UUID> fetchOlderAdultIdByCareCircleId(UUID careCircleId);

    /** Returns the relatives with an active link in the older adult's circle. */
    List<UUID> fetchActiveRelativeIdsByOlderAdultId(UUID olderAdultId);

    /** Indicates whether the relative has an active link in the older adult's circle. */
    boolean isActiveRelativeOf(UUID relativeId, UUID olderAdultId);

    /** Indicates whether the user is the owner or an actively linked relative of the circle. */
    boolean hasAccessToCareCircle(UUID userId, UUID careCircleId);
}
