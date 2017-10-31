package com.reactiveandroid.database;

import com.reactiveandroid.database.table.TableInfo;
import com.reactiveandroid.test.BaseTest;
import com.reactiveandroid.test.models.CategoryTestModel;
import com.reactiveandroid.test.models.TestModel;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Collections;

import static junit.framework.Assert.assertEquals;

public class TableInfoTest extends BaseTest {

    private TableInfo testTableInfo;
    private TableInfo categoryTestTableInfo;

    @Before
    public void setUp() throws Exception {
        testTableInfo = new TableInfo(TestModel.class, Collections.EMPTY_MAP);
        categoryTestTableInfo = new TableInfo(CategoryTestModel.class, Collections.EMPTY_MAP);
    }

    @Test
    public void testGetType() {
        assertEquals(TestModel.class, testTableInfo.getTableClass());
        assertEquals(CategoryTestModel.class, categoryTestTableInfo.getTableClass());
    }

    @Test
    public void testGetTableName(){
        assertEquals("TestModel", testTableInfo.getTableName());
        assertEquals("Categories", categoryTestTableInfo.getTableName());
    }

    @Test
    public void testGetIdName() {
        assertEquals("id", testTableInfo.getPrimaryKeyColumnName());
        assertEquals("cat_id", categoryTestTableInfo.getPrimaryKeyColumnName());
    }

    @Test
    public void testGetFields() {
        assertEquals(6, testTableInfo.getFields().size());
        assertEquals(4, categoryTestTableInfo.getFields().size());
    }

    @Test
    public void testGetColumnInfo() throws NoSuchFieldException {
        Field dateField = TestModel.class.getField("dateField");
        Field isVisibleField = CategoryTestModel.class.getField("isVisible");

        assertEquals("dateField", testTableInfo.getColumnInfo(dateField).name);
        assertEquals("visible", categoryTestTableInfo.getColumnInfo(isVisibleField).name);
    }

    @Test
    public void testGetIndexGroups() throws NoSuchFieldException {
        Field dateField = TestModel.class.getField("dateField");
        Field isVisibleField = CategoryTestModel.class.getField("isVisible");

        assertEquals("dateField", testTableInfo.getColumnInfo(dateField).name);
        assertEquals("visible", categoryTestTableInfo.getColumnInfo(isVisibleField).name);
    }

    @Test
    public void testGetUniqueGroups() throws NoSuchFieldException {
        Field dateField = TestModel.class.getField("dateField");
        Field isVisibleField = CategoryTestModel.class.getField("isVisible");

        assertEquals("dateField", testTableInfo.getColumnInfo(dateField).name);
        assertEquals("visible", categoryTestTableInfo.getColumnInfo(isVisibleField).name);
    }

}
