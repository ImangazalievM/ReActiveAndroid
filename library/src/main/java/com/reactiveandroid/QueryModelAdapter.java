package com.reactiveandroid;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.reactiveandroid.database.table.ColumnInfo;
import com.reactiveandroid.database.table.QueryTableInfo;
import com.reactiveandroid.internal.log.LogLevel;
import com.reactiveandroid.internal.log.ReActiveLog;
import com.reactiveandroid.query.Select;
import com.reactiveandroid.serializer.TypeSerializer;
import com.reactiveandroid.utils.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QueryModelAdapter<QueryModelClass> {

    private QueryTableInfo queryTableInfo;

    public QueryModelAdapter(QueryTableInfo queryTableInfo) {
        this.queryTableInfo = queryTableInfo;
    }

    public QueryModelClass createFromCursor(Cursor cursor) {
        QueryModelClass model = newInstance();
        List<String> columnsOrdered = new ArrayList<>(Arrays.asList(cursor.getColumnNames()));
        for (Field field : queryTableInfo.getFields()) {
            Class<?> fieldType = field.getType();
            ColumnInfo columnInfo = queryTableInfo.getColumnInfo(field);
            String columnName = columnInfo.name;
            int columnIndex = columnsOrdered.indexOf(columnName);
            if (columnIndex < 0) {
                continue;
            }

            if (!field.isAccessible()) {
                field.setAccessible(true);
            }

            try {
                boolean columnIsNull = cursor.isNull(columnIndex);
                TypeSerializer typeSerializer = ReActiveAndroid.getSerializerForType(queryTableInfo.getModelClass(), fieldType);
                Object value = null;

                if (typeSerializer != null) {
                    fieldType = typeSerializer.getSerializedType();
                }

                if (columnIsNull) {
                    field = null;
                } else if (fieldType.equals(Byte.class) || fieldType.equals(byte.class)) {
                    value = cursor.getInt(columnIndex);
                } else if (fieldType.equals(Short.class) || fieldType.equals(short.class)) {
                    value = cursor.getInt(columnIndex);
                } else if (fieldType.equals(Integer.class) || fieldType.equals(int.class)) {
                    value = cursor.getInt(columnIndex);
                } else if (fieldType.equals(Long.class) || fieldType.equals(long.class)) {
                    value = cursor.getLong(columnIndex);
                } else if (fieldType.equals(Float.class) || fieldType.equals(float.class)) {
                    value = cursor.getFloat(columnIndex);
                } else if (fieldType.equals(Double.class) || fieldType.equals(double.class)) {
                    value = cursor.getDouble(columnIndex);
                } else if (fieldType.equals(Boolean.class) || fieldType.equals(boolean.class)) {
                    value = cursor.getInt(columnIndex) != 0;
                } else if (fieldType.equals(Character.class) || fieldType.equals(char.class)) {
                    value = cursor.getString(columnIndex).charAt(0);
                } else if (fieldType.equals(String.class)) {
                    value = cursor.getString(columnIndex);
                } else if (fieldType.equals(Byte[].class) || fieldType.equals(byte[].class)) {
                    value = cursor.getBlob(columnIndex);
                } else if (ReflectionUtils.isModel(fieldType)) {
                    long entityId = cursor.getLong(columnIndex);
                    String foreignKeyIdName = ReActiveAndroid.getTableInfo(fieldType).getPrimaryKeyColumnName();
                    value = Select.from(fieldType).where(foreignKeyIdName + "=?", entityId).fetchSingle();
                }

                // Use a deserializer if one is available
                if (typeSerializer != null && !columnIsNull) {
                    value = typeSerializer.deserialize(value);
                }

                // Set the field value
                if (value != null) {
                    field.set(model, value);
                }
            } catch (IllegalArgumentException | IllegalAccessException | SecurityException e) {
                ReActiveLog.e(LogLevel.BASIC, e.getClass().getName(), e);
            }
        }

        return model;
    }

    private QueryModelClass newInstance() {
        try {
            return (QueryModelClass) queryTableInfo.getModelClass().getConstructor().newInstance();
        } catch (Exception e) {
            throw new IllegalStateException(queryTableInfo.getModelClass().getName() + " should have constructor without arguments");
        }
    }

    @NonNull
    private SQLiteDatabase getDatabase() {
        return ReActiveAndroid.getWritableDatabaseForTable(queryTableInfo.getModelClass());
    }

}
