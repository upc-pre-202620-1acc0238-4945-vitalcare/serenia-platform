package com.serenia.platform.dailycheckin.application.acl;

import com.serenia.platform.dailycheckin.domain.model.valueobjects.CheckInStatus;
import com.serenia.platform.dailycheckin.domain.model.valueobjects.OlderAdultId;
import com.serenia.platform.dailycheckin.domain.repositories.CheckInRepository;
import com.serenia.platform.dailycheckin.interfaces.acl.DailyCheckInContextFacade;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

/**
 * Default implementation of {@link DailyCheckInContextFacade}.
 *
 * <p>Resolves the moods of a date range from the answered check-ins.</p>
 */
@Service
public class DailyCheckInContextFacadeImpl implements DailyCheckInContextFacade {

    private final CheckInRepository checkInRepository;

    public DailyCheckInContextFacadeImpl(CheckInRepository checkInRepository) {
        this.checkInRepository = checkInRepository;
    }

    @Override
    public Map<LocalDate, String> fetchMoodsByDateRange(UUID olderAdultId, LocalDate fromDate, LocalDate toDate) {
        var moods = new TreeMap<LocalDate, String>();
        if (olderAdultId == null || fromDate == null || toDate == null || toDate.isBefore(fromDate)) return moods;
        checkInRepository.findAllByOlderAdultIdAndCheckDateBetween(new OlderAdultId(olderAdultId), fromDate, toDate).stream()
                .filter(checkIn -> checkIn.getStatus() == CheckInStatus.ANSWERED)
                .forEach(checkIn -> moods.put(checkIn.getCheckDate(), checkIn.getMood().name()));
        return moods;
    }
}
