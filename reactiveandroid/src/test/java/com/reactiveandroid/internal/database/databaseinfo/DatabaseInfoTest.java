package com.reactiveandroid.internal.database.databaseinfo;

import com.reactiveandroid.internal.database.DatabaseConfig;
import com.reactiveandroid.internal.database.DatabaseInfo;
import com.reactiveandroid.test.BaseTest;
import com.reactiveandroid.test.TestUtils;
import com.reactiveandroid.test.TestModel;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static junit.framework.Assert.assertEquals;

public class DatabaseInfoTest extends BaseTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testScanningModels() {
        DatabaseConfig config = new DatabaseConfig.Builder(MyDatabase.class).build();
        DatabaseInfo databaseInfo = new DatabaseInfo(TestUtils.getApplication(), config);

        assertEquals(2, databaseInfo.getTableInfos().size());
    }

    @Test
    public void testTablesFromConfig() {
        DatabaseConfig config = new DatabaseConfig.Builder(MyDatabase.class)
                .addModelClasses(TestModelOne.class, TestModelTwo.class)
                .build();
        DatabaseInfo databaseInfo = new DatabaseInfo(TestUtils.getApplication(), config);

        assertEquals(2, databaseInfo.getTableInfos().size());
    }

    @Test
    public void testNonExistingTableInfo() {
        DatabaseConfig config = new DatabaseConfig.Builder(MyDatabase.class)
                .addModelClasses(TestModel.class)
                .build();
        DatabaseInfo databaseInfo = new DatabaseInfo(TestUtils.getApplication(), config);

        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Table info for class AbstractModel not found");
        databaseInfo.getTableInfo(AbstractModel.class);
    }

}
