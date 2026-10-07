package com.serenia.platform.wellbeingmonitoring.domain.repositories;

import com.serenia.platform.wellbeingmonitoring.domain.model.aggregates.WellbeingInsight;
import com.serenia.platform.wellbeingmonitoring.domain.model.entities.SmallWin;
import com.serenia.platform.wellbeingmonitoring.domain.model.entities.WellbeingSuggestion;
import com.serenia.platform.wellbeingmonitoring.domain.model.valueobjects.OlderAdultId;

import java.time.Instant;
import java.util.List;

/**
 * Persistence contract of the {@link WellbeingInsight} aggregate and of the queries of
 * suggestions and small wins.
 */
public interface WellbeingInsightRepository {

    /** Composes the aggregate; an older adult with nothing evaluated yet gets an empty one. */
    WellbeingInsight findByOlderAdultId(OlderAdultId olderAdultId);

    /**
     * Persists the changes of the aggregate: its latest pattern, its suggestions and its new small wins.
     *
     * @throws com.serenia.platform.wellbeingmonitoring.domain.exceptions.SmallWinAlreadyRecordedException
     *         when a check-in already produced a small win
     */
    WellbeingInsight save(WellbeingInsight insight);

    List<WellbeingSuggestion> findActiveSuggestionsByOlderAdultId(OlderAdultId olderAdultId);

    /** Returns the small wins recorded from {@code from}, inclusive, to {@code to}, exclusive, ordered by date. */
    List<SmallWin> findSmallWinsByOlderAdultIdBetween(OlderAdultId olderAdultId, Instant from, Instant to);
}
