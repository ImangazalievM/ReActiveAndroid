package com.reactiveandroid.internal.utils;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.reactiveandroid.ReActiveAndroid;
import com.reactiveandroid.internal.ModelAdapter;
import com.reactiveandroid.internal.QueryModelAdapter;
import com.reactiveandroid.internal.cache.ModelCache;
import com.reactiveandroid.internal.database.DatabaseInfo;
import com.reactiveandroid.internal.database.table.TableInfo;
import com.reactiveandroid.internal.log.LogLevel;
import com.reactiveandroid.internal.log.ReActiveLog;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class QueryUtils {

    public static void execSQL(Class<?> table, String sql) {
        ReActiveAndroid.getWritableDatabaseForTable(table).execSQL(sql);
    }

    public static void execSQL(Class<?> table, String sql, String[] selectionArgs) {
        ReActiveAndroid.getWritableDatabaseForTable(table).execSQL(sql, selectionArgs);
    }

    @NonNull
    public static Cursor rawQuery(Class<?> database, String sql, String[] selectionArgs) {
        return ReActiveAndroid.getDatabase(database).getWritableDatabase().rawQuery(sql, selectionArgs);
    }

    @NonNull
    public static Cursor rawQueryForTable(Class<?> table, String sql, String[] selectionArgs) {
        return ReActiveAndroid.getWritableDatabaseForTable(table).rawQuery(sql, selectionArgs);
    }

    @NonNull
    public static <TableClass> List<TableClass> fetchModels(Class<TableClass> table,
                                                            String sql, String[] selectionArgs,
                                                            boolean disableCacheForThisQuery) {
        Cursor cursor = rawQueryForTable(table, sql, selectionArgs);
        List<TableClass> entities = processCursor(table, cursor, disableCacheForThisQuery);
        cursor.close();
        return entities;
    }

    @NonNull
    public static <CustomClass> List<CustomClass> fetchQueryModels(Class<CustomClass> customType,
                                                                   String sql, String[] selectionArgs) {
        DatabaseInfo databaseInfo = ReActiveAndroid.getDatabaseForTable(customType);
        QueryModelAdapter<CustomClass> queryModelAdapter = databaseInfo.getQueryModelAdapter(customType);
        Cursor cursor = databaseInfo.getWritableDatabase().rawQuery(sql, selectionArgs);
        cursor.moveToFirst();
        List<CustomClass> entities = new ArrayList<>();
        do {
            entities.add(queryModelAdapter.createFromCursor(cursor));
        } while (cursor.moveToNext());
        cursor.close();
        return entities;
    }

    @NonNull
    private static <TableClass> List<TableClass> processCursor(Class<TableClass> table,
                                                               Cursor cursor,
                                                               boolean disableCacheForThisQuery) {
        ModelAdapter modelAdapter = ReActiveAndroid.getModelAdapter(table);
        TableInfo tableInfo = modelAdapter.getTableInfo();
        ModelCache modelCache = modelAdapter.getModelCache();
        String idName = tableInfo.getPrimaryKeyColumnName();
        List<TableClass> entities = new ArrayList<>();

        if (cursor.moveToFirst()) {
            int idColumnIndex = cursor.getColumnIndex(idName);
            do {
                long entityId = cursor.getLong(idColumnIndex);
                TableClass entity = tableInfo.isCachingEnabled() ? (TableClass) modelCache.get(entityId) : null;
                if (entity == null) {
                    entity = createModelInstance(table);
                    modelAdapter.loadFromCursor(entity, cursor);
                    entities.add(entity);

                    if (!disableCacheForThisQuery) {
                        modelCache.addModel(entityId, entity);
                    }
                }
            } while (cursor.moveToNext());
        }
        return entities;
    }

    private static <TableClass> TableClass createModelInstance(Class<TableClass> table) {
        try {
            Constructor<TableClass> entityConstructor = table.getConstructor();
            return entityConstructor.newInstance();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(
                    "Your model " + table.getName() + " does not define a default " +
                            "constructor. The default constructor is required for " +
                            "now in ReActiveAndroid models, as the process to " +
                            "populate the ORM model is : " +
                            "1. instantiate default model " +
                            "2. populate fields");
        } catch (Exception e) {
            ReActiveLog.e(LogLevel.BASIC, "Failed to process cursor.", e);
        }
        return null;
    }

}
