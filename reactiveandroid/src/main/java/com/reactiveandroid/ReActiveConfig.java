package com.reactiveandroid;

import android.content.Context;
import android.support.annotation.NonNull;

import com.reactiveandroid.internal.database.DatabaseConfig;

import java.util.HashMap;
import java.util.Map;

public class ReActiveConfig {

    public final Context context;
    public final Map<Class<?>, DatabaseConfig> databaseConfigMap;

    private ReActiveConfig(Context context, Map<Class<?>, DatabaseConfig> databaseConfigMap) {
        this.context = context;
        this.databaseConfigMap = databaseConfigMap;
    }

    public static class Builder {

        private final Context context;
        private final Map<Class<?>, DatabaseConfig> databaseConfigMap;

        public Builder(@NonNull Context context) {
            this.context = context.getApplicationContext();
            this.databaseConfigMap = new HashMap<>();
        }

        public Builder addDatabaseConfigs(@NonNull DatabaseConfig... configs) {
            for (DatabaseConfig config : configs) {
                databaseConfigMap.put(config.databaseClass, config);
            }
            return this;
        }

        public ReActiveConfig build() {
            return new ReActiveConfig(context, databaseConfigMap);
        }

    }

}
