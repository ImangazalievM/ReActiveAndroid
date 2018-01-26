package com.reactiveandroid.internal.serializer;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.reactiveandroid.test.BaseTest;

import org.junit.Test;

import java.util.Date;

import static junit.framework.Assert.assertEquals;

public class TypeSerializerTest extends BaseTest {

    @Test
    public void testClassesOfGeneric_shouldReturnSerializeAndDeserializeClasses() {
        TestDeserializer testDeserializer = new TestDeserializer();
        assertEquals(Date.class, testDeserializer.getDeserializedType());
        assertEquals(Long.class, testDeserializer.getSerializedType());
    }

    @Test
    public void testGenericTypeNotSpecified_shouldThrowException() {
        try {
            new IncorrectTestDeserializer();
        } catch (IllegalArgumentException e) {
            assertEquals("Please specify deserialized and serialized types via generic", e.getMessage());
        }
    }

    private class TestDeserializer extends TypeSerializer<Date, Long> {

        @Nullable
        @Override
        public Long serialize(@NonNull Date data) {
            return null;
        }

        @Nullable
        @Override
        public Date deserialize(@NonNull Long data) {
            return null;
        }

    }

    private class IncorrectTestDeserializer extends TypeSerializer {

        @Nullable
        @Override
        public Date serialize(@NonNull Object data) {
            return null;
        }

        @Nullable
        @Override
        public Long deserialize(@NonNull Object data) {
            return null;
        }

    }

}
