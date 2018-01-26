package com.reactiveandroid.internal.serializer;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;

public final class FileSerializer extends TypeSerializer<File, String> {

    @Nullable
    public String serialize(@NonNull File data) {
        return data.toString();
    }

    @Nullable
    public File deserialize(@NonNull String data) {
        return new File(data);
    }

}
