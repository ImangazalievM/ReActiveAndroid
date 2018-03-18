package com.reactiveandroid.internal.serializer;

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

}
