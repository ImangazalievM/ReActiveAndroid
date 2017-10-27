package com.reactiveandroid.serializer;

import java.math.BigDecimal;

public final class BigDecimalSerializer extends TypeSerializer<BigDecimal, String> {

    public String serialize(BigDecimal data) {
        if (data == null) {
            return null;
        }

        return data.toString();
    }

    public BigDecimal deserialize(String data) {
        if (data == null) {
            return null;
        }

        return new BigDecimal(data);
    }

}