package com.reactiveandroid.sample.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.greenfrvr.hashtagview.HashtagView;
import com.reactiveandroid.sample.R;
import com.reactiveandroid.sample.models.Folder;
import com.reactiveandroid.sample.models.Note;
import com.reactiveandroid.sample.mvp.presenters.NoteDetailsPresenter;
import com.reactiveandroid.sample.mvp.presenters.NotesListPresenter;
import com.reactiveandroid.sample.mvp.views.NoteDetailsView;
import com.reactiveandroid.sample.mvp.views.NotesListView;

public class NoteDetailsActivity extends MvpAppCompatActivity implements NoteDetailsView {

    private static final String KEY_NOTE_ID = "note_id";

    public static Intent buildIntent(Context context, Long noteId) {
        Intent intent = new Intent(context, NoteDetailsActivity.class);
        intent.putExtra(KEY_NOTE_ID, noteId);
        return intent;
    }

    private TextView noteTitle;
    private TextView noteText;
    private HashtagView noteFolders;

    @InjectPresenter
    NoteDetailsPresenter presenter;

    @ProvidePresenter
    public NoteDetailsPresenter provideNoteDetailsPresenter() {
        long noteId = getIntent().getLongExtra(KEY_NOTE_ID, NoteDetailsPresenter.NEW_NOTE_ID);
        return new NoteDetailsPresenter(noteId);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        noteTitle = (TextView) findViewById(R.id.note_title);
        noteText = (TextView) findViewById(R.id.note_text);
        noteFolders = (HashtagView) findViewById(R.id.note_folders);
    }

    @Override
    public void showNoteDetails(Note note) {
        noteTitle.setText(note.getTitle());
        noteText.setText(note.getText());
        noteFolders.setData(note.getFolders(), Folder::getName);
    }

    @Override
    public void showFirstSaveNoteMessage() {
        Toast.makeText(this, R.string.first_save_note, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showNoteSavedMessage() {
        Toast.makeText(this, R.string.note_is_saved, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showNoteDeletedMessage() {
        Toast.makeText(this, R.string.note_is_deleted, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showNoteInfo(Note note) {

    }

    @Override
    public void openNoteFoldersScreen(long noteId) {
        startActivity(FolderSelectActivity.buildIntent(this, noteId));
    }

    @Override
    public void closeScreen() {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.note_details, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_note:
                presenter.onSaveNoteClicked(noteTitle.getText().toString(), noteText.getText().toString());
                break;
            case R.id.delete_note:
                presenter.onDeleteNoteClicked();
                break;
            case R.id.note_info:
                presenter.onShowNoteInfoClicked();
                break;
            case R.id.note_folders:
                presenter.onOpenNoteFoldersScreenClicked();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
