package com.reactiveandroid.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;

import com.reactiveandroid.ReActiveAndroid;
import com.reactiveandroid.TableManager;
import com.reactiveandroid.annotation.Column;
import com.reactiveandroid.database.table.TableInfo;
import com.reactiveandroid.internal.log.LogLevel;
import com.reactiveandroid.internal.log.ReActiveLog;
import com.reactiveandroid.serializer.TypeSerializer;

import java.lang.reflect.Field;
import java.util.List;


public class ContentUtils {

    public static final String BASE_CONTENT_URI = "content://";

    public static Uri createUri(String authority, Class<?> type, Long id) {
        Uri.Builder builder = Uri.parse(BASE_CONTENT_URI + authority).buildUpon();
        builder.appendPath(ReActiveAndroid.getTableName(type).toLowerCase());

        if (id != null) {
            builder.appendPath(id.toString());
        }
        return builder.build();
    }

    public static <TableClass> int bulkInsert(ContentResolver contentResolver,
                                                            Uri bulkInsertUri,
                                                            Class<TableClass> table,
                                                            List<TableClass> models) {

        TableInfo tableInfo = ReActiveAndroid.getTableInfo(table);
        ContentValues[] contentValues = new ContentValues[models == null ? 0 : models.size()];

        if (models != null) {
            for (int i = 0; i < contentValues.length; i++) {
                contentValues[i] = getContentValues(models.get(i), tableInfo);
            }
        }

        return contentResolver.bulkInsert(bulkInsertUri, contentValues);
    }

    public static <TableClass> ContentValues getContentValues(TableClass model, 
                                                                            TableInfo tableInfo) {
        ContentValues contentValues = new ContentValues();
        for (Field field : tableInfo.getFields()) {
            String fieldName = tableInfo.getColumnInfo(field).name;
            Class<?> fieldType = field.getType();

            if (!field.isAccessible()) {
                field.setAccessible(true);
            }

            try {
                Object value = field.get(model);
                Column column = field.getAnnotation(Column.class);

                if (value != null) {
                    TypeSerializer typeSerializer = ReActiveAndroid.getSerializerForType(tableInfo.getModelClass(), fieldType);
                    if (typeSerializer != null) {
                        // serialize data
                        value = typeSerializer.serialize(value);
                        // set new object type
                        if (value != null) {
                            fieldType = value.getClass();
                            // check that the serializer returned what it promised
                            if (!fieldType.equals(typeSerializer.getSerializedType())) {
                                ReActiveLog.w(LogLevel.BASIC,
                                        String.format("TypeSerializer returned wrong type: expected a %s but got a %s",
                                                typeSerializer.getSerializedType(), fieldType));
                            }
                        }
                    }
                }


                // TODO: Find a smarter way to do this? This if block is necessary because we
                // can't know the type until runtime.
                if (value == null) {
                    if (column != null) {
                        //Not putting anything in ContentValues since we have default value
                        continue;
                    }

                    contentValues.putNull(fieldName);
                } else if (fieldType.equals(Byte.class) || fieldType.equals(byte.class)) {
                    contentValues.put(fieldName, (Byte) value);
                } else if (fieldType.equals(Short.class) || fieldType.equals(short.class)) {
                    contentValues.put(fieldName, (Short) value);
                } else if (fieldType.equals(Integer.class) || fieldType.equals(int.class)) {
                    contentValues.put(fieldName, (Integer) value);
                } else if (fieldType.equals(Long.class) || fieldType.equals(long.class)) {
                    contentValues.put(fieldName, (Long) value);
                } else if (fieldType.equals(Float.class) || fieldType.equals(float.class)) {
                    contentValues.put(fieldName, (Float) value);
                } else if (fieldType.equals(Double.class) || fieldType.equals(double.class)) {
                    contentValues.put(fieldName, (Double) value);
                } else if (fieldType.equals(Boolean.class) || fieldType.equals(boolean.class)) {
                    contentValues.put(fieldName, (Boolean) value);
                } else if (fieldType.equals(Character.class) || fieldType.equals(char.class)) {
                    contentValues.put(fieldName, value.toString());
                } else if (fieldType.equals(String.class)) {
                    contentValues.put(fieldName, value.toString());
                } else if (fieldType.equals(Byte[].class) || fieldType.equals(byte[].class)) {
                    contentValues.put(fieldName, (byte[]) value);
                } else if (ReflectionUtils.isModel(fieldType)) {
                    TableManager tableManager = ReActiveAndroid.getTableManager(fieldType);
                    contentValues.put(fieldName, tableManager.getModelId(value));
                }

            } catch (IllegalArgumentException | IllegalAccessException e) {
                ReActiveLog.e(LogLevel.BASIC, e.getClass().getName(), e);
            }
        }
        return contentValues;
    }


}
