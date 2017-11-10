package com.reactiveandroid.sample.ui.activities;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.reactiveandroid.sample.R;
import com.reactiveandroid.sample.models.Note;
import com.reactiveandroid.sample.mvp.presenters.NotesListPresenter;
import com.reactiveandroid.sample.mvp.views.NotesListView;
import com.reactiveandroid.sample.ui.adapters.NotesAdapter;

import java.util.List;

public class MainActivity extends MvpAppCompatActivity implements NotesListView {

    private View notesNotFoundMessage;
    private FloatingActionButton newNoteButton;
    private RecyclerView notesList;
    private NotesAdapter notesAdapter;

    @InjectPresenter
    NotesListPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notesNotFoundMessage = findViewById(R.id.notes_not_found);

        newNoteButton = (FloatingActionButton) findViewById(R.id.fab_new_note);
        newNoteButton.setOnClickListener(view -> presenter.onNewNoteButtonClicked());

        notesList = (RecyclerView) findViewById(R.id.notes_list);
        notesAdapter = new NotesAdapter(this);
        notesAdapter.setOnItemClickListener(position -> presenter.onNoteSelected(position));
        notesList.setAdapter(notesAdapter);
    }

    @Override
    public void updateNotesList(List<Note> notes) {
        notesAdapter.setNotes(notes);
    }

    @Override
    public void showNotesList() {
        notesList.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideNotesList() {
        notesList.setVisibility(View.GONE);
    }

    @Override
    public void showNotesNotFoundMessage() {
        notesNotFoundMessage.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideNotesNotFoundMessage() {
        notesNotFoundMessage.setVisibility(View.GONE);
    }

    @Override
    public void openNoteDetailsScreen(long noteId) {
        startActivity(NoteDetailsActivity.buildIntent(this, noteId));
    }

    @Override
    public void openFoldersEditScreen() {
        startActivity(FoldersEditActivity.buildIntent(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        initSearchItem(menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.open_folders_edit_screen:
                presenter.onOpenFoldersEditScreenClicked();
                break;
            case R.id.delete_all_notes:
                presenter.onDeleteAllNotesClicked();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initSearchItem(Menu menu) {
        MenuItem searchViewMenuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchViewMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                presenter.onSearchQuery(newText);
                return true;
            }
        });
    }

}
