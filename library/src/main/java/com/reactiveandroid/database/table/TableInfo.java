package com.reactiveandroid.database.table;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.SparseArray;

import com.reactiveandroid.annotation.Column;
import com.reactiveandroid.annotation.IndexGroup;
import com.reactiveandroid.annotation.PrimaryKey;
import com.reactiveandroid.annotation.Table;
import com.reactiveandroid.annotation.UniqueGroup;
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
public final class TableInfo {

    private Class<?> databaseClass;
    private Class<?> tableClass;
    private String tableName;
    private List<Field> modelFields = new ArrayList<>();
    private Field primaryKeyField;
    private String primaryKeyColumnName;

    private Map<Field, ColumnInfo> columns = new LinkedHashMap<>();
    private SparseArray<IndexGroupInfo> indexGroups = new SparseArray<>();
    private SparseArray<UniqueGroupInfo> uniqueGroups = new SparseArray<>();

    private boolean cachingEnabled;
    private int cacheSize;


    public TableInfo(Class<?> tableClass,
                     Map<Class<?>, TypeSerializer> typeSerializers) {
        Table tableAnnotation = tableClass.getAnnotation(Table.class);

        this.tableClass = tableClass;
        this.databaseClass = tableAnnotation.database();
        this.tableName = tableAnnotation.name().isEmpty() ? tableClass.getSimpleName() : tableAnnotation.name();
        this.cachingEnabled = tableAnnotation.cachingEnabled();
        this.cacheSize = tableAnnotation.cacheSize();
        createUniqueGroups(tableAnnotation);
        createIndexGroups(tableAnnotation);

        // Manually addColumn the id column since it is not declared like the other columns.
        primaryKeyField = findPrimaryKeyField(tableClass);
        columns.put(primaryKeyField, new ColumnInfo(primaryKeyColumnName, SQLiteType.INTEGER, true));
        modelFields.add(primaryKeyField);

        List<Field> modelFields = new LinkedList<>(ReflectionUtils.getDeclaredColumnFields(tableClass));
        Collections.reverse(modelFields);
        for (Field field : modelFields) {
            Column columnAnnotation = field.getAnnotation(Column.class);
            ColumnInfo columnInfo = createColumnInfo(field, columnAnnotation, typeSerializers);
            createColumnUnique(columnInfo, columnAnnotation);
            createColumnIndex(columnInfo, columnAnnotation);
            this.modelFields.add(field);
            columns.put(field, columnInfo);
        }
    }

    @NonNull
    public Class<?> getDatabaseClass() {
        return databaseClass;
    }

    @NonNull
    public Class<?> getTableClass() {
        return tableClass;
    }

    @NonNull
    public String getTableName() {
        return tableName;
    }

    @NonNull
    public String getPrimaryKeyColumnName() {
        return primaryKeyColumnName;
    }

    @NonNull
    public List<Field> getFields() {
        return modelFields;
    }

    @NonNull
    public Field getPrimaryKeyField() {
        return primaryKeyField;
    }

    @NonNull
    public ColumnInfo getColumnInfo(Field field) {
        return columns.get(field);
    }

    @NonNull
    public SparseArray<IndexGroupInfo> getIndexGroups() {
        return indexGroups;
    }

    @NonNull
    public SparseArray<UniqueGroupInfo> getUniqueGroups() {
        return uniqueGroups;
    }

    public boolean isCachingEnabled() {
        return cachingEnabled;
    }

    public int getCacheSize() {
        return cacheSize;
    }

    private void createUniqueGroups(Table tableAnnotation) {
        UniqueGroup[] uniqueGroupAnnotations = tableAnnotation.uniqueGroups();
        for (UniqueGroup uniqueGroup : uniqueGroupAnnotations) {
            UniqueGroupInfo uniqueGroupInfo = new UniqueGroupInfo(uniqueGroup.onUniqueConflict());
            uniqueGroups.put(uniqueGroup.groupNumber(), uniqueGroupInfo);
        }
    }

    private void createIndexGroups(Table tableAnnotation) {
        IndexGroup[] indexGroupAnnotations = tableAnnotation.indexGroups();
        for (IndexGroup indexGroup : indexGroupAnnotations) {
            IndexGroupInfo indexGroupInfo = new IndexGroupInfo(indexGroup.name(), indexGroup.unique());
            indexGroups.put(indexGroup.groupNumber(), indexGroupInfo);
        }
    }

    private Field findPrimaryKeyField(Class<?> modelClass) {
        for (Field field : modelClass.getDeclaredFields()) {
            PrimaryKey primaryKeyAnnotation = field.getAnnotation(PrimaryKey.class);
            if (primaryKeyAnnotation != null) {
                if (!(field.getType().equals(Long.class))) {
                    throw new IllegalArgumentException("Primary key field should be Long type");
                }

                if (primaryKeyField == null) {
                    this.primaryKeyField = field;
                    this.primaryKeyColumnName = primaryKeyAnnotation.name();
                } else {
                    throw new IllegalStateException(modelClass.getSimpleName() + " contains more than one primary key. " +
                            "ReActiveAndroid does not support composite primary key");
                }
            }
        }

        if (primaryKeyField != null) {
            primaryKeyField.setAccessible(true);
        } else {
            throw new IllegalStateException("Primary key field not found for model " + modelClass.getSimpleName());
        }
        return primaryKeyField;
    }

    private ColumnInfo createColumnInfo(Field field, Column columnAnnotation, Map<Class<?>, TypeSerializer> typeSerializers) {
        String columnName = TextUtils.isEmpty(columnAnnotation.name()) ? field.getName() : columnAnnotation.name();
        SQLiteType sqliteType = getFieldSQLiteType(field, typeSerializers);
        boolean notNull = columnAnnotation.notNull();
        return new ColumnInfo(columnName, sqliteType, notNull);
    }

    private void createColumnUnique(ColumnInfo columnInfo, Column columnAnnotation) {
        int[] groups = columnAnnotation.uniqueGroups();
        for (int group : groups) {
            UniqueGroupInfo uniqueGroupInfo = uniqueGroups.get(group);
            if (uniqueGroupInfo != null) {
                uniqueGroupInfo.addColumn(columnInfo);
            } else {
                throw new IllegalArgumentException("Unique group with number " + group + " not found in class " + tableClass.getName());
            }
        }
    }

    private void createColumnIndex(ColumnInfo columnInfo, Column columnAnnotation) {
        int[] groups = columnAnnotation.indexGroups();
        for (int group : groups) {
            IndexGroupInfo indexGroupInfo = indexGroups.get(group);
            if (indexGroupInfo != null) {
                indexGroupInfo.addColumn(columnInfo);
            } else {
                throw new IllegalArgumentException("Index group with number " + group + " not found");
            }
        }
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
