package com.serenia.platform.carecircle.application.internal.commandservices;

import com.serenia.platform.carecircle.application.internal.errors.CareCircleErrorMapper;
import com.serenia.platform.carecircle.domain.exceptions.CareCircleDomainException;
import com.serenia.platform.carecircle.domain.model.aggregates.SharedNote;
import com.serenia.platform.carecircle.domain.model.commands.CreateSharedNoteCommand;
import com.serenia.platform.carecircle.domain.model.commands.EditSharedNoteCommand;
import com.serenia.platform.carecircle.domain.model.valueobjects.CareCircleId;
import com.serenia.platform.carecircle.domain.model.valueobjects.NoteContent;
import com.serenia.platform.carecircle.domain.model.valueobjects.RelativeId;
import com.serenia.platform.carecircle.domain.model.valueobjects.SharedNoteId;
import com.serenia.platform.carecircle.domain.repositories.CareCircleRepository;
import com.serenia.platform.carecircle.domain.repositories.SharedNoteRepository;
import com.serenia.platform.carecircle.domain.services.SharedNoteCommandService;
import com.serenia.platform.shared.application.outboundservices.events.DomainEventPublisher;
import com.serenia.platform.shared.application.result.ApplicationError;
import com.serenia.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * Application service that handles the creation and edition of shared notes.
 *
 * <p>Only relatives with an active link can write notes; the authorship check on edition
 * is delegated to the aggregate.</p>
 */
@Service
public class SharedNoteCommandServiceImpl implements SharedNoteCommandService {

    private static final String NO_ACCESS = "care.circle.access.denied";
    private static final String AUTHOR_NOT_LINKED = "shared.note.author.not.linked";

    private final SharedNoteRepository sharedNoteRepository;
    private final CareCircleRepository careCircleRepository;
    private final DomainEventPublisher domainEventPublisher;

    public SharedNoteCommandServiceImpl(SharedNoteRepository sharedNoteRepository,
                                        CareCircleRepository careCircleRepository,
                                        DomainEventPublisher domainEventPublisher) {
        this.sharedNoteRepository = sharedNoteRepository;
        this.careCircleRepository = careCircleRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public Result<SharedNote, ApplicationError> handle(CreateSharedNoteCommand command) {
        var careCircleId = new CareCircleId(command.careCircleId());
        var careCircle = careCircleRepository.findById(careCircleId);
        if (careCircle.isEmpty())
            return Result.failure(ApplicationError.notFound("care_circle", command.careCircleId().toString()));

        var authorId = new RelativeId(command.authorId());
        if (!careCircle.get().isActiveRelative(authorId))
            return Result.failure(ApplicationError.forbidden(AUTHOR_NOT_LINKED));

        var sharedNote = SharedNote.create(careCircleId, authorId, new NoteContent(command.content()), Instant.now());
        return Result.success(saveAndPublish(sharedNote));
    }

    @Override
    public Result<SharedNote, ApplicationError> handle(EditSharedNoteCommand command) {
        var sharedNote = sharedNoteRepository.findById(new SharedNoteId(command.sharedNoteId()));
        if (sharedNote.isEmpty())
            return Result.failure(ApplicationError.notFound("shared_note", command.sharedNoteId().toString()));

        var careCircle = careCircleRepository.findById(sharedNote.get().getCareCircleId());
        if (careCircle.isEmpty() || !careCircle.get().hasAccess(command.editorId()))
            return Result.failure(ApplicationError.forbidden(NO_ACCESS));

        try {
            sharedNote.get().edit(new RelativeId(command.editorId()), new NoteContent(command.content()), Instant.now());
            return Result.success(saveAndPublish(sharedNote.get()));
        } catch (CareCircleDomainException e) {
            return Result.failure(CareCircleErrorMapper.toApplicationError(e));
        }
    }

    private SharedNote saveAndPublish(SharedNote sharedNote) {
        var saved = sharedNoteRepository.save(sharedNote);
        var events = List.copyOf(sharedNote.domainEvents());
        sharedNote.clearDomainEvents();
        domainEventPublisher.publish(events);
        return saved;
    }
}
