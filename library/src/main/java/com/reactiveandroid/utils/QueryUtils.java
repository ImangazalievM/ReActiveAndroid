package com.reactiveandroid.utils;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.reactiveandroid.Model;
import com.reactiveandroid.ReActiveAndroid;
import com.reactiveandroid.TableManager;
import com.reactiveandroid.database.table.ColumnInfo;
import com.reactiveandroid.database.table.TableInfo;
import com.reactiveandroid.internal.cache.ModelCache;
import com.reactiveandroid.internal.log.LogLevel;
import com.reactiveandroid.internal.log.ReActiveLog;
import com.reactiveandroid.query.Select;
import com.reactiveandroid.serializer.TypeSerializer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
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
    public static <TableClass extends Model> List<TableClass> rawQuery(Class<TableClass> table,
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
    public static <TableClass extends Model> List<TableClass> processCursor(Class<TableClass> table,
                                                                            Cursor cursor,
                                                                            boolean disableCacheForThisQuery) {
        TableManager tableManager = ReActiveAndroid.getTableManager(table);
        TableInfo tableInfo = tableManager.getTableInfo();
        ModelCache modelCache = tableManager.getModelCache();
        String idName = tableInfo.getIdName();
        List<TableClass> entities = new ArrayList<>();

        try {
            Constructor<TableClass> entityConstructor = table.getConstructor();
            if (cursor.moveToFirst()) {
                int idColumnIndex = cursor.getColumnIndex(idName);
                do {
                    long entityId = cursor.getLong(idColumnIndex);
                    Model entityFromCache = tableInfo.isCachingEnabled() ? modelCache.get(entityId) : null;
                    Model entity = entityFromCache != null ? entityFromCache : entityConstructor.newInstance();
                    entity.loadFromCursor(cursor);
                    entities.add((TableClass) entity);

                    //if the model was not previously in the cache, then add it to the cache
                    if (entityFromCache == null && !disableCacheForThisQuery) {
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
