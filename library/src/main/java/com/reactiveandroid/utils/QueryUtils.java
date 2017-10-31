package com.reactiveandroid.utils;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.reactiveandroid.ReActiveAndroid;
import com.reactiveandroid.TableManager;
import com.reactiveandroid.database.table.TableInfo;
import com.reactiveandroid.internal.cache.ModelCache;
import com.reactiveandroid.internal.log.LogLevel;
import com.reactiveandroid.internal.log.ReActiveLog;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class QueryUtils {

    public static void execSQL(Class<?> table, String sql) {
        ReActiveAndroid.getWritableDatabaseForTable(table).execSQL(sql);
    }

    public static void execSQL(Class<?> table, String sql, String[] selectionArgs) {
        ReActiveAndroid.getWritableDatabaseForTable(table).execSQL(sql, selectionArgs);
    }

    @NonNull
    public static <TableClass> List<TableClass> rawQuery(Class<TableClass> table,
                                                         String sql, String[] selectionArgs,
                                                         boolean disableCacheForThisQuery) {
        SQLiteDatabase database = ReActiveAndroid.getWritableDatabaseForTable(table);
        Cursor cursor = database.rawQuery(sql, selectionArgs);
        List<TableClass> entities = processCursor(table, cursor, disableCacheForThisQuery);
        cursor.close();
        return entities;
    }

    @NonNull
    @SuppressWarnings("unchecked")
    public static <TableClass> List<TableClass> processCursor(Class<TableClass> table,
                                                              Cursor cursor,
                                                              boolean disableCacheForThisQuery) {
        TableManager tableManager = ReActiveAndroid.getTableManager(table);
        TableInfo tableInfo = tableManager.getTableInfo();
        ModelCache modelCache = tableManager.getModelCache();
        String idName = tableInfo.getPrimaryKeyColumnName();
        List<TableClass> entities = new ArrayList<>();

        try {
            Constructor<TableClass> entityConstructor = table.getConstructor();
            if (cursor.moveToFirst()) {
                int idColumnIndex = cursor.getColumnIndex(idName);
                do {
                    long entityId = cursor.getLong(idColumnIndex);
                    TableClass entity = tableInfo.isCachingEnabled() ? (TableClass) modelCache.get(entityId) : null;
                    if (entity == null) {
                        entity = entityConstructor.newInstance();
                        tableManager.loadFromCursor(entity, cursor);
                        entities.add(entity);
                        if (!disableCacheForThisQuery) {
                            modelCache.addModel(entityId, entity);
                        }
                    }
                } while (cursor.moveToNext());
            }
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

        return entities;
    }

}
