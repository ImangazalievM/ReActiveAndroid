package com.reactiveandroid.query;

import com.reactiveandroid.test.BaseTest;
import com.reactiveandroid.test.DataBaseTestRule;
import com.reactiveandroid.test.models.TestModel;

import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SumTest extends BaseTest {

    @Rule
    public DataBaseTestRule dataBaseTestRule = DataBaseTestRule.create();

    @Test
    public void testColumnMin() {
        cleanTable();
        populateTable();

        float sum = Select
                .from(TestModel.class)
                .sum("intField");

        assertEquals(12, sum, 0);
    }

    @Test
    public void testSumWhereClause() {
        cleanTable();
        populateTable();

        float sum = Select
                .from(TestModel.class)
                .where("intField > ?", 1)
                .min("intField");

        assertEquals(2, sum, 0);
    }

    @Test
    public void testSumEmptyResult() {
        cleanTable();
        populateTable();

        float sum = Select
                .from(TestModel.class)
                .where("intField > ?", 10)
                .min("intField");

        assertEquals(0, sum, 0);
    }

    @Test
    public void testSumOrderBy() {
        cleanTable();
        populateTable();

        float sum = Select
                .from(TestModel.class)
                .orderBy("intField ASC")
                .sum("intField");

        //Should not change the result if order by is used.
        assertEquals(12, sum, 0);
    }

    @Test
    public void testSumGroupBy() {
        cleanTable();
        populateTable();

        float sum = Select
                .from(TestModel.class)
                .groupBy("stringField")
                .sum("intField");

        //Should return minimal of the values which belong to the first category in selection
        //May seem weird, just test it in an SQL browser
        assertEquals(4, sum, 0);
    }

    private void cleanTable() {
        Delete.from(TestModel.class).execute();
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
