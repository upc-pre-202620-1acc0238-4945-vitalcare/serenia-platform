package com.serenia.platform.wellbeingmonitoring.infrastructure.persistence.jpa.adapters;

import com.serenia.platform.wellbeingmonitoring.domain.exceptions.SmallWinAlreadyRecordedException;
import com.serenia.platform.wellbeingmonitoring.domain.model.aggregates.WellbeingInsight;
import com.serenia.platform.wellbeingmonitoring.domain.model.entities.SmallWin;
import com.serenia.platform.wellbeingmonitoring.domain.model.entities.WellbeingSuggestion;
import com.serenia.platform.wellbeingmonitoring.domain.model.valueobjects.OlderAdultId;
import com.serenia.platform.wellbeingmonitoring.domain.model.valueobjects.SuggestionStatus;
import com.serenia.platform.wellbeingmonitoring.domain.repositories.WellbeingInsightRepository;
import com.serenia.platform.wellbeingmonitoring.infrastructure.persistence.jpa.mappers.WellbeingInsightPersistenceMapper;
import com.serenia.platform.wellbeingmonitoring.infrastructure.persistence.jpa.repositories.SmallWinJpaRepository;
import com.serenia.platform.wellbeingmonitoring.infrastructure.persistence.jpa.repositories.WellbeingPatternJpaRepository;
import com.serenia.platform.wellbeingmonitoring.infrastructure.persistence.jpa.repositories.WellbeingSuggestionJpaRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * Adapter that implements the domain {@link WellbeingInsightRepository} with Spring Data JPA.
 *
 * <p>Composes the aggregate from its latest pattern and its active suggestions and persists its
 * changes in the three tables. If the same check-in is evaluated twice, the unique index of
 * {@code small_wins.check_in_id} prevents duplicating the small win.</p>
 */
@Repository
public class WellbeingInsightRepositoryImpl implements WellbeingInsightRepository {

    private final WellbeingPatternJpaRepository wellbeingPatternJpaRepository;
    private final WellbeingSuggestionJpaRepository wellbeingSuggestionJpaRepository;
    private final SmallWinJpaRepository smallWinJpaRepository;

    public WellbeingInsightRepositoryImpl(WellbeingPatternJpaRepository wellbeingPatternJpaRepository,
                                          WellbeingSuggestionJpaRepository wellbeingSuggestionJpaRepository,
                                          SmallWinJpaRepository smallWinJpaRepository) {
        this.wellbeingPatternJpaRepository = wellbeingPatternJpaRepository;
        this.wellbeingSuggestionJpaRepository = wellbeingSuggestionJpaRepository;
        this.smallWinJpaRepository = smallWinJpaRepository;
    }

    @Override
    public WellbeingInsight findByOlderAdultId(OlderAdultId olderAdultId) {
        var latestPattern = wellbeingPatternJpaRepository.findFirstByOlderAdultIdOrderByDetectedAtDesc(olderAdultId.value())
                .map(WellbeingInsightPersistenceMapper::toDomainFromPersistence)
                .orElse(null);
        return new WellbeingInsight(olderAdultId, latestPattern, findActiveSuggestionsByOlderAdultId(olderAdultId));
    }

    @Override
    @Transactional
    public WellbeingInsight save(WellbeingInsight insight) {
        var olderAdultId = insight.getOlderAdultId();
        if (insight.getLatestDiscomfortPattern() != null)
            wellbeingPatternJpaRepository.save(WellbeingInsightPersistenceMapper.toPersistenceFromDomain(
                    olderAdultId, insight.getLatestDiscomfortPattern()));

        wellbeingSuggestionJpaRepository.saveAll(insight.getActiveSuggestions().stream()
                .map(suggestion -> WellbeingInsightPersistenceMapper.toPersistenceFromDomain(olderAdultId, suggestion))
                .toList());

        if (!insight.getNewSmallWins().isEmpty()) {
            try {
                smallWinJpaRepository.saveAllAndFlush(insight.getNewSmallWins().stream()
                        .map(smallWin -> WellbeingInsightPersistenceMapper.toPersistenceFromDomain(olderAdultId, smallWin))
                        .toList());
            } catch (DataIntegrityViolationException e) {
                throw new SmallWinAlreadyRecordedException();
            }
        }
        return insight;
    }

    @Override
    public List<WellbeingSuggestion> findActiveSuggestionsByOlderAdultId(OlderAdultId olderAdultId) {
        return wellbeingSuggestionJpaRepository
                .findAllByOlderAdultIdAndStatusOrderByIssuedAtDesc(olderAdultId.value(), SuggestionStatus.ACTIVE)
                .stream()
                .map(WellbeingInsightPersistenceMapper::toDomainFromPersistence)
                .toList();
    }

    @Override
    public List<SmallWin> findSmallWinsByOlderAdultIdBetween(OlderAdultId olderAdultId, Instant from, Instant to) {
        return smallWinJpaRepository.findAllByOlderAdultIdRecordedBetween(olderAdultId.value(), from, to).stream()
                .map(WellbeingInsightPersistenceMapper::toDomainFromPersistence)
                .toList();
    }
}
