package com.reactiveandroid.internal.serializer;

import android.support.annotation.Nullable;

import java.io.File;

public final class FileSerializer extends TypeSerializer<File, String> {

    @Nullable
    public String serialize(@Nullable File data) {
        if (data == null) return null;
        return data.toString();
    }

    @Nullable
    public File deserialize(@Nullable String data) {
        if (data == null) return null;
        return new File(data);
    }

}
