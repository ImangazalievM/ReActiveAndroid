package com.reactiveandroid.sample.mvp.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.reactiveandroid.query.Delete;
import com.reactiveandroid.query.Select;
import com.reactiveandroid.sample.mvp.models.Folder;
import com.reactiveandroid.sample.mvp.models.Note;
import com.reactiveandroid.sample.mvp.views.FoldersEditView;

import java.util.List;

@InjectViewState
public class FoldersEditPresenter extends MvpPresenter<FoldersEditView> {

    private List<Folder> folders;

    @Override
    protected void onFirstViewAttach() {
        loadFolders();
    }

    public void onFolderCreate(String folderName) {
        Folder newFolder = new Folder(folderName);
        folders.add(newFolder);
        getViewState().updateFoldersList(folders);
        newFolder.saveAsync().subscribe();
    }

    public void onFolderUpdate(String folderName, int position) {
        Folder updatedFolder = folders.get(position);
        updatedFolder.setName(folderName);
        getViewState().updateFoldersList(folders);
        updatedFolder.saveAsync().subscribe();
    }

    public void onFolderDelete(int position) {
        Folder deletedFolder = folders.remove(position);
        getViewState().updateFoldersList(folders);
        deletedFolder.deleteAsync().subscribe();
    }

    private void loadFolders() {
        Select.from(Folder.class)
                .fetchAsync()
                .subscribe(this::onFoldersLoaded);
    }

    private void onFoldersLoaded(List<Folder> folders) {
        this.folders = folders;
        getViewState().updateFoldersList(folders);
    }

    public void onDeleteAllFoldersClicked() {
        folders.clear();
        getViewState().closeScreen();
        Delete.from(Note.class).executeAsync();
    }
}
