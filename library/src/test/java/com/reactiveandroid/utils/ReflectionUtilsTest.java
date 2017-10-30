package com.reactiveandroid.utils;

import com.reactiveandroid.Model;
import com.reactiveandroid.serializer.UtilDateSerializer;
import com.reactiveandroid.test.BaseTest;
import com.reactiveandroid.test.ReActiveAndroidTestRunner;
import com.reactiveandroid.test.databases.EmptyDatabase;
import com.reactiveandroid.test.databases.TestDatabase;
import com.reactiveandroid.test.TestUtils;
import com.reactiveandroid.test.models.AbstractTestModel;
import com.reactiveandroid.test.models.SimplePojo;
import com.reactiveandroid.test.models.TestModel;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

@RunWith(ReActiveAndroidTestRunner.class)
public class ReflectionUtilsTest extends BaseTest {

    @Test
    public void testIsSubclassOf() {
        assertTrue(ReflectionUtils.isSubclassOf(TestModel.class, Model.class));
        assertFalse(ReflectionUtils.isSubclassOf(SimplePojo.class, Model.class));
    }

    @Test
    public void testIsModel() {
        assertTrue(ReflectionUtils.isModel(TestModel.class));
        assertFalse(ReflectionUtils.isModel(AbstractTestModel.class));
        assertFalse(ReflectionUtils.isModel(SimplePojo.class));
    }

    @Test
    public void testIsTypeSerializer() {
        assertTrue(ReflectionUtils.isTypeSerializer(UtilDateSerializer.class));
        assertFalse(ReflectionUtils.isTypeSerializer(TestModel.class));
    }

    @Test
    public void testGetDeclaredColumnFields() throws NoSuchFieldException {
        Set<Field> columnFields = ReflectionUtils.getDeclaredColumnFields(TestModel.class);
        Field[] columnFieldsArray = columnFields.toArray(new Field[columnFields.size()]);

        assertEquals(5, columnFieldsArray.length);
        //shouldn't include field without @Column annotation
        assertFalse(columnFields.contains(TestModel.class.getField("nonColumnField")));
        //the fields are sorted alphabetically in reverse order
        assertEquals(columnFieldsArray[0], TestModel.class.getField("stringField"));
        assertEquals(columnFieldsArray[1], TestModel.class.getField("intField"));
        assertEquals(columnFieldsArray[2], TestModel.class.getField("doubleField"));
        assertEquals(columnFieldsArray[3], TestModel.class.getField("dateField"));
        assertEquals(columnFieldsArray[4], TestModel.class.getField("booleanField"));
    }

    @Test
    public void testGetDatabaseModelClasses() throws NoSuchFieldException {
        List<Class> allClasses = ReflectionUtils.getAllClasses(TestUtils.getApplication());
        List<Class> testDatabaseModelClasses = ReflectionUtils.getDatabaseModelClasses(allClasses, TestDatabase.class);
        List<Class> emptyDatabaseModelClasses = ReflectionUtils.getDatabaseModelClasses(allClasses, EmptyDatabase.class);

        assertEquals(10, testDatabaseModelClasses.size());
        assertEquals(0, emptyDatabaseModelClasses.size());
    }

    @Test
    public void testGetDatabaseQueryModelClasses() throws NoSuchFieldException {
        List<Class> allClasses = ReflectionUtils.getAllClasses(TestUtils.getApplication());
        List<Class> testDatabaseQueryClasses = ReflectionUtils.getDatabaseQueryModelClasses(allClasses, TestDatabase.class);

        assertEquals(1, testDatabaseQueryClasses.size());
    }

}
