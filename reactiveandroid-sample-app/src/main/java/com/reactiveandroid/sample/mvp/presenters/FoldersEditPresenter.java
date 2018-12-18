package com.reactiveandroid.sample.mvp.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.reactiveandroid.query.Delete;
import com.reactiveandroid.query.Select;
import com.reactiveandroid.sample.mvp.models.Folder;
import com.reactiveandroid.sample.mvp.models.Note;
import com.reactiveandroid.sample.mvp.views.FoldersEditView;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@InjectViewState
public class FoldersEditPresenter extends MvpPresenter<FoldersEditView> {

    private List<Folder> folders;

    @Override
    protected void onFirstViewAttach() {
        loadFolders();
    }

    public void onFolderCreate(String folderName) {
        if (folders == null) return;

        Folder newFolder = new Folder(folderName);
        folders.add(newFolder);
        getViewState().updateFoldersList(folders);
        newFolder.saveAsync()
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public void onFolderUpdate(String folderName, int position) {
        if (folders == null) return;

        Folder updatedFolder = folders.get(position);
        updatedFolder.setName(folderName);
        getViewState().updateFoldersList(folders);
        updatedFolder.saveAsync()
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public void onFolderDelete(int position) {
        if (folders == null) return;

        Folder deletedFolder = folders.remove(position);
        getViewState().updateFoldersList(folders);
        deletedFolder.deleteAsync()
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    private void loadFolders() {
        Select.from(Folder.class)
                .fetchAsync()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(folders -> {
                    this.folders = folders;
                    getViewState().updateFoldersList(folders);
                });
    }

    public void onDeleteAllFoldersClicked() {
        folders.clear();
        getViewState().closeScreen();
        Delete.from(Note.class).executeAsync()
                .subscribeOn(Schedulers.io())
                .subscribe();
    }
}
