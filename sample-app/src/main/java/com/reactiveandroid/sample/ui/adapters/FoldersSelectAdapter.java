package com.reactiveandroid.sample.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.reactiveandroid.sample.R;
import com.reactiveandroid.sample.mvp.models.Folder;

import java.util.List;

public class FoldersSelectAdapter extends RecyclerView.Adapter<FoldersSelectAdapter.SelectFolderHolder> {

    public interface OnFolderSelectedListener {
        void onFolderSelected(int position, boolean isChecked);
    }

    private LayoutInflater layoutInflater;
    private List<Folder> allFolders;
    private List<Folder> noteFolders;
    private OnFolderSelectedListener onFolderSelectedListener;

    public FoldersSelectAdapter(Context context,
                                List<Folder> allFolders,
                                List<Folder> noteFolders) {
        this.allFolders = allFolders;
        this.noteFolders = noteFolders;
        this.layoutInflater = LayoutInflater.from(context);
    }

    public void setOnFolderSelectedListener(OnFolderSelectedListener onFolderSelectedListener) {
        this.onFolderSelectedListener = onFolderSelectedListener;
    }

    @Override
    public SelectFolderHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.item_folder_select, parent, false);
        return new SelectFolderHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SelectFolderHolder holder, int position) {
        Folder folder = allFolders.get(position);

        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(noteFolders.contains(folder));
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> onFolderSelected(position, isChecked));
        holder.folderName.setText(folder.getName());
    }

    private void onFolderSelected(int position, boolean isChecked) {
        if (onFolderSelectedListener != null) {
            onFolderSelectedListener.onFolderSelected(position, isChecked);
        }
    }

    @Override
    public int getItemCount() {
        return allFolders.size();
    }

    public void setNoteFolders(List<Folder> noteFolders) {
        this.noteFolders = noteFolders;
        notifyDataSetChanged();
    }

    static class SelectFolderHolder extends RecyclerView.ViewHolder {

        final CheckBox checkBox;
        final TextView folderName;

        SelectFolderHolder(View itemView) {
            super(itemView);

            this.checkBox = itemView.findViewById(R.id.checkbox);
            this.folderName = itemView.findViewById(R.id.folder_name_text);
        }

    }

}
