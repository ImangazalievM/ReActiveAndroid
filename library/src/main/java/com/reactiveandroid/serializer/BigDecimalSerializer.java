package com.reactiveandroid.serializer;

import android.support.annotation.Nullable;

import java.math.BigDecimal;

public final class BigDecimalSerializer extends TypeSerializer<BigDecimal, String> {

    @Nullable
    public String serialize(BigDecimal data) {
        if (data == null) {
            return null;
        }

        return data.toString();
    }

    @Nullable
    public BigDecimal deserialize(String data) {
        if (data == null) {
            return null;
        }

        return new BigDecimal(data);
    }

}