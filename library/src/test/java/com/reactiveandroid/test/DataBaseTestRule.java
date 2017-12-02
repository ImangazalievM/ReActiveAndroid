package com.reactiveandroid.test;

import com.reactiveandroid.ReActiveAndroid;
import com.reactiveandroid.ReActiveConfig;
import com.reactiveandroid.internal.database.DatabaseConfig;
import com.reactiveandroid.test.databases.TestDatabase;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class DataBaseTestRule implements TestRule {

    public static DataBaseTestRule create() {
        return new DataBaseTestRule();
    }

    @Override
    public Statement apply(final Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                DatabaseConfig testDatabaseConfig = new DatabaseConfig.Builder(TestDatabase.class)
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
