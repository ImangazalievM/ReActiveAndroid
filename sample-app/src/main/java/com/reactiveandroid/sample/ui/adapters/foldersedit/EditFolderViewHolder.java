package com.reactiveandroid.sample.ui.adapters.foldersedit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.TextView;

import com.reactiveandroid.sample.R;
import com.reactiveandroid.sample.models.Folder;
import com.reactiveandroid.sample.utils.AppUtils;

public class EditFolderViewHolder extends RecyclerView.ViewHolder implements OpenCloseable {

    private final FoldersEditAdapter adapter;
    private final ImageButton leftButton;
    private final TextView folderName;
    private final ImageButton rightButton;
    private final TextInputLayout folderNameInputLayout;
    private final View focusHolder;

    private int position;

    public EditFolderViewHolder(FoldersEditAdapter adapter, View itemView) {
        super(itemView);

        this.adapter = adapter;
        this.leftButton = itemView.findViewById(R.id.left_button);
        this.folderName = itemView.findViewById(R.id.folder_name_text);
        this.rightButton = itemView.findViewById(R.id.right_button);
        this.folderNameInputLayout = itemView.findViewById(R.id.folder_name_input_layout);
        this.focusHolder = itemView.findViewById(R.id.focus_holder);

        folderName.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                open();
            }
        });
        folderName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                apply();
                close();
                return true;
            }
            return false;
        });

        leftButton.setOnClickListener(view -> onLeftButtonClick());
        rightButton.setOnClickListener(view -> onRightButtonClick());
    }

    public void onBindViewHolder(Folder folder, int position) {
        this.position = position;
        folderName.setText(folder.getName());
    }

    @Override
    public void open() {
        leftButton.setImageResource(R.drawable.ic_delete_grey_24dp);
        rightButton.setImageResource(R.drawable.ic_done_white_24dp);
        itemView.setBackgroundResource(R.color.md_white_1000);
        if (adapter.getLastOpened() != null) {
            adapter.getLastOpened().close();
        }
        adapter.setLastOpened(this);
    }

    @Override
    public boolean isOpen() {
        return folderName.hasFocus();
    }

    @Override
    public void close() {
        focusHolder.requestFocus();
        AppUtils.hideSoftKeyboard(folderName);
        leftButton.setImageResource(R.drawable.ic_folder_grey_24dp);
        folderName.setText(folderName.getText());
        rightButton.setImageResource(R.drawable.ic_mode_edit_grey_24dp);
        itemView.setBackgroundResource(0);
        if (adapter.getLastOpened() == this) {
            adapter.setLastOpened(null);
        } ;
    }

    private void delete() {
        new AlertDialog.Builder(itemView.getContext(), R.style.DialogTheme)
                .setCancelable(true)
                .setTitle("Delete folder?")
                .setMessage("Folder '" + folderName.getText() + "' will be deleted however notes in this folder will remain safe")
                .setPositiveButton("Delete Folder", (dialog, which) -> {
                    dialog.dismiss();
                    adapter.onFolderDelete(position);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void onLeftButtonClick() {
        if (isOpen()) {
            close();
            delete();
        }
    }

    private void onRightButtonClick() {
        if (isOpen()) {
            apply();
            close();
        } else {
            folderName.requestFocus();
            AppUtils.showSoftKeyboard(folderName);
        }
    }

    private void apply() {
        if (TextUtils.isEmpty(folderName.getText())) {
            folderNameInputLayout.setError(itemView.getContext().getString(R.string.enter_folder_name));
            return;
        }
        adapter.onFolderUpdate(folderName.getText().toString(), position);
    }

}