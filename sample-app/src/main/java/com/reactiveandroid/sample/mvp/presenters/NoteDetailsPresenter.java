package com.reactiveandroid.sample.mvp.presenters;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.reactiveandroid.query.Select;
import com.reactiveandroid.sample.models.Note;
import com.reactiveandroid.sample.models.NoteFolderRelation;
import com.reactiveandroid.sample.mvp.views.NoteDetailsView;

import io.reactivex.Observable;
import io.reactivex.Single;

@InjectViewState
public class NoteDetailsPresenter extends MvpPresenter<NoteDetailsView> {

    public static final long NEW_NOTE_ID = -1L;

    private Long noteId;
    private Note note;

    public NoteDetailsPresenter(Long noteId) {
        this.noteId = noteId;
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();

        if (noteId != NEW_NOTE_ID) {
            loadNote();
        }
    }

    public void onSaveNoteClicked(String title, String text) {
        if (note == null) {
            note = new Note(title, text, ColorGenerator.MATERIAL.getRandomColor());
        }

        note.saveAsync().subscribe(noteId -> getViewState().showNoteSavedMessage());
    }

    public void onDeleteNoteClicked() {
        if (note == null) {
            getViewState().closeScreen();
            return;
        }

        note.deleteAsync().subscribe(() -> {
            getViewState().showNoteDeletedMessage();
            getViewState().closeScreen();
        });
    }

    public void onShowNoteInfoClicked() {
        getViewState().showNoteInfo(note);
    }

    public void onOpenNoteFoldersScreenClicked() {
        if (note != null) {
            getViewState().openNoteFoldersScreen(note.getId());
        } else {
            getViewState().showFirstSaveNoteMessage();
        }
    }

    private void loadNote() {
        Single<Note> noteSingle = Select.from(Note.class)
                .where("id = ?", noteId)
                .fetchSingleAsync();

        Select.from(NoteFolderRelation.class)
                .where("note = ?", noteId)
                .fetchAsync()
                .flatMapObservable(Observable::fromIterable)
                .map(NoteFolderRelation::getFolder)
                .toList()
                .zipWith(noteSingle, (folders, note) -> {
                    note.setFolders(folders);
                    return note;
                }).subscribe(this::onNotesLoaded);
    }

    private void onNotesLoaded(Note note) {
        this.note = note;
        getViewState().showNoteDetails(note);
    }

}
