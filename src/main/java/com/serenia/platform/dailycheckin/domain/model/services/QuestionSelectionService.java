package com.serenia.platform.dailycheckin.domain.model.services;

import com.serenia.platform.dailycheckin.domain.model.entities.CheckInQuestion;
import com.serenia.platform.dailycheckin.domain.model.valueobjects.CheckInQuestionId;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.random.RandomGenerator;

/**
 * Domain service that picks the question of the day.
 *
 * <p>Questions never used are preferred; among the used ones, the one presented longest ago is
 * picked. That way no question repeats before the whole active set has been asked, and the
 * question always differs from the previous day's while there is more than one active question.</p>
 */
public class QuestionSelectionService {

    private static final String NO_ACTIVE_QUESTIONS_MESSAGE_KEY = "check.in.question.catalog.empty";

    private final RandomGenerator random;

    public QuestionSelectionService() {
        this(new Random());
    }

    public QuestionSelectionService(RandomGenerator random) {
        this.random = random;
    }

    /**
     * Selects the question of the day.
     *
     * @param activeQuestions   the questions of the catalog that can be presented
     * @param recentQuestionIds the questions of the latest check-ins, the most recent first
     * @return the identifier of the selected question
     */
    public CheckInQuestionId select(List<CheckInQuestion> activeQuestions, List<CheckInQuestionId> recentQuestionIds) {
        var candidates = activeQuestions.stream().filter(CheckInQuestion::isActive).toList();
        if (candidates.isEmpty()) throw new IllegalStateException(NO_ACTIVE_QUESTIONS_MESSAGE_KEY);

        // Position of the last time each question was used: 0 is yesterday, higher is older
        var lastUsedPosition = new HashMap<CheckInQuestionId, Integer>();
        for (int position = 0; position < recentQuestionIds.size(); position++) {
            lastUsedPosition.putIfAbsent(recentQuestionIds.get(position), position);
        }

        var neverUsed = candidates.stream()
                .filter(question -> !lastUsedPosition.containsKey(question.getId()))
                .toList();
        if (!neverUsed.isEmpty()) return neverUsed.get(random.nextInt(neverUsed.size())).getId();

        return candidates.stream()
                .max(Comparator.comparingInt(question -> lastUsedPosition.get(question.getId())))
                .orElseThrow()
                .getId();
    }
}
