package com.serenia.platform.dailycheckin.infrastructure.persistence.jpa.adapters;

import com.serenia.platform.dailycheckin.domain.model.aggregates.CheckInPreferences;
import com.serenia.platform.dailycheckin.domain.model.valueobjects.OlderAdultId;
import com.serenia.platform.dailycheckin.domain.repositories.CheckInPreferencesRepository;
import com.serenia.platform.dailycheckin.infrastructure.persistence.jpa.entities.CheckInPreferencesPersistenceEntity;
import com.serenia.platform.dailycheckin.infrastructure.persistence.jpa.mappers.CheckInPreferencesPersistenceMapper;
import com.serenia.platform.dailycheckin.infrastructure.persistence.jpa.repositories.CheckInPreferencesJpaRepository;
import com.serenia.platform.dailycheckin.infrastructure.persistence.jpa.repositories.QuestionPauseJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Adapter that implements the domain {@link CheckInPreferencesRepository} with Spring Data JPA,
 * composing the preferences with their latest pause.
 *
 * <p>A row is inserted into {@code question_pauses} when a pause is activated, and removed when
 * the older adult resumes the questions the same day; older pauses are kept as history.</p>
 */
@Repository
@Slf4j
public class CheckInPreferencesRepositoryImpl implements CheckInPreferencesRepository {

    private final CheckInPreferencesJpaRepository checkInPreferencesJpaRepository;
    private final QuestionPauseJpaRepository questionPauseJpaRepository;

    public CheckInPreferencesRepositoryImpl(CheckInPreferencesJpaRepository checkInPreferencesJpaRepository,
                                            QuestionPauseJpaRepository questionPauseJpaRepository) {
        this.checkInPreferencesJpaRepository = checkInPreferencesJpaRepository;
        this.questionPauseJpaRepository = questionPauseJpaRepository;
    }

    @Override
    @Transactional
    public CheckInPreferences save(CheckInPreferences preferences) {
        var saved = checkInPreferencesJpaRepository.save(
                CheckInPreferencesPersistenceMapper.toPersistenceFromDomain(preferences));

        var latestPause = preferences.getLatestPause();
        var storedLatestPause = questionPauseJpaRepository.findFirstByOlderAdultIdOrderByPausedDateDesc(
                preferences.getOlderAdultId().value());
        if (latestPause == null) {
            // A loaded aggregate always carries the stored latest pause, so losing it means it was resumed
            storedLatestPause.ifPresent(questionPauseJpaRepository::delete);
        } else if (storedLatestPause.map(stored -> !stored.getId().equals(latestPause.getId().value())).orElse(true)) {
            questionPauseJpaRepository.save(CheckInPreferencesPersistenceMapper.toPersistenceFromDomain(
                    preferences.getOlderAdultId(), latestPause));
        }

        return new CheckInPreferences(
                preferences.getId(), new OlderAdultId(saved.getOlderAdultId()), preferences.getReminderTime(),
                preferences.getTimeLimit(), saved.isSimplifiedMode(), latestPause, saved.getUpdatedAt());
    }

    @Override
    public Optional<CheckInPreferences> findByOlderAdultId(OlderAdultId olderAdultId) {
        return checkInPreferencesJpaRepository.findByOlderAdultId(olderAdultId.value()).map(this::withLatestPause);
    }

    /**
     * Finds the preferences of every older adult. A row that cannot be converted to the domain, such as
     * a stored time out of the allowed range, is logged and skipped so that it does not hide the others.
     */
    @Override
    public List<CheckInPreferences> findAll() {
        return checkInPreferencesJpaRepository.findAll().stream()
                .flatMap(entity -> toDomainIfValid(entity).stream())
                .toList();
    }

    private Optional<CheckInPreferences> toDomainIfValid(CheckInPreferencesPersistenceEntity entity) {
        try {
            return Optional.of(withLatestPause(entity));
        } catch (RuntimeException e) {
            log.warn("Skipping the check-in preferences of older adult {}: {}", entity.getOlderAdultId(), e.getMessage());
            return Optional.empty();
        }
    }

    private CheckInPreferences withLatestPause(CheckInPreferencesPersistenceEntity entity) {
        var latestPause = questionPauseJpaRepository.findFirstByOlderAdultIdOrderByPausedDateDesc(entity.getOlderAdultId());
        return CheckInPreferencesPersistenceMapper.toDomainFromPersistence(entity, latestPause.orElse(null));
    }
}
