package com.serenia.platform.carecircle.domain.model.aggregates;

import com.serenia.platform.carecircle.domain.exceptions.SharedNoteNotAuthoredException;
import com.serenia.platform.carecircle.domain.model.events.SharedNoteCreated;
import com.serenia.platform.carecircle.domain.model.events.SharedNoteEdited;
import com.serenia.platform.carecircle.domain.model.valueobjects.CareCircleId;
import com.serenia.platform.carecircle.domain.model.valueobjects.NoteContent;
import com.serenia.platform.carecircle.domain.model.valueobjects.RelativeId;
import com.serenia.platform.carecircle.domain.model.valueobjects.SharedNoteId;
import com.serenia.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.time.Instant;

/**
 * Aggregate root representing a note with relevant information about the older adult,
 * visible to every relative of the care circle.
 */
@Getter
public class SharedNote extends AbstractDomainAggregateRoot<SharedNote> {

    private static final String REQUIRED_VALUE_MESSAGE_KEY = "shared.note.required.value";

    private final SharedNoteId id;
    private final CareCircleId careCircleId;
    private final RelativeId authorId;
    private NoteContent content;
    private final Instant createdAt;
    private Instant updatedAt;

    /**
     * Reconstitution constructor — used by the persistence mapper to rebuild a note
     * from stored data.
     */
    public SharedNote(SharedNoteId id, CareCircleId careCircleId, RelativeId authorId, NoteContent content,
                      Instant createdAt, Instant updatedAt) {
        if (id == null || careCircleId == null || authorId == null || content == null
                || createdAt == null || updatedAt == null)
            throw new IllegalArgumentException(REQUIRED_VALUE_MESSAGE_KEY);
        this.id = id;
        this.careCircleId = careCircleId;
        this.authorId = authorId;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /** Creates a new note and registers {@link SharedNoteCreated}. */
    public static SharedNote create(CareCircleId careCircleId, RelativeId authorId, NoteContent content, Instant createdAt) {
        var sharedNote = new SharedNote(SharedNoteId.generate(), careCircleId, authorId, content, createdAt, createdAt);
        sharedNote.registerDomainEvent(new SharedNoteCreated(
                sharedNote.id.value(), careCircleId.value(), authorId.value(), createdAt));
        return sharedNote;
    }

    /** Replaces the content of the note; only the author can edit it. Registers {@link SharedNoteEdited}. */
    public void edit(RelativeId editorId, NoteContent content, Instant updatedAt) {
        if (!isAuthoredBy(editorId)) throw new SharedNoteNotAuthoredException();
        if (content == null) throw new IllegalArgumentException(REQUIRED_VALUE_MESSAGE_KEY);
        this.content = content;
        this.updatedAt = updatedAt;
        registerDomainEvent(new SharedNoteEdited(id.value(), updatedAt));
    }

    /** Indicates whether the note was written by the given relative. */
    public boolean isAuthoredBy(RelativeId relativeId) {
        return authorId.equals(relativeId);
    }
}
