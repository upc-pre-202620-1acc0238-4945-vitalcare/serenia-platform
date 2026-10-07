package com.serenia.platform.wellbeingmonitoring.application.internal.outboundservices.acl;

import com.serenia.platform.dailycheckin.interfaces.acl.DailyCheckInContextFacade;
import com.serenia.platform.wellbeingmonitoring.domain.model.valueobjects.MoodLevel;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

/**
 * Outbound service that consumes the {@link DailyCheckInContextFacade} and translates the
 * reported moods into {@link MoodLevel}.
 */
@Service("wellbeingMonitoringExternalDailyCheckInService")
public class ExternalDailyCheckInService {

    private final DailyCheckInContextFacade dailyCheckInContextFacade;

    public ExternalDailyCheckInService(DailyCheckInContextFacade dailyCheckInContextFacade) {
        this.dailyCheckInContextFacade = dailyCheckInContextFacade;
    }

    /**
     * Returns the moods of the answered check-ins between both dates, inclusive.
     * Values Wellbeing Monitoring does not recognize are ignored, as if the day had no answer.
     */
    public Map<LocalDate, MoodLevel> fetchMoods(UUID olderAdultId, LocalDate fromDate, LocalDate toDate) {
        var moods = new TreeMap<LocalDate, MoodLevel>();
        dailyCheckInContextFacade.fetchMoodsByDateRange(olderAdultId, fromDate, toDate).forEach((date, mood) -> {
            try {
                moods.put(date, MoodLevel.valueOf(mood));
            } catch (IllegalArgumentException e) {
                // Unknown mood value: treated as a day without an answer
            }
        });
        return moods;
    }
}
