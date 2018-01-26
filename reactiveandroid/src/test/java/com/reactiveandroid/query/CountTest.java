package com.reactiveandroid.query;

import com.reactiveandroid.test.BaseTest;
import com.reactiveandroid.test.models.TestModel;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class CountTest extends BaseTest {

    @Test
    public void testCountTable() {
        cleanTable();
        populateTable();

        List<TestModel> list = Select.from(TestModel.class).fetch();
        int count = Select.from(TestModel.class).count();

        assertEquals(3, count);
        assertEquals(list.size(), count);
    }

    @Test
    public void testCountWhereClause() {
        cleanTable();
        populateTable();

        List<TestModel> list = Select
                .from(TestModel.class)
                .where("intField = ?", 1)
                .fetch();

        int count = Select
                .from(TestModel.class)
                .where("intField = ?", 1)
                .count();

        assertEquals(2, count);
        assertEquals(list.size(), count);
    }

    @Test
    public void testCountEmptyResult() {
        cleanTable();
        populateTable();

        List<TestModel> list = Select
                .from(TestModel.class)
                .where("intField = ?", 3)
                .fetch();
        int count = Select
                .from(TestModel.class)
                .where("intField = ?", 3)
                .count();

        //Should return the same count as there are entries in the result set if the where-clause
        //matches zero entries.
        assertEquals(0, count);
        assertEquals(list.size(), count);
    }

    @Test
    public void testCountOrderBy() {
        cleanTable();
        populateTable();

        int count = Select
                .from(TestModel.class)
                .orderBy("intField ASC")
                .count();

        List<TestModel> list = Select
                .from(TestModel.class)
                .orderBy("intField ASC")
                .fetch();

        //Should not change the result if order by is used.
        assertEquals(3, count);
        assertEquals(list.size(), count);
    }

    @Test
    public void testCountGroupBy() {
        cleanTable();
        populateTable();

        int count = Select
                .from(TestModel.class)
                .groupBy("intField")
                .having("intField = 1")
                .count();

        List<TestModel> list = Select
                .from(TestModel.class)
                .groupBy("intField")
                .having("intField = 1")
                .fetch();

        //Should return the total number of rows, even if the rows are grouped.
        //May seem weird, just test it in an SQL browser
        assertEquals(2, count);
        assertEquals(1, list.size());
    }

    private void cleanTable() {
        Delete.from(TestModel.class).execute();
    }

    private void populateTable() {
        TestModel m1 = new TestModel();
        TestModel m2 = new TestModel();
        TestModel m3 = new TestModel();

        m1.intField = 1;
        m2.intField = 1;
        m3.intField = 2;

        m1.save();
        m2.save();
        m3.save();
    }

}