package com.reactiveandroid.internal.serializer;

import android.support.annotation.Nullable;

import java.util.UUID;

public final class UUIDSerializer extends TypeSerializer<UUID, String> {

    @Nullable
    public String serialize(@Nullable UUID data) {
        if (data == null) return null;
        return data.toString();
    }

    @Nullable
    public UUID deserialize(@Nullable String data) {
        if (data == null) return null;
        return UUID.fromString(data);
    }

}