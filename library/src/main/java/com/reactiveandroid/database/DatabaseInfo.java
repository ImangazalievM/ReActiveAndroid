package com.reactiveandroid.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.reactiveandroid.Model;
import com.reactiveandroid.database.table.TableInfo;
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
    private Map<Class<? extends Model>, TableInfo> tableInfos = new HashMap<>();
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

    public ReActiveOpenHelper getOpenHelper() {
        return reActiveOpenHelper;
    }

    public SQLiteDatabase getWritableDatabase() {
        return reActiveOpenHelper.getWritableDatabase();
    }

    public Collection<TableInfo> getTableInfos() {
        return tableInfos.values();
    }

    public TableInfo getTableInfo(Class<? extends Model> table) {
        TableInfo tableInfo = tableInfos.get(table);
        if (tableInfo == null) {
            throw new IllegalStateException(String.format("Table info for class %s not found", table.getSimpleName()));
        }
        return tableInfo;
    }

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

        List<Class<? extends Model>> models = databaseConfig.modelClasses;
        if (models != null) {
            for (Class<? extends Model> model : models) {
                tableInfos.put(model, new TableInfo(model, typeSerializers));
            }
        }
        return true;
    }

    private void scanModelsInfo(Context context, Class<?> databaseClass) {
        List<Class> modelClasses = ReflectionUtils.getDatabaseModelClasses(context, databaseClass);
        for (Class modelClass : modelClasses) {
            tableInfos.put(modelClass, new TableInfo(modelClass, typeSerializers));
        }
    }

}
