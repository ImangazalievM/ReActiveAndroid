package com.reactiveandroid.internal.database.table;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.SparseArray;

import com.reactiveandroid.annotation.Column;
import com.reactiveandroid.annotation.Index;
import com.reactiveandroid.annotation.IndexGroup;
import com.reactiveandroid.annotation.PrimaryKey;
import com.reactiveandroid.annotation.Table;
import com.reactiveandroid.annotation.Unique;
import com.reactiveandroid.annotation.UniqueGroup;
import com.reactiveandroid.internal.serializer.TypeSerializer;
import com.reactiveandroid.internal.utils.ReflectionUtils;
import com.reactiveandroid.internal.utils.SQLiteUtils;

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
public final class TableInfo {

    private Class<?> databaseClass;
    private Class<?> modelClass;
    private String tableName;
    private List<Field> modelFields;
    private List<Field> modelColumnFields = new ArrayList<>();
    private Field primaryKeyField;
    private String primaryKeyColumnName;

    private Map<Field, ColumnInfo> columns = new LinkedHashMap<>();
    private SparseArray<IndexGroupInfo> indexGroups = new SparseArray<>();
    private SparseArray<UniqueGroupInfo> uniqueGroups = new SparseArray<>();

    private boolean cachingEnabled;
    private int cacheSize;
    private boolean createWithDatabase;


    public TableInfo(Class<?> modelClass,
                     Map<Class<?>, TypeSerializer> typeSerializers) {
        Table tableAnnotation = modelClass.getAnnotation(Table.class);
        this.databaseClass = tableAnnotation.database();
        this.modelClass = modelClass;
        this.tableName = tableAnnotation.name().isEmpty() ? modelClass.getSimpleName() : tableAnnotation.name();
        this.cachingEnabled = tableAnnotation.cachingEnabled();
        this.cacheSize = tableAnnotation.cacheSize();
        this.createWithDatabase = tableAnnotation.createWithDatabase();
        createUniqueGroups(tableAnnotation);
        createIndexGroups(tableAnnotation);

        modelFields = ReflectionUtils.getDeclaredFields(modelClass);
        modelColumnFields.addAll(filterAndSortColumnFields(modelFields));
        Collections.reverse(modelColumnFields);
        for (Field field : modelColumnFields) {
            ColumnInfo columnInfo = createColumnInfo(field, typeSerializers);
            createColumnUnique(field, columnInfo);
            createColumnIndex(field, columnInfo);
            modelFields.add(field);
            columns.put(field, columnInfo);
        }

        // Manually addColumn the primary key column since it is not declared like the other columns.
        primaryKeyField = findPrimaryKeyField(modelClass);
        columns.put(primaryKeyField, new ColumnInfo(primaryKeyColumnName, SQLiteType.INTEGER, true));
        modelColumnFields.add(0, primaryKeyField);
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
    public List<Field> getColumnFields() {
        return modelColumnFields;
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

    public boolean createWithDatabase() {
        return createWithDatabase;
    }

    private List<Field> filterAndSortColumnFields(List<Field> modelFields) {
        List<Field> modelColumnFields = new ArrayList<>();
        for (Field field : modelFields) {
            if (field.isAnnotationPresent(Column.class)) {
                modelColumnFields.add(field);
            }
        }
        Collections.sort(modelColumnFields, new Comparator<Field>() {
            @Override
            public int compare(Field field1, Field field2) {
                return field2.getName().compareTo(field1.getName());
            }
        });
        return modelColumnFields;
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
                Class<?> fieldType = field.getType();
                if (!(fieldType.equals(long.class) || fieldType.equals(Long.class))) {
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

    private ColumnInfo createColumnInfo(Field field, Map<Class<?>, TypeSerializer> typeSerializers) {
        Column columnAnnotation = field.getAnnotation(Column.class);
        String columnName = !TextUtils.isEmpty(columnAnnotation.name()) ? columnAnnotation.name() : field.getName();
        SQLiteType sqliteType = SQLiteUtils.getFieldSQLiteType(field, typeSerializers);
        boolean notNull = columnAnnotation.notNull();
        return new ColumnInfo(columnName, sqliteType, notNull);
    }

    private void createColumnUnique(Field field, ColumnInfo columnInfo) {
        Unique uniqueAnnotation = field.getAnnotation(Unique.class);
        if (uniqueAnnotation == null) {
            return;
        }

        int[] groups = uniqueAnnotation.uniqueGroups();
        for (int group : groups) {
            UniqueGroupInfo uniqueGroupInfo = uniqueGroups.get(group);
            if (uniqueGroupInfo != null) {
                uniqueGroupInfo.addColumn(columnInfo);
            } else {
                throw new IllegalArgumentException("Unique group with number " + group + " not found in class " + modelClass.getName());
            }
        }
    }

    private void createColumnIndex(Field field, ColumnInfo columnInfo) {
        Index indexAnnotation = field.getAnnotation(Index.class);
        if (indexAnnotation == null) {
            return;
        }

        int[] groups = indexAnnotation.indexGroups();
        for (int group : groups) {
            IndexGroupInfo indexGroupInfo = indexGroups.get(group);
            if (indexGroupInfo != null) {
                indexGroupInfo.addColumn(columnInfo);
            } else {
                throw new IllegalArgumentException("Index group with number " + group + " not found");
            }
        }
    }

}
