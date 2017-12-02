package com.reactiveandroid.internal.serializer;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.sql.Date;

public final class SqlDateSerializer extends TypeSerializer<Date, Long> {

    @Nullable
    public Long serialize(@NonNull Date data) {
        return data.getTime();
    }

    @Nullable
    public Date deserialize(@NonNull Long data) {
        return new Date(data);
    }

}