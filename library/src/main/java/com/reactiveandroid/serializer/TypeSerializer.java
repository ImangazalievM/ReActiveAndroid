package com.reactiveandroid.serializer;

import android.support.annotation.Nullable;

import java.lang.reflect.ParameterizedType;

public abstract class TypeSerializer<DeserializedType, SerializedType> {

    private Class<DeserializedType> deserializedTypeClass;
    private Class<SerializedType> serializedTypeClass;

    public TypeSerializer() {
        if (!(getClass().getGenericSuperclass() instanceof ParameterizedType)) {
            throw new IllegalArgumentException("Please specify deserialized and serialized types via generic");
        }

        ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
        this.deserializedTypeClass = (Class<DeserializedType>) parameterizedType.getActualTypeArguments()[0];
        this.serializedTypeClass = (Class<SerializedType>) parameterizedType.getActualTypeArguments()[1];
    }

    public Class<DeserializedType> getDeserializedType() {
        return deserializedTypeClass;
    }

    public Class<SerializedType> getSerializedType() {
        return serializedTypeClass;
    }

    @Nullable
    public abstract SerializedType serialize(DeserializedType data);

    @Nullable
    public abstract DeserializedType deserialize(SerializedType data);

}