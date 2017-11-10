package com.reactiveandroid.sample.ui.adapters.foldersedit;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.reactiveandroid.sample.R;
import com.reactiveandroid.sample.utils.AppUtils;

public class NewFolderViewHolder extends RecyclerView.ViewHolder implements OpenCloseable {

    private final FoldersEditAdapter adapter;
    private final ImageButton leftButton;
    private final TextView folderName;
    private final ImageButton doneButton;
    private final View focusHolder;

    public NewFolderViewHolder(FoldersEditAdapter adapter, View itemView) {
        super(itemView);

        this.adapter = adapter;
        this.leftButton = itemView.findViewById(R.id.left_button);
        this.folderName = itemView.findViewById(R.id.folder_name_text);
        this.doneButton = itemView.findViewById(R.id.done_button);
        this.focusHolder = itemView.findViewById(R.id.focus_holder);

        folderName.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                open();
            }
        });
        folderName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                onDoneButtonClick();
                return true;
            }
            return false;
        });

        leftButton.setOnClickListener(view -> onLeftButtonClick());
        doneButton.setOnClickListener(view -> onDoneButtonClick());
    }

    @Override
    public void open() {
        doneButton.setVisibility(View.VISIBLE);
        leftButton.setImageResource(R.drawable.ic_close_grey_24dp);
        leftButton.setAlpha(0.5f);
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
        doneButton.setVisibility(View.GONE);
        folderName.setText(null);
        leftButton.setAlpha(1f);
        leftButton.setImageResource(R.drawable.ic_add_grey_24dp);
        itemView.setBackgroundResource(0);
        if (adapter != null && adapter.getLastOpened() == this) {
            adapter.setLastOpened(null);
        }
    }

    private void onLeftButtonClick() {
        if (isOpen()) {
            close();
        } else {
            folderName.requestFocus();
            AppUtils.showSoftKeyboard(folderName);
        }
    }

    private void onDoneButtonClick() {
        if (TextUtils.isEmpty(folderName.getText())) {
            Toast.makeText(itemView.getContext(), R.string.enter_folder_name, Toast.LENGTH_SHORT).show();
            return;
        }

        adapter.onFolderCreate(folderName.getText().toString().trim());
        close();
    }

}