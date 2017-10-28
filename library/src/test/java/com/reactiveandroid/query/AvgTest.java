package com.reactiveandroid.query;

import android.database.Cursor;

import com.reactiveandroid.test.BaseTest;
import com.reactiveandroid.test.DataBaseTestRule;
import com.reactiveandroid.test.models.TestModel;

import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AvgTest extends BaseTest {

    @Rule
    public DataBaseTestRule dataBaseTestRule = DataBaseTestRule.create();

    @Test
    public void testAvgSql() {
        String expected = "SELECT AVG(intField) FROM TestModel";

        String actual = Select
                .avg("intField")
                .from(TestModel.class)
                .getSql();

        assertEquals(expected, actual);
    }

    @Test
    public void testAvgWhereClauseSql() {
        String expected = "SELECT AVG(intField) FROM TestModel WHERE intField = ?";

        String actual = Select
                .avg("intField")
                .from(TestModel.class)
                .where("intField = ?", 1)
                .getSql();

        assertEquals(expected, actual);
    }

    @Test
    public void testAvgOrderBySql() {
        String expected = "SELECT AVG(intField) FROM TestModel WHERE intField <> ? ORDER BY intField";

        String actual = Select
                .avg("intField")
                .from(TestModel.class)
                .where("intField <> ?", 0)
                .orderBy("intField")
                .getSql();

        assertEquals(expected, actual);
    }

    @Test
    public void testColumnAvg() {
        cleanTable();
        populateTable();

        float avg = Select
                .avg("intField")
                .from(TestModel.class)
                .fetchValue(float.class);

        assertEquals(3f, avg, 0);
    }

    @Test
    public void testAvgWhereClause() {
        cleanTable();
        populateTable();

        float avg = Select
                .avg("intField")
                .from(TestModel.class)
                .where("intField > ?", 2)
                .fetchValue(float.class);

        assertEquals(4.5f, avg, 0);
    }

    @Test
    public void testAvgEmptyResult() {
        cleanTable();
        populateTable();

        float avg = Select
                .avg("intField")
                .from(TestModel.class)
                .where("intField > ?", 10)
                .fetchValue(float.class);

        assertEquals(0, avg, 0);
    }

    @Test
    public void testAvgOrderBy() {
        cleanTable();
        populateTable();

        float avg = Select
                .avg("intField")
                .from(TestModel.class)
                .where("intField > ?", 2)
                .orderBy("intField ASC")
                .fetchValue(float.class);

        //Should not change the result if order by is used.
        assertEquals(4.5f, avg, 0);
    }

    @Test
    public void testAvgGroupBy() {
        cleanTable();
        populateTable();

        Cursor avgCursor = Select
                .avg("intField")
                .from(TestModel.class)
                .groupBy("stringField")
                .fetchCursor();

        avgCursor.moveToFirst(); //avg for "Category 1"
        assertEquals(2f, avgCursor.getFloat(0), 0);
        avgCursor.moveToNext(); //avg for "Category 2"
        assertEquals(4f, avgCursor.getFloat(0), 0);
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
