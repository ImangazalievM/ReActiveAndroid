package com.reactiveandroid.sample.mvp.views;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.reactiveandroid.sample.mvp.models.Folder;

import java.util.List;

@StateStrategyType(AddToEndSingleStrategy.class)
public interface FoldersEditView extends MvpView {

    void updateFoldersList(List<Folder> folders);
    void closeScreen();

}
