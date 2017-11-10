package com.reactiveandroid.sample.ui.adapters.foldersedit;

import android.content.Context;

import com.hannesdorfmann.adapterdelegates3.ListDelegationAdapter;
import com.reactiveandroid.sample.models.Folder;

import java.util.List;

public class FoldersEditAdapter extends ListDelegationAdapter<List<Folder>> {

    private OnFolderChangedListener onFolderChangedListener;
    private OpenCloseable lastOpenedItem;

    public FoldersEditAdapter(Context context) {
        delegatesManager.addDelegate(new EditFolderDelegate(this, context))
                .addDelegate(new NewFolderDelegate(this, context));
    }

    public void setOnFolderChangedListener(OnFolderChangedListener listener) {
        this.onFolderChangedListener = listener;
    }

    @Override
    public int getItemCount() {
        return super.getItemCount() + 1;
    }

    OpenCloseable getLastOpened() {
        return lastOpenedItem;
    }

    void setLastOpened(OpenCloseable lastOpenedItem) {
        this.lastOpenedItem = lastOpenedItem;
    }

    void onFolderCreate(String folderName) {
        if (onFolderChangedListener != null) {
            onFolderChangedListener.onFolderCreate(folderName);
        }
    }

    void onFolderUpdate(String folderName, int position) {
        if (onFolderChangedListener != null) {
            onFolderChangedListener.onFolderUpdate(folderName, position);
        }
    }

    void onFolderDelete(int position) {
        if (onFolderChangedListener != null) {
            onFolderChangedListener.onFolderDelete(position);
        }
    }

}