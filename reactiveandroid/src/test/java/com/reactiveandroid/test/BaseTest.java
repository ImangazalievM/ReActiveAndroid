package com.reactiveandroid.test;

import com.reactiveandroid.internal.serializer.TypeSerializer;

import org.junit.Rule;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

@RunWith(ReActiveAndroidTestRunner.class)
public abstract class BaseTest {

    @Rule
    public DataBaseTestRule rule = DataBaseTestRule.create(getDatabaseClass(), getDatabaseModels(), getTypeSerializers());

    public Class<?> getDatabaseClass() {
        return TestDatabase.class;
    }

    public List<Class<?>> getDatabaseModels() {
        return Arrays.asList();
    }

    public List<Class<? extends TypeSerializer>> getTypeSerializers() {
        return Arrays.asList();
    }

}
