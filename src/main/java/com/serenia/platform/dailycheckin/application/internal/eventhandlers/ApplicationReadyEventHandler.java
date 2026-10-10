package com.serenia.platform.dailycheckin.application.internal.eventhandlers;

import com.serenia.platform.dailycheckin.domain.model.entities.CheckInQuestion;
import com.serenia.platform.dailycheckin.domain.repositories.CheckInQuestionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Loads the initial question catalog when the application starts, only if it is empty.
 */
// Explicit name avoids conflicts with ApplicationReadyEventHandler beans in sibling bounded contexts
@Service("dailyCheckInApplicationReadyEventHandler")
@Slf4j
public class ApplicationReadyEventHandler {

    private static final String WARM = "cálido";
    private static final String REFLECTIVE = "reflexivo";
    private static final String CHEERFUL = "animado";

    private static final List<CheckInQuestion> INITIAL_QUESTIONS = List.of(
            CheckInQuestion.of("¿Cómo se siente hoy?", WARM),
            CheckInQuestion.of("¿Cómo descansó anoche?", WARM),
            CheckInQuestion.of("¿Qué tal amaneció hoy?", WARM),
            CheckInQuestion.of("¿Cómo está su ánimo esta mañana?", WARM),
            CheckInQuestion.of("¿Se siente con energía hoy?", CHEERFUL),
            CheckInQuestion.of("¿Qué le gustaría hacer hoy?", CHEERFUL),
            CheckInQuestion.of("¿Habló hoy con alguien que quiere?", WARM),
            CheckInQuestion.of("¿Qué fue lo mejor de ayer?", REFLECTIVE),
            CheckInQuestion.of("¿Hay algo que le preocupe hoy?", REFLECTIVE),
            CheckInQuestion.of("¿Disfrutó de su desayuno?", CHEERFUL),
            CheckInQuestion.of("¿Salió a caminar o tomó un poco de sol?", CHEERFUL),
            CheckInQuestion.of("¿Qué recuerdo bonito vino hoy a su mente?", REFLECTIVE),
            CheckInQuestion.of("¿Cómo se siente su cuerpo hoy?", WARM),
            CheckInQuestion.of("¿Tiene ganas de conversar hoy?", WARM),
            CheckInQuestion.of("¿Qué le hizo sonreír recientemente?", CHEERFUL),
            CheckInQuestion.of("¿Hay algo que necesite hoy?", REFLECTIVE),
            CheckInQuestion.of("¿Escuchó música o vio algo que le gustara?", CHEERFUL),
            CheckInQuestion.of("¿Por qué cosa se siente agradecido hoy?", REFLECTIVE),
            CheckInQuestion.of("¿Cómo pasó la tarde de ayer?", WARM),
            CheckInQuestion.of("¿Qué planes tiene para esta semana?", CHEERFUL));

    private final CheckInQuestionRepository checkInQuestionRepository;

    public ApplicationReadyEventHandler(CheckInQuestionRepository checkInQuestionRepository) {
        this.checkInQuestionRepository = checkInQuestionRepository;
    }

    @EventListener
    public void on(ApplicationReadyEvent event) {
        if (checkInQuestionRepository.count() > 0) return;
        checkInQuestionRepository.saveAll(INITIAL_QUESTIONS);
        log.info("Loaded {} questions into the check-in catalog", INITIAL_QUESTIONS.size());
    }
}
