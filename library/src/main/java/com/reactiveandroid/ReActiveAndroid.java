package com.reactiveandroid;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.reactiveandroid.database.DatabaseConfig;
import com.reactiveandroid.database.DatabaseInfo;
import com.reactiveandroid.database.table.QueryTableInfo;
import com.reactiveandroid.database.table.TableInfo;
import com.reactiveandroid.internal.log.LogLevel;
import com.reactiveandroid.internal.log.ReActiveLog;
import com.reactiveandroid.serializer.TypeSerializer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class ReActiveAndroid {

    private static Context context;
    private static Map<Class<?>, DatabaseInfo> databaseMap;
    private static Map<Class<?>, DatabaseInfo> tableDatabaseMap;
    private static boolean isInitialized = false;

    private ReActiveAndroid() {
    }

    /**
     * Initializes ReActiveAndroid.
     * Creates databases and tables, if they were not created
     * Runs migrations, if database version changed
     */
    public static synchronized void init(@NonNull ReActiveConfig reActiveConfig) {
        if (isInitialized) {
            ReActiveLog.v(LogLevel.BASIC, "ReActiveAndroid already initialized.");
            return;
        }

        context = reActiveConfig.context;
        databaseMap = new HashMap<>();
        tableDatabaseMap = new HashMap<>();

        for (DatabaseConfig databaseConfig : reActiveConfig.databaseConfigMap.values()) {
            DatabaseInfo reActiveDatabase = new DatabaseInfo(context, databaseConfig);
            databaseMap.put(databaseConfig.databaseClass, reActiveDatabase);

            for (TableInfo tableInfo : reActiveDatabase.getTableInfos()) {
                tableDatabaseMap.put(tableInfo.getModelClass(), reActiveDatabase);
            }

            for (QueryTableInfo queryTableInfo : reActiveDatabase.getQueryTableInfos()) {
                tableDatabaseMap.put(queryTableInfo.getModelClass(), reActiveDatabase);
            }
        }

        isInitialized = true;
        ReActiveLog.v(LogLevel.BASIC, "ReActiveAndroid initialized successfully.");
    }

    /**
     * Release all references to static variables
     */
    public static synchronized void destroy() {
        for (DatabaseInfo database : databaseMap.values()) {
            database.getOpenHelper().close();
        }

        context = null;
        databaseMap = null;
        tableDatabaseMap = null;
        isInitialized = false;

        ReActiveLog.v(LogLevel.BASIC, "ReActiveAndroid disposed. Call init to use library.");
    }

    public static Context getContext() {
        return context;
    }

    public static boolean isInitialized() {
        return isInitialized;
    }

    public static void setLogLevel(LogLevel logLevel) {
        ReActiveLog.setLogLevel(logLevel);
    }

    /**
     * @param databaseClass Database class
     * @return {@link DatabaseInfo} for specified database
     */
    @NonNull
    public static DatabaseInfo getDatabase(Class<?> databaseClass) {
        DatabaseInfo databaseInfo = databaseMap.get(databaseClass);
        if (databaseInfo == null) {
            throw new IllegalArgumentException("Database info referenced with class " + databaseClass.getName() + " not found.");
        }
        return databaseInfo;
    }

    /**
     * @param table Table by which we search the database
     * @return {@link DatabaseInfo} for specified table
     */
    @NonNull
    public static DatabaseInfo getDatabaseForTable(Class<?> table) {
        DatabaseInfo databaseInfo = tableDatabaseMap.get(table);
        if (databaseInfo == null) {
            throw new IllegalArgumentException("Database info referenced with table " + table.getName() + " not found.");
        }
        return databaseInfo;
    }

    /**
     * @param table Table by which we search the database
     * @return {@link SQLiteDatabase} for specified table
     */
    @NonNull
    public static SQLiteDatabase getWritableDatabaseForTable(Class<?> table) {
        return getDatabaseForTable(table).getWritableDatabase();
    }

    /**
     * @param databaseClass Database  in which tables are stored
     * @return Information about all tables, stored in the specified database
     */
    @NonNull
    public static Collection<TableInfo> getDatabaseTablesInfos(Class<?> databaseClass) {
        return getDatabase(databaseClass).getTableInfos();
    }

    /**
     * @param table Table class
     * @return {@link TableManager} for specified table
     */
    @NonNull
    public static TableManager getTableManager(Class<?> table) {
        return getDatabaseForTable(table).getTableManager(table);
    }

    /**
     * @param table Query table class
     * @return {@link QueryModelManager} for specified table
     */
    @NonNull
    public static QueryModelManager getQueryTableManager(Class<?> table) {
        return getDatabaseForTable(table).getQueryModelManager(table);
    }

    /**
     * @param table Table class
     * @return {@link TableInfo} for specified table
     */
    @NonNull
    public static TableInfo getTableInfo(Class<?> table) {
        return getDatabaseForTable(table).getTableInfo(table);
    }

    /**
     * @param table Table class
     * @param type  Deserialized type class
     * @return {@link TypeSerializer} for specified type
     */
    @Nullable
    public static TypeSerializer getSerializerForType(Class<?> table, Class<?> type) {
        return getDatabaseForTable(table).getTypeSerializer(type);
    }

    /**
     * @param table Table class
     * @return Table name for specified class
     */
    @NonNull
    public static String getTableName(Class<?> table) {
        return getDatabaseForTable(table).getTableInfo(table).getTableName();
    }

}
