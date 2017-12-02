package com.reactiveandroid.internal.serializer;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.reactiveandroid.internal.database.table.SQLiteType;

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

        if (!SQLiteType.containsType(serializedTypeClass)) {
            throw new IllegalArgumentException("Serialized type should be one of those: Byte, Short, Integer, Long, Float, Double, Boolean, Character, String, Byte[]");
        }
    }

    @NonNull
    public Class<DeserializedType> getDeserializedType() {
        return deserializedTypeClass;
    }

    @NonNull
    public Class<SerializedType> getSerializedType() {
        return serializedTypeClass;
    }

    @Nullable
    public abstract SerializedType serialize(@NonNull DeserializedType data);

    @Nullable
    public abstract DeserializedType deserialize(@NonNull SerializedType data);

}