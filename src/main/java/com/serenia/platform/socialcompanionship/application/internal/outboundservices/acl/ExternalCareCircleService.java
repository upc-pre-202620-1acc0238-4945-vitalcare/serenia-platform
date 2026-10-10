package com.serenia.platform.socialcompanionship.application.internal.outboundservices.acl;

import com.serenia.platform.carecircle.interfaces.acl.CareCircleContextFacade;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Outbound service that consumes the {@link CareCircleContextFacade} to resolve circles,
 * message recipients and access permissions.
 */
@Service("socialCompanionshipExternalCareCircleService")
public class ExternalCareCircleService {

    private final CareCircleContextFacade careCircleContextFacade;

    public ExternalCareCircleService(CareCircleContextFacade careCircleContextFacade) {
        this.careCircleContextFacade = careCircleContextFacade;
    }

    public Optional<UUID> fetchCareCircleId(UUID olderAdultId) {
        return careCircleContextFacade.fetchCareCircleIdByOlderAdultId(olderAdultId);
    }

    public Optional<UUID> fetchOlderAdultId(UUID careCircleId) {
        return careCircleContextFacade.fetchOlderAdultIdByCareCircleId(careCircleId);
    }

    public List<UUID> fetchActiveRelativeIds(UUID olderAdultId) {
        return careCircleContextFacade.fetchActiveRelativeIdsByOlderAdultId(olderAdultId);
    }

    public boolean hasAccessToCareCircle(UUID userId, UUID careCircleId) {
        return careCircleContextFacade.hasAccessToCareCircle(userId, careCircleId);
    }
}
