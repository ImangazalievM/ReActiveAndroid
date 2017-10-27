package com.reactiveandroid.database;

import com.reactiveandroid.test.BaseTest;
import com.reactiveandroid.test.databases.TestDatabase;
import com.reactiveandroid.test.TestUtils;

import org.junit.Test;

public class DatabaseOpenHelperTest extends BaseTest {

    @Test
    public void test() {
        DatabaseConfig configuration = new DatabaseConfig.Builder(TestDatabase.class)
                .build();

        ReActiveOpenHelper databaseHelper = new ReActiveOpenHelper(TestUtils.getApplication(), configuration);

        //TestUtils.testNotImplemented();
    }

}
