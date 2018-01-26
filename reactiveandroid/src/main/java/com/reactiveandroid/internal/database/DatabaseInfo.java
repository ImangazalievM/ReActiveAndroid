package com.reactiveandroid.internal.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.reactiveandroid.internal.QueryModelAdapter;
import com.reactiveandroid.internal.ModelAdapter;
import com.reactiveandroid.internal.database.table.QueryModelInfo;
import com.reactiveandroid.internal.database.table.TableInfo;
import com.reactiveandroid.internal.log.LogLevel;
import com.reactiveandroid.internal.log.ReActiveLog;
import com.reactiveandroid.internal.serializer.CalendarSerializer;
import com.reactiveandroid.internal.serializer.FileSerializer;
import com.reactiveandroid.internal.serializer.SqlDateSerializer;
import com.reactiveandroid.internal.serializer.TypeSerializer;
import com.reactiveandroid.internal.serializer.UtilDateSerializer;
import com.reactiveandroid.internal.utils.ReflectionUtils;

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
    private final Map<Class<?>, TableInfo> tableInfos = new HashMap<>();
    private Map<Class<?>, ModelAdapter> tableModelrMap = new HashMap<>();
    private Map<Class<?>, QueryModelInfo> queryModelInfos = new HashMap<>();
    private Map<Class<?>, QueryModelAdapter> queryModelManagerMap = new HashMap<>();
    private Map<Class<?>, TypeSerializer> typeSerializers = new HashMap<Class<?>, TypeSerializer>() {{
        put(Calendar.class, new CalendarSerializer());
        put(java.sql.Date.class, new SqlDateSerializer());
        put(java.util.Date.class, new UtilDateSerializer());
        put(java.io.File.class, new FileSerializer());
    }};

    public DatabaseInfo(Context context, @NonNull DatabaseConfig databaseConfig) {
        loadModels(context, databaseConfig);
        loadTypeSerializers(databaseConfig);

        this.reActiveOpenHelper = new ReActiveOpenHelper(context, databaseConfig, tableInfos.values());

        ReActiveLog.i(LogLevel.BASIC, "Tables info for database " + databaseConfig.databaseName + " loaded.");
    }

    public void initDatabase() {
        //Runs tables creation
        getWritableDatabase();
    }

    @NonNull
    public ReActiveOpenHelper getOpenHelper() {
        return reActiveOpenHelper;
    }

    @NonNull
    public SQLiteDatabase getWritableDatabase() {
        return reActiveOpenHelper.getWritableDatabase();
    }

    public void beginTransaction() {
        reActiveOpenHelper.getWritableDatabase().beginTransaction();
    }

    public void endTransaction() {
        reActiveOpenHelper.getWritableDatabase().endTransaction();
    }

    @NonNull
    public Collection<TableInfo> getTableInfos() {
        return tableInfos.values();
    }

    @NonNull
    public Collection<QueryModelInfo> getQueryModelInfos() {
        return queryModelInfos.values();
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
    public ModelAdapter getModelAdapter(Class<?> table) {
        ModelAdapter modelAdapter = tableModelrMap.get(table);
        if (modelAdapter == null) {
            throw new IllegalArgumentException("Cannot find ModelAdapter for " + table.getName());
        }
        return modelAdapter;
    }

    @NonNull
    public QueryModelAdapter getQueryModelAdapter(Class<?> table) {
        QueryModelAdapter queryModelAdapter = queryModelManagerMap.get(table);
        if (queryModelAdapter == null) {
            throw new IllegalArgumentException("Cannot find ModelAdapter for " + table.getName());
        }
        return queryModelAdapter;
    }

    @Nullable
    public TypeSerializer getTypeSerializer(Class<?> type) {
        return typeSerializers.get(type);
    }

    private void loadModels(Context context, DatabaseConfig databaseConfig) {
        List<Class<?>> allClasses;
        if (!databaseConfig.modelClasses.isEmpty()) {
            allClasses = databaseConfig.modelClasses;
        } else {
            allClasses = ReflectionUtils.getAllClasses(context);
        }

        Class<?> databaseClass = databaseConfig.databaseClass;
        List<Class<?>> modelClasses = ReflectionUtils.getDatabaseTableClasses(allClasses, databaseClass);
        List<Class<?>> queryModelClasses = ReflectionUtils.getDatabaseQueryModelClasses(allClasses, databaseClass);

        createTablesInfo(modelClasses);
        createQueryModelsInfo(queryModelClasses);
    }

    private void loadTypeSerializers(DatabaseConfig databaseConfig) {
        List<Class<? extends TypeSerializer>> customTypeSerializers = databaseConfig.typeSerializers;
        if (customTypeSerializers != null) {
            for (Class<? extends TypeSerializer> typeSerializer : customTypeSerializers) {
                try {
                    TypeSerializer instance = typeSerializer.newInstance();
                    typeSerializers.put(instance.getDeserializedType(), instance);
                } catch (InstantiationException e) {
                    ReActiveLog.e(LogLevel.BASIC, "Couldn't instantiate TypeSerializer.", e);
                } catch (IllegalAccessException e) {
                    ReActiveLog.e(LogLevel.BASIC, "IllegalAccessException while instantiating "
                            + typeSerializer.getClass().getCanonicalName(), e);
                }
            }
        }
    }

    private void createTablesInfo(List<Class<?>> tableClasses) {
        for (Class<?> tableClass : tableClasses) {
            TableInfo tableInfo = new TableInfo(tableClass, typeSerializers);
            tableInfos.put(tableClass, tableInfo);
            tableModelrMap.put(tableClass, new ModelAdapter(tableInfo));
        }
    }

    private void createQueryModelsInfo(List<Class<?>> queryModelClasses) {
        for (Class<?> queryModelClass : queryModelClasses) {
            QueryModelInfo queryModelInfo = new QueryModelInfo(queryModelClass, typeSerializers);
            queryModelInfos.put(queryModelClass, queryModelInfo);
            queryModelManagerMap.put(queryModelClass, new QueryModelAdapter(queryModelInfo));
        }
    }

}
