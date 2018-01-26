package com.reactiveandroid.internal;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.reactiveandroid.annotation.Column;
import com.reactiveandroid.annotation.OneToMany;
import com.reactiveandroid.annotation.PrimaryKey;
import com.reactiveandroid.internal.cache.ModelCache;
import com.reactiveandroid.ReActiveAndroid;
import com.reactiveandroid.internal.cache.ModelLruCache;
import com.reactiveandroid.internal.serializer.TypeSerializer;
import com.reactiveandroid.internal.database.table.ColumnInfo;
import com.reactiveandroid.internal.database.table.TableInfo;
import com.reactiveandroid.internal.log.LogLevel;
import com.reactiveandroid.internal.log.ReActiveLog;
import com.reactiveandroid.internal.notifications.ChangeAction;
import com.reactiveandroid.internal.notifications.ModelChangeNotifier;
import com.reactiveandroid.internal.utils.SQLiteUtils;
import com.reactiveandroid.query.Delete;
import com.reactiveandroid.query.Select;
import com.reactiveandroid.internal.utils.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModelAdapter<ModelClass> {

    public static final int INSERT_FAILED = -1;

    private TableInfo tableInfo;
    private ModelCache<ModelClass> modelCache;
    private SQLiteDatabase sqliteDatabase;

    public ModelAdapter(@NonNull TableInfo tableInfo) {
        this.tableInfo = tableInfo;
        this.modelCache = new ModelLruCache<>(tableInfo.getCacheSize());
    }

    @NonNull
    public TableInfo getTableInfo() {
        return tableInfo;
    }

    @NonNull
    public ModelCache<ModelClass> getModelCache() {
        return modelCache;
    }

    @Nullable
    public ModelClass load(@NonNull Class<ModelClass> type, long id) {
        return Select.from(type).where(tableInfo.getPrimaryKeyColumnName() + "=?", id).fetchSingle();
    }

    @NonNull
    public Long save(@NonNull ModelClass model) {
        Long id = getModelId(model);

        if (isModelExists(id)) {
            update(model, id);
        } else {
            id = insert(model, id);
        }

        ModelChangeNotifier.get().notifyModelChanged(model, ChangeAction.SAVE);
        return id;
    }

    public void delete(@NonNull ModelClass model) {
        Long id = getModelId(model);
        if (isModelExists(id)) {
            modelCache.removeModel(id);
            getDatabase().delete(tableInfo.getTableName(), tableInfo.getPrimaryKeyColumnName() + "=?", new String[]{id.toString()});
            ModelChangeNotifier.get().notifyModelChanged(model, ChangeAction.DELETE);
            ModelChangeNotifier.get().notifyTableChanged(tableInfo.getModelClass(), ChangeAction.DELETE);
        }
        setModelId(model, 0L);
    }

    public void loadFromCursor(@NonNull ModelClass model, @NonNull Cursor cursor) {
        List<String> columnsOrdered = new ArrayList<>(Arrays.asList(cursor.getColumnNames()));
        long modelId = cursor.getLong(cursor.getColumnIndex(tableInfo.getPrimaryKeyColumnName()));
        for (Field field : tableInfo.getFields()) {
            field.setAccessible(true);
            Class<?> fieldType = field.getType();

            Object value = null;
            if (field.isAnnotationPresent(Column.class)) {
                ColumnInfo columnInfo = tableInfo.getColumnInfo(field);
                String columnName = columnInfo.name;
                int columnIndex = columnsOrdered.indexOf(columnName);
                if (cursor.isNull(columnIndex)) {
                    continue;
                }

                if (ReflectionUtils.isModel(fieldType)) {
                    value = getModelFieldValue(fieldType, cursor, columnIndex);
                } else {
                    value = getColumnFieldValue(fieldType, cursor, columnIndex);
                }
            } else if (field.isAnnotationPresent(PrimaryKey.class)) {
                value = cursor.getLong(columnsOrdered.indexOf(tableInfo.getPrimaryKeyColumnName()));
            } else if (field.isAnnotationPresent(OneToMany.class)) {
                value = getOneToManyFieldValue(field, fieldType, modelId);
            }

            try {
                if (value != null) {
                    field.set(model, value);
                }
            } catch (IllegalArgumentException | IllegalAccessException | SecurityException e) {
                ReActiveLog.e(LogLevel.BASIC, e.getClass().getName(), e);
            }
        }
    }

    @Nullable
    public Long getModelId(@NonNull ModelClass model) {
        try {
            return (Long) tableInfo.getPrimaryKeyField().get(model);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @NonNull
    private Long insert(@NonNull ModelClass model, Long id) {
        ContentValues values = new ContentValues();
        if (isIdIncremented(id)) {
            SQLiteUtils.fillContentValuesForUpdate(model, this, values);
        } else {
            SQLiteUtils.fillContentValuesForInsert(model, this, values);
        }

        id = getDatabase().insert(tableInfo.getTableName(), null, values);
        if (id > INSERT_FAILED) {
            setModelId(model, id);
            ModelChangeNotifier.get().notifyModelChanged(model, ChangeAction.INSERT);
        }
        return id;
    }

    private void update(@NonNull ModelClass model, Long id) {
        ContentValues values = new ContentValues();
        SQLiteUtils.fillContentValuesForUpdate(model, this, values);
        getDatabase().update(tableInfo.getTableName(), values, tableInfo.getPrimaryKeyColumnName() + "=" + id, null);
        ModelChangeNotifier.get().notifyModelChanged(model, ChangeAction.UPDATE);
    }

    private boolean isModelExists(Long id) {
        return isIdIncremented(id) && Select.from(tableInfo.getModelClass())
                .where(tableInfo.getPrimaryKeyColumnName() + "=?", id)
                .count() > 0;
    }

    private boolean isIdIncremented(Long id) {
        return id != null && id > 0;
    }

    private Object getModelFieldValue(Class<?> fieldType, Cursor cursor, int columnIndex) {
        long entityId = cursor.getLong(columnIndex);
        Object entity = tableInfo.isCachingEnabled() ? modelCache.get(entityId) : null;
        if (entity == null) {
            String foreignKeyIdName = ReActiveAndroid.getTableInfo(fieldType).getPrimaryKeyColumnName();
            entity = Select.from(fieldType).where(foreignKeyIdName + "=?", entityId).fetchSingle();
        }
        return entity;
    }

    private Object getColumnFieldValue(Class<?> fieldType, Cursor cursor, int columnIndex) {
        TypeSerializer typeSerializer = ReActiveAndroid.getSerializerForType(tableInfo.getModelClass(), fieldType);
        if (typeSerializer != null) {
            fieldType = typeSerializer.getSerializedType();
        }

        Object value = null;
        if (fieldType.equals(Byte.class) || fieldType.equals(byte.class)) {
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
        }

        if (typeSerializer != null && value != null) {
            value = typeSerializer.deserialize(value);
        }

        return value;
    }

    private Object getOneToManyFieldValue(Field field, Class<?> fieldType, long modelId) {
        if (!fieldType.equals(List.class)) {
            throw new IllegalArgumentException("Field declared as OneToMany must be List type");
        }

        ParameterizedType genericListType = (ParameterizedType) field.getGenericType();
        Class<?> genericListClass = (Class<?>) genericListType.getActualTypeArguments()[0];
        String foreignColumnName = field.getAnnotation(OneToMany.class).foreignColumnName();
        return Select.from(genericListClass).where(foreignColumnName + "=?", modelId).fetch();
    }

    private void setModelId(@NonNull ModelClass model, @Nullable Long id) {
        try {
            tableInfo.getPrimaryKeyField().set(model, id);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private SQLiteDatabase getDatabase() {
        if (sqliteDatabase == null) {
            sqliteDatabase = ReActiveAndroid.getWritableDatabaseForTable(tableInfo.getModelClass());
        }
        return sqliteDatabase;
    }

}
