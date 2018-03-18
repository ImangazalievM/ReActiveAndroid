package com.reactiveandroid.query.crud;

import android.database.Cursor;

import com.reactiveandroid.query.Select;
import com.reactiveandroid.query.join.CustomerModel;
import com.reactiveandroid.test.BaseTest;
import com.reactiveandroid.test.TestUtils;
import com.reactiveandroid.test.TestModel;

import org.junit.Test;

import java.util.List;

import static com.reactiveandroid.test.TestUtils.assertSqlEquals;
import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

public class SelectTest extends BaseTest {

    private static final String SELECT_PREFIX = "SELECT * FROM TestModel ";

    @Test
    public void testSelectEmpty() {
        TestUtils.assertSqlEquals("SELECT *", Select.columns());
    }

    @Test
    public void testSelectAllAndDistinct() {
        assertSqlEquals("SELECT ALL", Select.all());
        assertSqlEquals("SELECT DISTINCT", Select.distinct());
    }

    @Test
    public void testSelectStringColumns() {
        assertSqlEquals("SELECT a, b, c", Select.columns("a", "b", "c"));
    }

    @Test
    public void testSelectDistinctColumns() {
        assertSqlEquals("SELECT DISTINCT a, b, c", Select.distinct().columns("a", "b", "c"));
    }

    @Test
    public void testFrom() {
        assertSqlEquals("SELECT * FROM TestModel", Select.from(TestModel.class));
    }

    @Test
    public void testAllDistinct() {
        assertSqlEquals("SELECT ALL" , Select.all());
        assertSqlEquals("SELECT DISTINCT", Select.distinct());
    }

    @Test
    public void testLimit() {
        assertSqlEquals(SELECT_PREFIX + "LIMIT 10", from().limit(10));
    }

    @Test
    public void testOffset() {
        assertSqlEquals(SELECT_PREFIX + "OFFSET 10", from().offset(10));
    }

    @Test
    public void testLimitOffset() {
        assertSqlEquals(SELECT_PREFIX + "LIMIT 10 OFFSET 20", from().limit(10).offset(20));
    }

    @Test
    public void testAs() {
        assertSqlEquals(SELECT_PREFIX + "AS a", from().as("a"));
    }

    @Test
    public void testOrderBy() {
        assertSqlEquals(SELECT_PREFIX + "ORDER BY id DESC", from().orderBy("id DESC"));
    }

    @Test
    public void testWhereNoArguments() {
        assertSqlEquals(SELECT_PREFIX + "WHERE id = 5", from().where("id = 5"));
    }

    @Test
    public void testWhereWithArguments() {
        Select.Where query = from().where("id = ?", 5);
        assertArrayEquals(query.getArgs(), new String[]{"5"});
        assertSqlEquals(SELECT_PREFIX + "WHERE id = ?", query);

        query = from().where("id > ? AND id < ?", 5, 10);
        assertArrayEquals(query.getArgs(), new String[]{"5", "10"});
        assertSqlEquals(SELECT_PREFIX + "WHERE id > ? AND id < ?", query);
    }

    @Test
    public void testGroupByHaving() {
        assertSqlEquals(SELECT_PREFIX + "GROUP BY id", from().groupBy("id"));
        assertSqlEquals(SELECT_PREFIX + "GROUP BY id HAVING id = 1", from().groupBy("id").having("id = 1"));
    }

    @Test
    public void testAllOperators() {
        final String expectedSql = SELECT_PREFIX + "AS a JOIN CustomerModel USING (id) WHERE id > 5 GROUP BY id HAVING id < 10 LIMIT 5 OFFSET 10";

        // Try a few different orderings, shouldn't change the output
        assertSqlEquals(expectedSql, from()
                .as("a")
                .join(CustomerModel.class)
                .using("id")
                .where("id > 5")
                .groupBy("id")
                .having("id < 10")
                .limit(5)
                .offset(10));;
    }

    @Test
    public void testFetchCursor() {
        List<TestModel> models = TestModel.createFilledModels(10);
        TestUtils.saveModels(models);

        Cursor cursor = Select.from(TestModel.class).fetchCursor();

        assertEquals(6, cursor.getColumnCount());
        assertEquals(10, cursor.getCount());
    }

    @Test
    public void testFetchCursorWithSpecifiedColumns() {
        List<TestModel> models = TestModel.createFilledModels(10);
        TestUtils.saveModels(models);

        Cursor cursor = Select.columns("id", "stringField").from(TestModel.class).fetchCursor();

        assertEquals(2, cursor.getColumnCount());
        assertEquals(10, cursor.getCount());
    }

    @Test
    public void testFetch() {
        for (int i = 0; i < 10; i++) {
            TestModel model = new TestModel();
            model.stringField = "Random text";
            model.save();
        }

        List<TestModel> resultModels = Select.from(TestModel.class).fetch();

        assertEquals(10, resultModels.size());
    }

    @Test
    public void testFetchSelectSingle() {
        TestModel model = new TestModel();
        model.stringField = "Random text";
        model.save();

        TestModel resultModel = Select.from(TestModel.class).fetchSingle();

        assertEquals(model.id, resultModel.id);
        assertEquals(model.stringField, resultModel.stringField);
    }

    private Select.From from() {
        return Select.from(TestModel.class);
    }


}
