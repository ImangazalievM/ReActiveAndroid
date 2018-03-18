package com.reactiveandroid.internal.database.tableinfo;

import com.reactiveandroid.internal.database.table.TableInfo;
import com.reactiveandroid.internal.serializer.TypeSerializer;
import com.reactiveandroid.test.BaseTest;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Collections;

import static junit.framework.Assert.assertEquals;

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
    public void testGetTableName(){
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

    //ToDo: add tests for:
    // 1. unique groups and index groups
    // 2. inherited models

}
