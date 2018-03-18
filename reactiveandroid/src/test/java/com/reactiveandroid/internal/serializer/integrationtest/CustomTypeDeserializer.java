package com.reactiveandroid.internal.serializer.integrationtest;

import android.support.annotation.Nullable;

import com.reactiveandroid.internal.serializer.TypeSerializer;

public class CustomTypeDeserializer extends TypeSerializer<ModelWithCustomField.MyCustomType, String> {

    @Nullable
    @Override
    public String serialize(@Nullable ModelWithCustomField.MyCustomType data) {
        return data.toString();
    }

    @Nullable
    @Override
    public ModelWithCustomField.MyCustomType deserialize(@Nullable String data) {
        return ModelWithCustomField.MyCustomType.getByName(data);
    }

}
