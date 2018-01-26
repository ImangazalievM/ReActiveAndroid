package com.reactiveandroid.internal.serializer;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.math.BigDecimal;

public final class BigDecimalSerializer extends TypeSerializer<BigDecimal, String> {

    @Nullable
    public String serialize(@NonNull BigDecimal data) {
        return data.toString();
    }

    @Nullable
    public BigDecimal deserialize(@NonNull String data) {
        return new BigDecimal(data);
    }

}