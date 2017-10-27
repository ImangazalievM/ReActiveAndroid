package com.reactiveandroid.query;

import com.reactiveandroid.test.BaseTest;
import com.reactiveandroid.test.DataBaseTestRule;
import com.reactiveandroid.test.models.TestModel;

import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MaxTest extends BaseTest {

    @Rule
    public DataBaseTestRule dataBaseTestRule = DataBaseTestRule.create();

    @Test
    public void testAvgSql() {
        String expected = "SELECT MAX(intField) FROM TestModel";

        String actual = Select
                .max("intField")
                .from(TestModel.class)
                .getSql();

        assertEquals(expected, actual);
    }

    @Test
    public void testCountWhereClauseSql() {
        String expected = "SELECT MAX(intField) FROM TestModel WHERE intField = ?";

        String actual = Select
                .max("intField")
                .from(TestModel.class)
                .where("intField = ?", 1)
                .getSql();

        assertEquals(expected, actual);
    }

    @Test
    public void testMaxOrderBySql() {
        String expected = "SELECT MAX(intField) FROM TestModel WHERE intField <> ? ORDER BY intField";

        String actual = Select
                .max("intField")
                .from(TestModel.class)
                .where("intField <> ?", 0)
                .orderBy("intField")
                .getSql();

        assertEquals(expected, actual);
    }

    @Test
    public void testColumnMax() {
        cleanTable();
        populateTable();

        float max = Select
                .max("intField")
                .from(TestModel.class)
                .fetchValue(float.class);

        assertEquals(6f, max, 0);
    }

    @Test
    public void testMaxWhereClause() {
        cleanTable();
        populateTable();

        float max = Select
                .max("intField")
                .from(TestModel.class)
                .where("intField < ?", 5)
                .fetchValue(float.class);

        assertEquals(3f, max, 0);
    }

    @Test
    public void testMaxEmptyResult() {
        cleanTable();
        populateTable();

        float max = Select
                .max("intField")
                .from(TestModel.class)
                .where("intField > ?", 10)
                .fetchValue(float.class);

        assertEquals(0, max, 0);
    }

    @Test
    public void testMaxOrderBy() {
        cleanTable();
        populateTable();

        float max = Select
                .max("intField")
                .from(TestModel.class)
                .where("intField < ?", 5)
                .orderBy("intField ASC")
                .fetchValue(float.class);

        //Should not change the result if order by is used.
        assertEquals(3f, max, 0);
    }

    @Test
    public void testMaxGroupBy() {
        cleanTable();
        populateTable();

        float max = Select
                .max("intField")
                .from(TestModel.class)
                .groupBy("stringField")
                .fetchValue(float.class);

        //Should return maximal value which belong to the first category in selection
        //May seem weird, just test it in an SQL browser
        assertEquals(3f, max, 0);
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
