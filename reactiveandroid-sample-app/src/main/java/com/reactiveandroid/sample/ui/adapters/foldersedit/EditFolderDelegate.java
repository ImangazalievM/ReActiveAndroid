package com.reactiveandroid.sample.ui.adapters.foldersedit;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hannesdorfmann.adapterdelegates3.AdapterDelegate;
import com.reactiveandroid.sample.R;
import com.reactiveandroid.sample.mvp.models.Folder;

import java.util.List;

public class EditFolderDelegate extends AdapterDelegate<List<Folder>> {

    private FoldersEditAdapter adapter;
    private LayoutInflater layoutInflater;

    public EditFolderDelegate(FoldersEditAdapter adapter, Context context) {
        this.adapter = adapter;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    protected boolean isForViewType(@NonNull List<Folder> items, int position) {
        return position > 0;
    }

    @NonNull
    @Override
    protected RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        View itemView = layoutInflater.inflate(R.layout.item_folder_edit, parent, false);
        return new EditFolderViewHolder(adapter, itemView);
    }

    @Override
    protected void onBindViewHolder(@NonNull List<Folder> items, int position, @NonNull RecyclerView.ViewHolder holder, @NonNull List<Object> payloads) {
        position--; // minus "new folder" item
        EditFolderViewHolder editFolderViewHolder = (EditFolderViewHolder) holder;
        editFolderViewHolder.onBindViewHolder(items.get(position), position);
    }

}
