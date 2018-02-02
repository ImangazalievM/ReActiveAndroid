package com.reactiveandroid.query;

import android.database.Cursor;

import com.reactiveandroid.ReActiveAndroid;
import com.reactiveandroid.internal.database.table.ColumnInfo;
import com.reactiveandroid.internal.database.table.TableInfo;
import com.reactiveandroid.test.BaseTest;
import com.reactiveandroid.test.models.TestModel;

import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class MinTest extends BaseTest {

    @Test
    public void testMin() {
        cleanTable();
        populateTable();

        float min = Select
                .from(TestModel.class)
                .min("intField");

        assertEquals(1, min, 0);
    }

    @Test
    public void testMinWhereClause() {
        cleanTable();
        populateTable();

        float min = Select
                .from(TestModel.class)
                .where("intField > ?", 1)
                .min("intField");

        assertEquals(2, min, 0);
    }

    @Test
    public void testMinEmptyResult() {
        cleanTable();
        populateTable();

        float min = Select
                .from(TestModel.class)
                .where("intField > ?", 10)
                .min("intField");

        assertEquals(0, min, 0);
    }

    @Test
    public void testMinOrderBy() {
        cleanTable();
        populateTable();

        float min = Select
                .from(TestModel.class)
                .where("intField > ?", 1)
                .orderBy("intField ASC")
                .min("intField");

        //Should not change the result if order by is used.
        assertEquals(2, min, 0);
    }

    @Test
    public void testMinGroupBy() {
        cleanTable();
        populateTable();

        float min = Select
                .from(TestModel.class)
                .groupBy("stringField")
                .min("intField");

        //Should return minimal of the values which belong to the first category in selection
        //May seem weird, just test it in an SQL browser
        assertEquals(1, min, 0);
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
