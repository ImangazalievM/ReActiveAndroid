package com.reactiveandroid.internal.database;

import com.reactiveandroid.Model;
import com.reactiveandroid.annotation.Column;
import com.reactiveandroid.annotation.PrimaryKey;
import com.reactiveandroid.annotation.Table;
import com.reactiveandroid.internal.database.table.TableInfo;
import com.reactiveandroid.internal.serializer.TypeSerializer;
import com.reactiveandroid.test.BaseTest;
import com.reactiveandroid.test.databases.TestDatabase;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Collections;

import static junit.framework.Assert.assertEquals;

public class TableInfoTest extends BaseTest {

    private TableInfo testTableInfo;
    private TableInfo testCategoryTableInfo;

    @Before
    public void setUp() {
        testTableInfo = new TableInfo(TestModel.class, Collections.<Class<?>, TypeSerializer>emptyMap());
        testCategoryTableInfo = new TableInfo(CategoryTestModel.class, Collections.<Class<?>, TypeSerializer>emptyMap());
    }

    @Test
    public void testGetType() {
        assertEquals(TestModel.class, testTableInfo.getModelClass());
        assertEquals(CategoryTestModel.class, testCategoryTableInfo.getModelClass());
    }

    @Test
    public void testGetTableName(){
        assertEquals("TestModel", testTableInfo.getTableName());
        assertEquals("Categories", testCategoryTableInfo.getTableName());
    }

    @Test
    public void testGetIdName() {
        assertEquals("id", testTableInfo.getPrimaryKeyColumnName());
        assertEquals("cat_id", testCategoryTableInfo.getPrimaryKeyColumnName());
    }

    @Test
    public void testGetFields_shouldReturnCorrectFieldsCount() {
        assertEquals(5, testTableInfo.getColumnFields().size());
        assertEquals(4, testCategoryTableInfo.getColumnFields().size());
    }

    @Test
    public void testGetColumnName_shouldReturnCorrectColumnName() throws NoSuchFieldException {
        Field dateField = TestModel.class.getField("stringField");
        Field isVisibleField = CategoryTestModel.class.getField("isVisible");

        assertEquals("stringField", testTableInfo.getColumnInfo(dateField).name);
        assertEquals("visible", testCategoryTableInfo.getColumnInfo(isVisibleField).name);
    }

    //ToDo: add tests for:
    // 1. unique groups and index groups
    // 2. inherited models

    @Table(database = TestDatabase.class)
    public class TestModel extends Model {

        @PrimaryKey
        public Long id;
        @Column
        public String stringField;
        @Column
        public double doubleField;
        @Column
        public int intField;
        @Column
        public boolean booleanField;

        public int nonColumnField;

    }

    @Table(name = "Categories", database = TestDatabase.class)
    public class CategoryTestModel extends Model {

        @PrimaryKey(name = "cat_id")
        public Long id;
        @Column
        public String name;
        @Column(name = "sort_order")
        public int sortOrder;
        @Column(name = "visible")
        public boolean isVisible;

    }

}
