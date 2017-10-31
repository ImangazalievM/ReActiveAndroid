package com.reactiveandroid.internal.serializer;

import java.sql.Date;

public final class SqlDateSerializer extends TypeSerializer<Date, Long> {

    public Long serialize(Date data) {
        if (data == null) {
            return null;
        }

        return data.getTime();
    }

    public Date deserialize(Long data) {
        if (data == null) {
            return null;
        }

        return new Date(data);
    }

}