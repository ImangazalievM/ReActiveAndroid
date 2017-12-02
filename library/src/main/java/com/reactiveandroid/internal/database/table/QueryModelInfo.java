package com.reactiveandroid.internal.database.table;

import android.database.DatabaseUtils;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.reactiveandroid.annotation.Column;
import com.reactiveandroid.annotation.QueryColumn;
import com.reactiveandroid.annotation.QueryModel;
import com.reactiveandroid.internal.serializer.TypeSerializer;
import com.reactiveandroid.internal.utils.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Contains information about table
 */
public final class QueryModelInfo {

    private Class<?> databaseClass;
    private Class<?> modelClass;
    private List<Field> modelFields = new ArrayList<>();
    private Map<Field, ColumnInfo> columns = new LinkedHashMap<>();

    public QueryModelInfo(Class<?> modelClass,
                          Map<Class<?>, TypeSerializer> typeSerializers) {
        QueryModel tableAnnotation = modelClass.getAnnotation(QueryModel.class);

        this.modelClass = modelClass;
        this.databaseClass = tableAnnotation.database();

        List<Field> fields = filterQueryColumnFields(ReflectionUtils.getDeclaredFields(modelClass));
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
        QueryColumn columnAnnotation = field.getAnnotation(QueryColumn.class);
        String columnName = !TextUtils.isEmpty(columnAnnotation.name()) ? columnAnnotation.name() : field.getName();
        SQLiteType sqliteType = getFieldSQLiteType(field, typeSerializers);
        return new ColumnInfo(columnName, sqliteType, false);
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

    private List<Field> filterQueryColumnFields(List<Field> modelDeclaredFields) {
        List<Field> modelColumnFields = new ArrayList<>();
        for (Field field : modelDeclaredFields) {
            if (field.isAnnotationPresent(QueryColumn.class)) {
                modelColumnFields.add(field);
            }
        }
        return modelColumnFields;
    }

}
