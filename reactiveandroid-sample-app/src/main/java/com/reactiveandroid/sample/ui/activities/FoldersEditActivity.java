package com.reactiveandroid.sample.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.reactiveandroid.sample.R;
import com.reactiveandroid.sample.mvp.models.Folder;
import com.reactiveandroid.sample.mvp.presenters.FoldersEditPresenter;
import com.reactiveandroid.sample.mvp.views.FoldersEditView;
import com.reactiveandroid.sample.ui.adapters.foldersedit.FoldersEditAdapter;
import com.reactiveandroid.sample.ui.adapters.foldersedit.OnFolderChangedListener;

import java.util.List;

public class FoldersEditActivity extends MvpAppCompatActivity implements FoldersEditView {

    public static Intent buildIntent(Context context) {
        return new Intent(context, FoldersEditActivity.class);
    }

    private RecyclerView foldersList;
    private FoldersEditAdapter foldersEditAdapter;

    @InjectPresenter
    FoldersEditPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folders_edit);

        foldersList = (RecyclerView) findViewById(R.id.folders_list);
        foldersEditAdapter = new FoldersEditAdapter(this);
        foldersEditAdapter.setOnFolderChangedListener(new OnFolderChangedListener() {
            @Override
            public void onFolderCreate(String folderName) {
                presenter.onFolderCreate(folderName);
            }

            @Override
            public void onFolderUpdate(String folderName, int position) {
                presenter.onFolderUpdate(folderName, position);
            }

            @Override
            public void onFolderDelete(int position) {
                presenter.onFolderDelete(position);
            }
        });
        foldersList.setAdapter(foldersEditAdapter);
    }

    @Override
    public void updateFoldersList(List<Folder> folders) {
        foldersEditAdapter.setItems(folders);
    }

    @Override
    public void closeScreen() {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.folders_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_all_folders:
                presenter.onDeleteAllFoldersClicked();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
