package com.reactiveandroid.internal.serializer.integrationtest;

import android.support.annotation.Nullable;

import com.reactiveandroid.internal.serializer.Blob;
import com.reactiveandroid.internal.serializer.TypeSerializer;

public class CustomTypeSerializer extends TypeSerializer<ModelWithCustomField.MyCustomType, Blob> {

    @Nullable
    @Override
    public Blob serialize(@Nullable ModelWithCustomField.MyCustomType data) {
        return new Blob(data.toString().getBytes());
    }

    @Nullable
    @Override
    public ModelWithCustomField.MyCustomType deserialize(@Nullable Blob data) {
        return ModelWithCustomField.MyCustomType.getByName(new String(data.getBlob()));
    }

}
