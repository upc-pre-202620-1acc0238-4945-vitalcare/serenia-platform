package com.serenia.platform.carecircle.application.acl;

import com.serenia.platform.carecircle.domain.model.aggregates.CareCircle;
import com.serenia.platform.carecircle.domain.model.valueobjects.CareCircleId;
import com.serenia.platform.carecircle.domain.model.valueobjects.OlderAdultId;
import com.serenia.platform.carecircle.domain.model.valueobjects.RelativeId;
import com.serenia.platform.carecircle.domain.repositories.CareCircleRepository;
import com.serenia.platform.carecircle.interfaces.acl.CareCircleContextFacade;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Default implementation of {@link CareCircleContextFacade}.
 *
 * <p>Resolves the queries of other bounded contexts on circles and links and returns
 * them as primitive types.</p>
 */
@Service
public class CareCircleContextFacadeImpl implements CareCircleContextFacade {

    private final CareCircleRepository careCircleRepository;

    public CareCircleContextFacadeImpl(CareCircleRepository careCircleRepository) {
        this.careCircleRepository = careCircleRepository;
    }

    @Override
    public Optional<UUID> fetchCareCircleIdByOlderAdultId(UUID olderAdultId) {
        return findByOlderAdultId(olderAdultId).map(careCircle -> careCircle.getId().value());
    }

    @Override
    public Optional<UUID> fetchOlderAdultIdByCareCircleId(UUID careCircleId) {
        if (careCircleId == null) return Optional.empty();
        return careCircleRepository.findById(new CareCircleId(careCircleId))
                .map(careCircle -> careCircle.getOlderAdultId().value());
    }

    @Override
    public List<UUID> fetchActiveRelativeIdsByOlderAdultId(UUID olderAdultId) {
        return findByOlderAdultId(olderAdultId)
                .map(careCircle -> careCircle.activeFamilyLinks().stream()
                        .map(link -> link.getRelativeId().value())
                        .toList())
                .orElse(List.of());
    }

    @Override
    public boolean isActiveRelativeOf(UUID relativeId, UUID olderAdultId) {
        if (relativeId == null) return false;
        return findByOlderAdultId(olderAdultId)
                .map(careCircle -> careCircle.isActiveRelative(new RelativeId(relativeId)))
                .orElse(false);
    }

    @Override
    public boolean hasAccessToCareCircle(UUID userId, UUID careCircleId) {
        if (careCircleId == null) return false;
        return careCircleRepository.findById(new CareCircleId(careCircleId))
                .map(careCircle -> careCircle.hasAccess(userId))
                .orElse(false);
    }

    private Optional<CareCircle> findByOlderAdultId(UUID olderAdultId) {
        if (olderAdultId == null) return Optional.empty();
        return careCircleRepository.findByOlderAdultId(new OlderAdultId(olderAdultId));
    }
}
