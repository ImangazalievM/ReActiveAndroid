package com.reactiveandroid.internal.serializer;

import java.util.UUID;

public final class UUIDSerializer extends TypeSerializer<UUID, String> {

    public String serialize(UUID data) {
        if (data == null) {
            return null;
        }

        return data.toString();
    }

    public UUID deserialize(String data) {
        if (data == null) {
            return null;
        }

        return UUID.fromString(data);
    }

}