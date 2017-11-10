package com.reactiveandroid.sample.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.reactiveandroid.sample.R;
import com.reactiveandroid.sample.models.Folder;
import com.reactiveandroid.sample.mvp.presenters.FolderSelectPresenter;
import com.reactiveandroid.sample.mvp.views.AddToFoldersView;
import com.reactiveandroid.sample.ui.adapters.FoldersSelectAdapter;

import java.util.List;

public class FolderSelectActivity extends MvpAppCompatActivity implements AddToFoldersView {

    private static final String KEY_NOTE_ID = "note_id";

    public static Intent buildIntent(Context context, Long noteId) {
        Intent intent = new Intent(context, FolderSelectActivity.class);
        intent.putExtra(KEY_NOTE_ID, noteId);
        return intent;
    }

    private RecyclerView foldersList;
    private FoldersSelectAdapter foldersSelectAdapter;

    @InjectPresenter
    FolderSelectPresenter presenter;

    @ProvidePresenter
    public FolderSelectPresenter provideFolderSelectPresenter() {
        long noteId = getIntent().getLongExtra(KEY_NOTE_ID, -1);
        return new FolderSelectPresenter(noteId);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folders_edit);

        foldersList = (RecyclerView) findViewById(R.id.folders_list);
    }

    @Override
    public void showFoldersList(List<Folder> allFolders, List<Folder> noteFolders) {
        foldersSelectAdapter = new FoldersSelectAdapter(this, allFolders, noteFolders);
        foldersSelectAdapter.setOnFolderSelectedListener((position, isChecked) -> presenter.onFolderSelected(position, isChecked));
        foldersList.setAdapter(foldersSelectAdapter);
    }

    @Override
    public void updateFoldersList(List<Folder> noteFolders) {
        foldersSelectAdapter.setNoteFolders(noteFolders);
    }

}
