package com.reactiveandroid.internal.serializer;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.UUID;

public final class UUIDSerializer extends TypeSerializer<UUID, String> {

    @Nullable
    public String serialize(@NonNull UUID data) {
        return data.toString();
    }

    @Nullable
    public UUID deserialize(@NonNull String data) {
        return UUID.fromString(data);
    }

}