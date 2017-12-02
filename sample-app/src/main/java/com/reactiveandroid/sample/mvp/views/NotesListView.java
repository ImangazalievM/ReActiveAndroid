package com.reactiveandroid.sample.mvp.views;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.reactiveandroid.sample.mvp.models.Note;

import java.util.List;

@StateStrategyType(AddToEndSingleStrategy.class)
public interface NotesListView extends MvpView {

    void updateNotesList(List<Note> notes);
    void showNotesList();
    void hideNotesList();
    void showNotesNotFoundMessage();
    void hideNotesNotFoundMessage();
    void openNoteDetailsScreen(long noteId);
    void openFoldersEditScreen();

}
