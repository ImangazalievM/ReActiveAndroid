package com.reactiveandroid.internal;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.reactiveandroid.internal.cache.ModelCache;
import com.reactiveandroid.ReActiveAndroid;
import com.reactiveandroid.internal.serializer.TypeSerializer;
import com.reactiveandroid.internal.database.table.ColumnInfo;
import com.reactiveandroid.internal.database.table.TableInfo;
import com.reactiveandroid.internal.log.LogLevel;
import com.reactiveandroid.internal.log.ReActiveLog;
import com.reactiveandroid.internal.notifications.ChangeAction;
import com.reactiveandroid.internal.notifications.ModelChangeNotifier;
import com.reactiveandroid.query.Delete;
import com.reactiveandroid.query.Select;
import com.reactiveandroid.internal.utils.ContentUtils;
import com.reactiveandroid.internal.utils.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModelAdapter<TableClass> {

    private TableInfo tableInfo;
    private ModelCache<TableClass> modelCache;
    private SQLiteDatabase sqLiteDatabase;

    public ModelAdapter(@NonNull TableInfo tableInfo, @NonNull ModelCache<TableClass> modelCache) {
        this.tableInfo = tableInfo;
        this.modelCache = modelCache;
    }

    @NonNull
    public TableInfo getTableInfo() {
        return tableInfo;
    }

    @NonNull
    public ModelCache<TableClass> getModelCache() {
        return modelCache;
    }

    @Nullable
    public TableClass load(@NonNull Class<TableClass> type, long id) {
        TableInfo tableInfo = ReActiveAndroid.getTableInfo(type);
        return Select.from(type).where(tableInfo.getPrimaryKeyColumnName() + "=?", id).fetchSingle();
    }

    @Nullable
    public Long save(@NonNull TableClass model) {
        SQLiteDatabase db = getDatabase();
        ContentValues values = ContentUtils.getContentValues(model, tableInfo);
        Long id = getModelId(model);
        if (id == null) {
            id = db.insert(tableInfo.getTableName(), null, values);
            setModelId(model, id);
            ModelChangeNotifier.get().notifyModelChanged(model, ChangeAction.INSERT);
            ModelChangeNotifier.get().notifyTableChanged(tableInfo.getModelClass(), ChangeAction.INSERT);
        } else {
            db.update(tableInfo.getTableName(), values, tableInfo.getPrimaryKeyColumnName() + "=" + id, null);
            ModelChangeNotifier.get().notifyModelChanged(model, ChangeAction.UPDATE);
            ModelChangeNotifier.get().notifyTableChanged(tableInfo.getModelClass(), ChangeAction.UPDATE);
        }
        return id;
    }

    public void saveAll(@NonNull Class<TableClass> table, @NonNull List<TableClass> models) {
        // skip if empty
        if (models.isEmpty()) {
            return;
        }

        SQLiteDatabase sqliteDatabase = ReActiveAndroid.getWritableDatabaseForTable(table);
        try {
            sqliteDatabase.beginTransaction();
            for (TableClass model : models) {
                save(model);
            }
            sqliteDatabase.setTransactionSuccessful();
            ContentUtils.bulkInsert(ReActiveAndroid.getContext().getContentResolver(), null, table, models);
        } finally {
            sqliteDatabase.endTransaction();
        }
    }

    public void delete(@NonNull TableClass model) {
        Long id = getModelId(model);
        modelCache.removeModel(id);
        getDatabase().delete(tableInfo.getTableName(), tableInfo.getPrimaryKeyColumnName() + "=?", new String[]{id.toString()});
        setModelId(model, null);
        ModelChangeNotifier.get().notifyModelChanged(model, ChangeAction.DELETE);
        ModelChangeNotifier.get().notifyTableChanged(tableInfo.getModelClass(), ChangeAction.DELETE);
    }

    public void delete(@NonNull Class<TableClass> type, long id) {
        TableInfo tableInfo = ReActiveAndroid.getTableInfo(type);
        Delete.from(type).where(tableInfo.getPrimaryKeyColumnName() + "=?", id).execute();
    }

    public void deleteAll(@NonNull Class<TableClass> type, @NonNull List<TableClass> models) {
        // skip if empty
        if (models.isEmpty()) {
            return;
        }

        Long[] ids = new Long[models.size()];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = getModelId(models.get(i));
        }

        TableInfo tableInfo = ReActiveAndroid.getTableInfo(type);
        String idsArg = TextUtils.join(", ", ids);
        ReActiveAndroid.getWritableDatabaseForTable(type).execSQL(String.format("DELETE FROM %s WHERE %s IN (%s);", tableInfo.getTableName(), tableInfo.getPrimaryKeyColumnName(), idsArg));
    }

    public void loadFromCursor(@NonNull TableClass model, @NonNull Cursor cursor) {

        List<String> columnsOrdered = new ArrayList<>(Arrays.asList(cursor.getColumnNames()));
        for (Field field : tableInfo.getFields()) {
            Class<?> fieldType = field.getType();
            ColumnInfo columnInfo = tableInfo.getColumnInfo(field);
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
                TypeSerializer typeSerializer = ReActiveAndroid.getSerializerForType(tableInfo.getModelClass(), fieldType);
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
                    Class<?> entityType = fieldType;
                    Object entity = tableInfo.isCachingEnabled() ? modelCache.get(entityId) : null;
                    if (entity == null) {
                        String foreignKeyIdName = ReActiveAndroid.getTableInfo(entityType).getPrimaryKeyColumnName();
                        entity = Select.from(entityType).where(foreignKeyIdName + "=?", entityId).fetchSingle();
                    }

                    value = entity;
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

    }

    private void setModelId(@NonNull TableClass model, @Nullable Long id) {
        try {
            tableInfo.getPrimaryKeyField().set(model, id);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    public Long getModelId(@NonNull TableClass model) {
        try {
            return (Long) tableInfo.getPrimaryKeyField().get(model);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private SQLiteDatabase getDatabase() {
        if (sqLiteDatabase == null) {
            sqLiteDatabase = ReActiveAndroid.getWritableDatabaseForTable(tableInfo.getModelClass());
        }
        return sqLiteDatabase;
    }

}
