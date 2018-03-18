package com.reactiveandroid.query.crud;

import com.reactiveandroid.query.Delete;
import com.reactiveandroid.query.Select;
import com.reactiveandroid.test.BaseTest;
import com.reactiveandroid.test.TestModel;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GroupConcatTest extends BaseTest {

    @Test
    public void testGroupConcat() {
        cleanTables();
        populateTable();

        String ids = Select
                .from(TestModel.class)
                .groupConcat("id");

        assertEquals("1,4,5,7,9", ids);
    }

    @Test
    public void testGroupConcatWithCustomSeparator() {
        cleanTables();
        populateTable();

        String ids = Select
                .from(TestModel.class)
                .groupConcat("id, \"|\"");

        assertEquals("1|4|5|7|9", ids);
    }

    private void cleanTables() {
        Delete.from(TestModel.class).execute();
    }

    private void populateTable() {
        new TestModel(1L).save();
        new TestModel(4L).save();
        new TestModel(5L).save();
        new TestModel(7L).save();
        new TestModel(9L).save();
    }

}
