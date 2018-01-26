package com.reactiveandroid.query;

import com.reactiveandroid.test.BaseTest;
import com.reactiveandroid.test.models.TestModel;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MaxTest extends BaseTest {

    @Test
    public void testMax() {
        populateTable();

        float max = Select
                .from(TestModel.class)
                .max("intField");

        assertEquals(6f, max, 0);
    }

    @Test
    public void testMaxWhereClause() {
        populateTable();

        float max = Select
                .from(TestModel.class)
                .where("intField < ?", 5)
                .max("intField");

        assertEquals(3f, max, 0);
    }

    @Test
    public void testMaxEmptyResult() {
        populateTable();

        float max = Select
                .from(TestModel.class)
                .where("intField > ?", 10)
                .max("intField");

        assertEquals(0, max, 0);
    }

    @Test
    public void testMaxOrderBy() {
        populateTable();

        float max = Select
                .from(TestModel.class)
                .orderBy("intField ASC")
                .max("intField");

        //Should not change the result if order by is used.
        assertEquals(6f, max, 0);
    }

    @Test
    public void testMaxGroupBy() {
        populateTable();

        float max = Select
                .from(TestModel.class)
                .groupBy("stringField")
                .max("intField");

        //Should return maximal value which belong to the first category in selection
        //May seem weird, just test it in an SQL browser
        assertEquals(3f, max, 0);
    }

    private void populateTable() {
        TestModel m1 = new TestModel();
        TestModel m2 = new TestModel();
        TestModel m3 = new TestModel();
        TestModel m4 = new TestModel();

        m1.stringField = "Category 1";
        m1.intField = 1;

        m2.stringField = "Category 2";
        m2.intField = 2;

        m3.stringField = "Category 1";
        m3.intField = 3;

        m4.stringField = "Category 2";
        m4.intField = 6;


        m1.save();
        m2.save();
        m3.save();
        m4.save();
    }

}
