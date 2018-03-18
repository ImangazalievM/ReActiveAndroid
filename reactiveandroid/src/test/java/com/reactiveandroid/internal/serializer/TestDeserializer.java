package com.reactiveandroid.internal.serializer;

import android.support.annotation.Nullable;

import java.util.Date;

class TestDeserializer extends TypeSerializer<Date, Long> {

    @Nullable
    @Override
    public Long serialize(@Nullable Date data) {
        return null;
    }

    @Nullable
    @Override
    public Date deserialize(@Nullable Long data) {
        return null;
    }

}
