package com.reactiveandroid.internal.notifications;

import android.support.annotation.NonNull;

public interface OnModelChangedListener<ModelClass> {

    void onModelChanged(@NonNull ModelClass model, @NonNull ChangeAction action);

}
