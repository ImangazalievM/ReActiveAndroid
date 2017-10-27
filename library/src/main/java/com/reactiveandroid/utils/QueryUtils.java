package com.reactiveandroid.utils;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.reactiveandroid.Model;
import com.reactiveandroid.ReActiveAndroid;
import com.reactiveandroid.TableManager;
import com.reactiveandroid.database.table.TableInfo;
import com.reactiveandroid.internal.cache.ModelCache;
import com.reactiveandroid.internal.log.LogLevel;
import com.reactiveandroid.internal.log.ReActiveLog;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QueryUtils {

    public static void execSQL(Class<? extends Model> table, String sql) {
        ReActiveAndroid.getWritableDatabaseForTable(table).execSQL(sql);
    }

    public static void execSQL(Class<? extends Model> table, String sql, String[] selectionArgs) {
        ReActiveAndroid.getWritableDatabaseForTable(table).execSQL(sql, selectionArgs);
    }

    @NonNull
    public static <TableClass extends Model> List<TableClass> rawQuery(Class<? extends Model> table,
                                                                       String sql, String[] selectionArgs,
                                                                       boolean disableCacheForThisQuery) {
        SQLiteDatabase database = ReActiveAndroid.getWritableDatabaseForTable(table);
        Cursor cursor = database.rawQuery(sql, selectionArgs);
        List<TableClass> entities = processCursor(table, cursor, disableCacheForThisQuery);
        cursor.close();
        return entities;
    }

    public static int intQuery(SQLiteDatabase database, String sql, String[] selectionArgs) {
        Cursor cursor = database.rawQuery(sql, selectionArgs);
        int number = cursor.moveToFirst() ?  cursor.getInt(0) : 0;
        cursor.close();
        return number;
    }

    public static float floatQuery(SQLiteDatabase database, String sql, String[] selectionArgs) {
        Cursor cursor = database.rawQuery(sql, selectionArgs);
        float number = cursor.moveToFirst() ?  cursor.getFloat(0) : 0f;
        cursor.close();
        return number;
    }

    @NonNull
    @SuppressWarnings("unchecked")
    public static <T extends Model> List<T> processCursor(Class<? extends Model> table, Cursor cursor, boolean disableCacheForThisQuery) {
        TableManager tableManager = ReActiveAndroid.getTableManager(table);
        TableInfo tableInfo = tableManager.getTableInfo();
        ModelCache modelCache = tableManager.getModelCache();
        String idName = tableInfo.getIdName();
        List<T> entities = new ArrayList<>();

        try {
            Constructor<?> entityConstructor = table.getConstructor();
            if (cursor.moveToFirst()) {
                List<String> columnsOrdered = new ArrayList<>(Arrays.asList(cursor.getColumnNames()));
                do {
                    long entityId = cursor.getLong(columnsOrdered.indexOf(idName));
                    Model entityFromCache = tableInfo.isCachingEnabled() ? modelCache.get(entityId) : null;
                    Model entity = entityFromCache != null ? entityFromCache : (Model) entityConstructor.newInstance();
                    entity.loadFromCursor(cursor);
                    entities.add((T) entity);

                    //if the model was not previously in the cache, then add it to the cache
                    if (entityFromCache == null && disableCacheForThisQuery) {
                        modelCache.addModel(entityId, entity);
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
