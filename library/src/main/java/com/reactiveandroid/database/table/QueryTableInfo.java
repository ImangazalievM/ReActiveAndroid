package com.reactiveandroid.database.table;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.reactiveandroid.annotation.Column;
import com.reactiveandroid.annotation.QueryModel;
import com.reactiveandroid.serializer.TypeSerializer;
import com.reactiveandroid.utils.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Contains information about table
 */
public final class QueryTableInfo {

    private Class<?> databaseClass;
    private Class<?> modelClass;
    private List<Field> modelFields = new ArrayList<>();
    private Map<Field, ColumnInfo> columns = new LinkedHashMap<>();

    public QueryTableInfo(Class<?> modelClass,
                          Map<Class<?>, TypeSerializer> typeSerializers) {
        QueryModel tableAnnotation = modelClass.getAnnotation(QueryModel.class);

        this.modelClass = modelClass;
        this.databaseClass = tableAnnotation.database();

        List<Field> fields = new LinkedList<>(ReflectionUtils.getDeclaredColumnFields(modelClass));
        Collections.reverse(fields);
        for (Field field : fields) {
            ColumnInfo columnInfo = createColumnInfo(field, typeSerializers);
            modelFields.add(field);
            columns.put(field, columnInfo);
        }
    }

    @NonNull
    public Class<?> getDatabaseClass() {
        return databaseClass;
    }

    @NonNull
    public Class<?> getModelClass() {
        return modelClass;
    }

    @NonNull
    public List<Field> getFields() {
        return modelFields;
    }

    @NonNull
    public ColumnInfo getColumnInfo(Field field) {
        return columns.get(field);
    }

    private ColumnInfo createColumnInfo(Field field, Map<Class<?>, TypeSerializer> typeSerializers) {
        Column columnAnnotation = field.getAnnotation(Column.class);
        String columnName = TextUtils.isEmpty(columnAnnotation.name()) ? field.getName() : columnAnnotation.name();
        SQLiteType sqliteType = getFieldSQLiteType(field, typeSerializers);
        boolean notNull = columnAnnotation.notNull();
        return new ColumnInfo(columnName, sqliteType, notNull);
    }

    private SQLiteType getFieldSQLiteType(Field field, Map<Class<?>, TypeSerializer> typeSerializers) {
        SQLiteType sqliteType = null;
        Class<?> fieldType = field.getType();

        TypeSerializer typeSerializer = typeSerializers.get(field.getType());
        if (typeSerializer != null) {
            fieldType = typeSerializer.getSerializedType();
        }

        if (SQLiteType.containsType(fieldType)) {
            sqliteType = SQLiteType.getSQLiteTypeForClass(fieldType);
        } else if (ReflectionUtils.isModel(fieldType)) {
            sqliteType = SQLiteType.INTEGER;
        }

        return sqliteType;
    }

}
