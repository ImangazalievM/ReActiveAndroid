package com.reactiveandroid.query.crud;

import com.reactiveandroid.query.Delete;
import com.reactiveandroid.test.BaseTest;
import com.reactiveandroid.test.TestModel;

import org.junit.Test;

import static com.reactiveandroid.test.TestUtils.assertSqlEquals;

public class DeleteTest extends BaseTest {

    @Test
    public void testFrom() {
        assertSqlEquals("DELETE FROM TestModel",  Delete.from(TestModel.class));
    }

}
