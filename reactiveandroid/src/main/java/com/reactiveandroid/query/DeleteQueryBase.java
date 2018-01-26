package com.reactiveandroid.query;

import android.support.annotation.NonNull;

import com.reactiveandroid.internal.notifications.ChangeAction;
import com.reactiveandroid.internal.utils.QueryUtils;

abstract class DeleteQueryBase<T> extends ExecutableQueryBase<T> {

    DeleteQueryBase(Query parent, Class<T> table) {
        super(parent, table);
    }

    @NonNull
    @Override
    ChangeAction getChangeAction() {
        return ChangeAction.DELETE;
    }


}
