package com.reactiveandroid.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.reactiveandroid.QueryModelAdapter;
import com.reactiveandroid.ModelAdapter;
import com.reactiveandroid.database.table.QueryTableInfo;
import com.reactiveandroid.database.table.TableInfo;
import com.reactiveandroid.internal.cache.ModelLruCache;
import com.reactiveandroid.internal.log.LogLevel;
import com.reactiveandroid.internal.log.ReActiveLog;
import com.reactiveandroid.serializer.CalendarSerializer;
import com.reactiveandroid.serializer.FileSerializer;
import com.reactiveandroid.serializer.SqlDateSerializer;
import com.reactiveandroid.serializer.TypeSerializer;
import com.reactiveandroid.serializer.UtilDateSerializer;
import com.reactiveandroid.utils.ReflectionUtils;

import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains information about database
 */
public class DatabaseInfo {

    private ReActiveOpenHelper reActiveOpenHelper;
    private Map<Class<?>, TableInfo> tableInfos = new HashMap<>();
    private Map<Class<?>, ModelAdapter> tableManagerMap = new HashMap<>();
    private Map<Class<?>, QueryTableInfo> queryTableInfos = new HashMap<>();
    private Map<Class<?>, QueryModelAdapter> queryTableManagerMap = new HashMap<>();
    private Map<Class<?>, TypeSerializer> typeSerializers = new HashMap<Class<?>, TypeSerializer>() {{
        put(Calendar.class, new CalendarSerializer());
        put(java.sql.Date.class, new SqlDateSerializer());
        put(java.util.Date.class, new UtilDateSerializer());
        put(java.io.File.class, new FileSerializer());
    }};

    public DatabaseInfo(Context context, DatabaseConfig databaseConfig) {
        this.reActiveOpenHelper = new ReActiveOpenHelper(context, databaseConfig);

        //Runs tables creation
        //getWritableDatabase();

        if (!loadModelFromConfig(databaseConfig)) {
            scanModelsInfo(context, databaseConfig.databaseClass);
        }

        ReActiveLog.i(LogLevel.BASIC, "Tables info for database " + databaseConfig.databaseName + " loaded.");
    }

    @NonNull
    public ReActiveOpenHelper getOpenHelper() {
        return reActiveOpenHelper;
    }

    @NonNull
    public SQLiteDatabase getWritableDatabase() {
        return reActiveOpenHelper.getWritableDatabase();
    }

    @NonNull
    public Collection<TableInfo> getTableInfos() {
        return tableInfos.values();
    }

    @NonNull
    public Collection<QueryTableInfo> getQueryTableInfos() {
        return queryTableInfos.values();
    }

    @NonNull
    public TableInfo getTableInfo(Class<?> table) {
        TableInfo tableInfo = tableInfos.get(table);
        if (tableInfo == null) {
            throw new IllegalStateException(String.format("Table info for class %s not found", table.getSimpleName()));
        }
        return tableInfo;
    }

    @NonNull
    public ModelAdapter getTableManager(Class<?> table) {
        ModelAdapter modelAdapter = tableManagerMap.get(table);
        if (modelAdapter == null) {
            throw new IllegalArgumentException("Cannot find TableManager for " + table.getName());
        }
        return modelAdapter;
    }

    @NonNull
    public QueryModelAdapter getQueryModelManager(Class<?> table) {
        QueryModelAdapter queryModelAdapter = queryTableManagerMap.get(table);
        if (queryModelAdapter == null) {
            throw new IllegalArgumentException("Cannot find TableManager for " + table.getName());
        }
        return queryModelAdapter;
    }

    @Nullable
    public TypeSerializer getTypeSerializer(Class<?> type) {
        return typeSerializers.get(type);
    }

    private boolean loadModelFromConfig(DatabaseConfig databaseConfig) {
        if (!databaseConfig.isValid()) {
            return false;
        }

        List<Class<? extends TypeSerializer>> customTypeSerializers = databaseConfig.typeSerializers;
        if (customTypeSerializers != null) {
            for (Class<? extends TypeSerializer> typeSerializer : customTypeSerializers) {
                try {
                    TypeSerializer instance = typeSerializer.newInstance();
                    typeSerializers.put(instance.getDeserializedType(), instance);
                } catch (InstantiationException e) {
                    ReActiveLog.e(LogLevel.BASIC, "Couldn't instantiate TypeSerializer.", e);
                } catch (IllegalAccessException e) {
                    ReActiveLog.e(LogLevel.BASIC, "IllegalAccessException", e);
                }
            }
        }

        List<Class<?>> models = databaseConfig.modelClasses;
        if (models != null) {
            for (Class<?> model : models) {
                tableInfos.put(model, new TableInfo(model, typeSerializers));
            }
        }
        return true;
    }

    private void scanModelsInfo(Context context, Class<?> databaseClass) {
        List<Class> allClasses = ReflectionUtils.getAllClasses(context);
        List<Class> tableClasses = ReflectionUtils.getDatabaseModelClasses(allClasses, databaseClass);
        List<Class> queryTableClasses = ReflectionUtils.getDatabaseQueryModelClasses(allClasses, databaseClass);

        for (Class tableClass : tableClasses) {
            TableInfo tableInfo = new TableInfo(tableClass, typeSerializers);
            tableInfos.put(tableClass, tableInfo);
            tableManagerMap.put(tableClass, new ModelAdapter(tableInfo, new ModelLruCache(tableInfo.getCacheSize())));
        }

        for (Class queryTableClass : queryTableClasses) {
            QueryTableInfo queryTableInfo = new QueryTableInfo(queryTableClass, typeSerializers);
            queryTableInfos.put(queryTableClass, queryTableInfo);
            queryTableManagerMap.put(queryTableClass, new QueryModelAdapter(queryTableInfo));
        }
    }

}
