package com.reactiveandroid.internal.utils;

import com.reactiveandroid.Model;
import com.reactiveandroid.internal.serializer.UtilDateSerializer;
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
    public void testGetDeclaredFields() throws NoSuchFieldException {
        List<Field> declaredFields = ReflectionUtils.getDeclaredFields(TestModel.class);

        assertTrue(declaredFields.contains(TestModel.class.getField("nonColumnField")));
        assertTrue(declaredFields.contains(TestModel.class.getField("stringField")));
        assertTrue(declaredFields.contains(TestModel.class.getField("intField")));
        assertTrue(declaredFields.contains(TestModel.class.getField("doubleField")));
        assertTrue(declaredFields.contains(TestModel.class.getField("dateField")));
        assertTrue(declaredFields.contains(TestModel.class.getField("booleanField")));
    }

    @Test
    public void testGetDatabaseModelClasses() throws NoSuchFieldException {
        List<Class<?>> allClasses = ReflectionUtils.getAllClasses(TestUtils.getApplication());
        List<Class<?>> testDatabaseModelClasses = ReflectionUtils.getDatabaseTableClasses(allClasses, TestDatabase.class);
        List<Class<?>> emptyDatabaseModelClasses = ReflectionUtils.getDatabaseTableClasses(allClasses, EmptyDatabase.class);

        assertEquals(12, testDatabaseModelClasses.size());
        assertEquals(0, emptyDatabaseModelClasses.size());
    }

    @Test
    public void testGetDatabaseQueryModelClasses() throws NoSuchFieldException {
        List<Class<?>> allClasses = ReflectionUtils.getAllClasses(TestUtils.getApplication());
        List<Class<?>> testDatabaseQueryClasses = ReflectionUtils.getDatabaseQueryModelClasses(allClasses, TestDatabase.class);

        assertEquals(1, testDatabaseQueryClasses.size());
    }

}
