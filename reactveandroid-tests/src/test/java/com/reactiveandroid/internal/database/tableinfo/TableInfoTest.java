package com.reactiveandroid.internal.database.tableinfo;

import android.database.Cursor;

import com.reactiveandroid.internal.database.table.TableInfo;
import com.reactiveandroid.internal.serializer.TypeSerializer;
import com.reactiveandroid.internal.utils.QueryUtils;
import com.reactiveandroid.test.BaseTest;
import com.reactiveandroid.test.TestDatabase;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Collections;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class TableInfoTest extends BaseTest {

    private TableInfo testModelOneTableInfo;
    private TableInfo testModelTwoTableInfo;

    @Before
    public void setUp() {
        testModelOneTableInfo = new TableInfo(TestModelOne.class, Collections.<Class<?>, TypeSerializer>emptyMap());
        testModelTwoTableInfo = new TableInfo(TestModelTwo.class, Collections.<Class<?>, TypeSerializer>emptyMap());
    }

    @Test
    public void testGetType() {
        assertEquals(TestModelOne.class, testModelOneTableInfo.getModelClass());
        assertEquals(TestModelTwo.class, testModelTwoTableInfo.getModelClass());
    }

    @Test
    public void testGetTableName() {
        assertEquals("TestModelOne", testModelOneTableInfo.getTableName());
        assertEquals("MyCustomName", testModelTwoTableInfo.getTableName());
    }

    @Test
    public void testGetIdName() {
        assertEquals("id", testModelOneTableInfo.getPrimaryKeyColumnName());
        assertEquals("cat_id", testModelTwoTableInfo.getPrimaryKeyColumnName());
    }

    @Test
    public void testGetFields_shouldReturnCorrectFieldsCount() {
        assertEquals(5, testModelOneTableInfo.getColumnFields().size());
        assertEquals(4, testModelTwoTableInfo.getColumnFields().size());
    }

    @Test
    public void testGetColumnName_shouldReturnCorrectColumnName() throws NoSuchFieldException {
        Field dateField = TestModelOne.class.getField("stringField");
        Field isVisibleField = TestModelTwo.class.getField("isVisible");

        assertEquals("stringField", testModelOneTableInfo.getColumnInfo(dateField).name);
        assertEquals("visible", testModelTwoTableInfo.getColumnInfo(isVisibleField).name);
    }

    @Test
    public void testCreateWithDatabase_shouldNotCreateTable() {
        assertTrue(!isTableExists("TestModelOne"));
    }

    private boolean isTableExists(String tableName) {
        Cursor cursor = QueryUtils.rawQuery(TestDatabase.class, "select DISTINCT tbl_name " +
                "from sqlite_master where tbl_name = '" + tableName + "'", null);

        if (cursor.getCount() > 0) {
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }

    //ToDo: add tests for:
    // 1. unique groups and index groups
    // 2. inherited models

}
