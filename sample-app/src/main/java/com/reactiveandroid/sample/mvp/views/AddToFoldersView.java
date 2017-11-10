package com.reactiveandroid.sample.mvp.views;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.reactiveandroid.sample.models.Folder;

import java.util.List;

@StateStrategyType(AddToEndSingleStrategy.class)
public interface AddToFoldersView extends MvpView {

    void showFoldersList(List<Folder> allFolders, List<Folder> noteFolders);
    void updateFoldersList(List<Folder> noteFolders);

}
