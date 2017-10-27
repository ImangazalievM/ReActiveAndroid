package com.reactiveandroid.serializer;

import java.io.File;

public final class FileSerializer extends TypeSerializer<File, String> {

    public String serialize(File data) {
        if (data == null) {
            return null;
        }

        return data.toString();
    }

    public File deserialize(String data) {
        if (data == null) {
            return null;
        }

        return new File(data);
    }

}
