package com.serenia.platform.dailycheckin.domain.repositories;

import com.serenia.platform.dailycheckin.domain.model.aggregates.CheckInPreferences;
import com.serenia.platform.dailycheckin.domain.model.valueobjects.OlderAdultId;

import java.util.List;
import java.util.Optional;

/**
 * Persistence contract of the {@link CheckInPreferences} aggregate together with its latest pause.
 */
public interface CheckInPreferencesRepository {

    CheckInPreferences save(CheckInPreferences preferences);

    Optional<CheckInPreferences> findByOlderAdultId(OlderAdultId olderAdultId);

    List<CheckInPreferences> findAll();
}
