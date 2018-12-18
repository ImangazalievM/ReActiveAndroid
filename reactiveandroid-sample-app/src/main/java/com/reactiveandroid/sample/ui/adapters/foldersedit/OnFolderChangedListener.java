package com.reactiveandroid.sample.ui.adapters.foldersedit;

public interface OnFolderChangedListener {

    void onFolderCreate(String folderName);

    void onFolderUpdate(String folderName, int position);

    void onFolderDelete(int position);

}
