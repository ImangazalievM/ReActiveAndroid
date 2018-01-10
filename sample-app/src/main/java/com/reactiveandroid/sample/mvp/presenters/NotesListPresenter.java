package com.reactiveandroid.sample.mvp.presenters;

import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.reactiveandroid.ReActiveAndroid;
import com.reactiveandroid.internal.notifications.OnModelChangedListener;
import com.reactiveandroid.query.Delete;
import com.reactiveandroid.query.Select;
import com.reactiveandroid.sample.Constants;
import com.reactiveandroid.sample.mvp.models.Note;
import com.reactiveandroid.sample.mvp.views.NotesListView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.schedulers.Schedulers;

@InjectViewState
public class NotesListPresenter extends MvpPresenter<NotesListView> {

    private OnModelChangedListener<Note> onNoteChangedListener;
    private List<Note> notes;

    @Override
    protected void onFirstViewAttach() {
        onNoteChangedListener = (updatedNote, action) -> {

            switch (action) {
                case INSERT:
                    notes.add(updatedNote);
                    break;

                case UPDATE:
                    int updatedNotePosition = notes.indexOf(getNoteById(updatedNote.getId()));
                    notes.set(updatedNotePosition, updatedNote);
                    break;

                case DELETE:
                    notes.remove(getNoteById(updatedNote.getId()));
                    break;
            }

            updateNotesScreenState();
            getViewState().updateNotesList(notes);
        };

        ReActiveAndroid.registerForModelChanges(Note.class, onNoteChangedListener);

        loadNotes();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ReActiveAndroid.unregisterForModelStateChanges(Note.class, onNoteChangedListener);
    }

    public void onNewNoteButtonClicked() {
        getViewState().openNoteDetailsScreen(Constants.NEW_NOTE_ID);
    }

    public void onNoteSelected(int position) {
        Long noteId = notes.get(position).getId();
        getViewState().openNoteDetailsScreen(noteId);
    }

    public void onOpenFoldersEditScreenClicked() {
        getViewState().openFoldersEditScreen();
    }

    public void onDeleteAllNotesClicked() {
        Delete.from(Note.class).executeAsync()
                .subscribeOn(Schedulers.io())
                .subscribe();

        notes.clear();
        updateNotesScreenState();
        getViewState().updateNotesList(notes);
    }

    public void onSearchQuery(String query) {
        if (!query.isEmpty()) {
            List<Note> searchResults = new ArrayList<>();
            for (Note note : notes) {
                if (note.getTitle().startsWith(query)) {
                    searchResults.add(note);
                }
            }
            getViewState().updateNotesList(searchResults);
        } else {
            getViewState().updateNotesList(notes);
        }
    }

    private Note getNoteById(long noteId) {
        for (Note note : notes) {
            if (note.getId() == noteId) {
                return note;
            }
        }
        return null;
    }

    private void loadNotes() {
        Select.from(Note.class)
                .fetchAsync()
                .observeOn(Schedulers.io())
                .subscribe(this::onNotesLoaded);
    }

    private void onNotesLoaded(List<Note> notes) {
        this.notes = notes;
        updateNotesScreenState();
        getViewState().updateNotesList(notes);
    }

    private void updateNotesScreenState() {
        if (notes.isEmpty()) {
            getViewState().hideNotesList();
            getViewState().showNotesNotFoundMessage();
        } else {
            getViewState().hideNotesNotFoundMessage();
            getViewState().showNotesList();
        }
    }

}
