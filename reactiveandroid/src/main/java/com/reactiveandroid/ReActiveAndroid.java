package com.reactiveandroid;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.reactiveandroid.internal.database.DatabaseConfig;
import com.reactiveandroid.internal.database.DatabaseInfo;
import com.reactiveandroid.internal.database.table.QueryModelInfo;
import com.reactiveandroid.internal.database.table.TableInfo;
import com.reactiveandroid.internal.ModelAdapter;
import com.reactiveandroid.internal.QueryModelAdapter;
import com.reactiveandroid.internal.log.LogLevel;
import com.reactiveandroid.internal.log.ReActiveLog;
import com.reactiveandroid.internal.notifications.ModelChangeNotifier;
import com.reactiveandroid.internal.notifications.OnModelChangedListener;
import com.reactiveandroid.internal.notifications.OnTableChangedListener;
import com.reactiveandroid.internal.serializer.TypeSerializer;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

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

            for (QueryModelInfo queryModelInfo : reActiveDatabase.getQueryModelInfos()) {
                tableDatabaseMap.put(queryModelInfo.getModelClass(), reActiveDatabase);
            }

            reActiveDatabase.initDatabase();
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
     * @return {@link ModelAdapter} for specified table
     */
    @NonNull
    public static ModelAdapter getModelAdapter(Class<?> table) {
        return getDatabaseForTable(table).getModelAdapter(table);
    }

    /**
     * @param table Query table class
     * @return {@link QueryModelAdapter} for specified table
     */
    @NonNull
    public static QueryModelAdapter getQueryModelAdapter(Class<?> table) {
        return getDatabaseForTable(table).getQueryModelAdapter(table);
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
     * @param modelClass Model class
     * @param type  Deserialized type class
     * @return {@link TypeSerializer} for specified type
     */
    @Nullable
    public static TypeSerializer getSerializerForType(Class<?> modelClass, Class<?> type) {
        return getDatabaseForTable(modelClass).getTypeSerializer(type);
    }

    /**
     * @param table Table class
     * @return Table name for specified class
     */
    @NonNull
    public static String getTableName(Class<?> table) {
        return getDatabaseForTable(table).getTableInfo(table).getTableName();
    }

    public static <T> void registerForModelChanges(@NonNull Class<T> table,
                                                   @NonNull OnModelChangedListener<T> listener) {
        ModelChangeNotifier.get().registerForModelChanges(table, listener);
    }

    public static <T> void unregisterForModelStateChanges(@NonNull Class<T> table,
                                                          @NonNull OnModelChangedListener<T> listener) {
        ModelChangeNotifier.get().unregisterForModelStateChanges(table, listener);
    }

    public <T> void registerForTableChanges(@NonNull Class<T> table,
                                            @NonNull OnTableChangedListener listener) {
        ModelChangeNotifier.get().registerForTableChanges(table, listener);
    }

    public <T> void unregisterForTableChanges(@NonNull Class<T> table,
                                              @NonNull OnTableChangedListener listener) {
        ModelChangeNotifier.get().unregisterForTableChanges(table, listener);
    }

}
