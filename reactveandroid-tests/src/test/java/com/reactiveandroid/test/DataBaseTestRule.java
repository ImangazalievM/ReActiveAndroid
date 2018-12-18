package com.reactiveandroid.test;

import com.reactiveandroid.ReActiveAndroid;
import com.reactiveandroid.ReActiveConfig;
import com.reactiveandroid.internal.database.DatabaseConfig;
import com.reactiveandroid.internal.serializer.TypeSerializer;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.List;

public class DataBaseTestRule implements TestRule {

    public static DataBaseTestRule create(
            Class<?> databaseClass,
            List<Class<?>> databaseModels,
            List<Class<? extends TypeSerializer>> typeSerializers
    ) {
        return new DataBaseTestRule(databaseClass, databaseModels, typeSerializers);
    }

    private Class<?> databaseClass;
    private List<Class<?>> databaseModels;
    private List<Class<? extends TypeSerializer>> typeSerializers;

    private DataBaseTestRule(
            Class<?> databaseClass,
            List<Class<?>> databaseModels,
            List<Class<? extends TypeSerializer>> typeSerializers
    ) {
        this.databaseClass = databaseClass;
        this.databaseModels = databaseModels;
        this.typeSerializers = typeSerializers;
    }

    @Override
    public Statement apply(final Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                DatabaseConfig testDatabaseConfig = new DatabaseConfig.Builder(databaseClass)
                        .addModelClasses((Class<?>[]) databaseModels.toArray())
                        .addTypeSerializers((Class<? extends TypeSerializer>[]) typeSerializers.toArray())
                        .build();

                ReActiveAndroid.init(new ReActiveConfig.Builder(TestUtils.getApplication())
                        .addDatabaseConfigs(testDatabaseConfig)
                        .build());
                try {
                    base.evaluate();
                } finally {
                    ReActiveAndroid.destroy();
                }
            }
        };
    }

}
