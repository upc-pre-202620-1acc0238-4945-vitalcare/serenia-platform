package com.serenia.platform.dailycheckin.interfaces.rest.transform;

import com.serenia.platform.dailycheckin.domain.model.aggregates.CheckInPreferences;
import com.serenia.platform.dailycheckin.interfaces.rest.resources.CheckInPreferencesResource;

/** Converts the {@link CheckInPreferences} aggregate into its {@link CheckInPreferencesResource} REST representation. */
public class CheckInPreferencesResourceFromEntityAssembler {
    public static CheckInPreferencesResource toResourceFromEntity(CheckInPreferences preferences, boolean pausedToday) {
        return new CheckInPreferencesResource(
                preferences.getId().value(),
                preferences.getOlderAdultId().value(),
                preferences.getReminderTime().value(),
                preferences.getTimeLimit().minutes(),
                preferences.isSimplifiedMode(),
                pausedToday);
    }
}
