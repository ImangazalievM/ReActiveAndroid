package com.reactiveandroid.internal.serializer;

import com.reactiveandroid.test.BaseTest;

import org.junit.Test;

import java.util.Date;

import static junit.framework.Assert.assertEquals;

public class TypeSerializerTest extends BaseTest {

    @Test
    public void testClassesOfGeneric_shouldReturnSerializeAndDeserializeClasses() {
        TestSerializer testSerializer = new TestSerializer();
        assertEquals(Date.class, testSerializer.getDeserializedType());
        assertEquals(Long.class, testSerializer.getSerializedType());
    }

    @Test
    public void testGenericTypeNotSpecified_shouldThrowException() {
        try {
            new IncorrectTestSerializer();
        } catch (IllegalArgumentException e) {
            assertEquals("Please specify deserialized and serialized types via generic", e.getMessage());
        }
    }

}
