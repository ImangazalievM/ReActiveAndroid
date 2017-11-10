package com.reactiveandroid.sample.mvp.views;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.reactiveandroid.sample.models.Note;

import java.util.List;

@StateStrategyType(AddToEndSingleStrategy.class)
public interface NoteDetailsView extends MvpView {

    void showNoteDetails(Note note);
    void showFirstSaveNoteMessage();
    void showNoteSavedMessage();
    void showNoteDeletedMessage();
    void showNoteInfo(Note note);
    void openNoteFoldersScreen(long noteId);
    void closeScreen();

}
