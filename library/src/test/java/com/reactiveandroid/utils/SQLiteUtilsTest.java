package com.reactiveandroid.utils;

import android.text.TextUtils;

import com.reactiveandroid.ReActiveAndroid;
import com.reactiveandroid.database.DatabaseInfo;
import com.reactiveandroid.database.ReActiveMasterTable;
import com.reactiveandroid.database.table.TableInfo;
import com.reactiveandroid.test.BaseTest;
import com.reactiveandroid.test.databases.TestDatabase;
import com.reactiveandroid.test.models.FullTestModel;

import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class SQLiteUtilsTest extends BaseTest {

    @Test
    public void testGetAllTablesNames() {
        DatabaseInfo testDatabase = ReActiveAndroid.getDatabase(TestDatabase.class);
        List<String> tableNames = SQLiteUtils.getAllTableNames(testDatabase.getWritableDatabase());

        assertEquals(9, tableNames.size());
        assertTrue(tableNames.contains("TestModel") && tableNames.contains("Categories"));
        assertFalse(tableNames.contains(ReActiveMasterTable.TABLE_NAME));
    }

    @Test
    public void testCreateTableDefinition() {
        String expectedSql = "CREATE TABLE IF NOT EXISTS `FullTestModel` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "`booleanField` INTEGER, `dateField` INTEGER, `doubleField` REAL, `intField` INTEGER, `stringField` " +
                "TEXT COLLATE BINARY UNIQUE ON CONFLICT IGNORE, UNIQUE (`dateField`, `doubleField`) ON CONFLICT FAIL, " +
                "UNIQUE (`booleanField`, `intField`) ON CONFLICT ROLLBACK);";
        TableInfo testModelTableInfo = ReActiveAndroid.getTableInfo(FullTestModel.class);

        assertEquals(expectedSql, SQLiteUtils.createTableDefinition(testModelTableInfo));
    }

    @Test
    public void testCreateColumnDefinition() throws NoSuchFieldException {
        TableInfo testModelTableInfo = ReActiveAndroid.getTableInfo(FullTestModel.class);
        Field idField = testModelTableInfo.getFields().get(0);
        Field stringField = FullTestModel.class.getField("stringField");
        Field booleanField = FullTestModel.class.getField("booleanField");

        assertEquals("`id` INTEGER PRIMARY KEY AUTOINCREMENT", SQLiteUtils.createColumnDefinition(testModelTableInfo, idField));
        assertEquals("`stringField` TEXT COLLATE BINARY UNIQUE ON CONFLICT IGNORE", SQLiteUtils.createColumnDefinition(testModelTableInfo, stringField));
        assertEquals("`booleanField` INTEGER", SQLiteUtils.createColumnDefinition(testModelTableInfo, booleanField));
    }

    @Test
    public void testCreateUniqueDefinition() throws NoSuchFieldException {
        String expectedSql = "UNIQUE (`dateField`, `doubleField`) ON CONFLICT FAIL, " +
                "UNIQUE (`booleanField`, `intField`) ON CONFLICT ROLLBACK";
        TableInfo testModelTableInfo = ReActiveAndroid.getTableInfo(FullTestModel.class);

        List<String> createUniqueDefinitions = SQLiteUtils.createUniqueDefinition(testModelTableInfo);
        assertEquals(expectedSql, TextUtils.join(", ", createUniqueDefinitions));
    }

    @Test
    public void testCreateIndexDefinition() throws NoSuchFieldException {
        String expectedSql = "CREATE INDEX IF NOT EXISTS index_FullTestModel_index_1 on FullTestModel(`doubleField`, `intField`);";
        TableInfo testModelTableInfo = ReActiveAndroid.getTableInfo(FullTestModel.class);

        List<String> createIndexDefinition = SQLiteUtils.createIndexDefinition(testModelTableInfo);
        assertEquals(expectedSql, TextUtils.join(", ", createIndexDefinition));
    }


}
