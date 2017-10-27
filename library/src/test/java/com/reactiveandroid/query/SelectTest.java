package com.reactiveandroid.query;

import android.database.Cursor;

import com.reactiveandroid.test.BaseTest;
import com.reactiveandroid.test.TestUtils;
import com.reactiveandroid.test.models.JoinModel;
import com.reactiveandroid.test.models.JoinModel2;
import com.reactiveandroid.test.models.TestModel;

import org.junit.Test;

import java.util.List;

import static com.reactiveandroid.test.TestUtils.assertSqlEquals;
import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

public class SelectTest extends BaseTest {

    private static final String SELECT_PREFIX = "SELECT * FROM TestModel ";

    @Test
    public void testSelectEmpty() {
        assertSqlEquals("SELECT *", Select.columns());
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
        assertSqlEquals(SELECT_PREFIX + "LIMIT 10", from().limit("10"));
    }

    @Test
    public void testOffset() {
        assertSqlEquals(SELECT_PREFIX + "OFFSET 10", from().offset("10"));
    }

    @Test
    public void testLimitOffset() {
        assertSqlEquals(SELECT_PREFIX + "LIMIT 10 OFFSET 20", from().limit("10").offset("20"));
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
    public void testSingleJoin() {
        assertSqlEquals(SELECT_PREFIX + "JOIN JoinModel ON TestModel.id = JoinModel.id",
                from().join(JoinModel.class).on("TestModel.id = JoinModel.id"));

        assertSqlEquals(SELECT_PREFIX + "AS a JOIN JoinModel AS b ON a.id = b.id",
                from().as("a").join(JoinModel.class).as("b").on("a.id = b.id"));

        assertSqlEquals(SELECT_PREFIX + "JOIN JoinModel USING (id, other)",
                from().join(JoinModel.class).using("id", "other"));
    }

    @Test
    public void testJoins() {
        assertSqlEquals(SELECT_PREFIX + "JOIN JoinModel ON id JOIN JoinModel2 ON id",
                from().join(JoinModel.class).on("id")
                        .join(JoinModel2.class).on("id"));
    }

    @Test
    public void testJoinTypes() {
        assertSqlEquals(SELECT_PREFIX + "INNER JOIN JoinModel ON", from().innerJoin(JoinModel.class).on(""));
        assertSqlEquals(SELECT_PREFIX + "LEFT OUTER JOIN JoinModel ON", from().leftOuterJoin(JoinModel.class).on(""));
        assertSqlEquals(SELECT_PREFIX + "CROSS JOIN JoinModel ON", from().crossJoin(JoinModel.class).on(""));
    }

    @Test
    public void testGroupByHaving() {
        assertSqlEquals(SELECT_PREFIX + "GROUP BY id", from().groupBy("id"));
        assertSqlEquals(SELECT_PREFIX + "GROUP BY id HAVING id = 1", from().groupBy("id").having("id = 1"));
    }

    @Test
    public void testExecuteCursorOnSelectQuery() {
        List<TestModel> models = TestModel.createFilledModels(10);
        TestUtils.saveModels(models);

        Cursor cursor = Select.from(TestModel.class).fetchCursor();

        assertEquals(6, cursor.getColumnCount());
        assertEquals(10, cursor.getCount());
    }

    @Test
    public void testFetchCurorWithSpecifiedColumns() {
        List<TestModel> models = TestModel.createFilledModels(10);
        TestUtils.saveModels(models);

        Cursor cursor = Select.columns("id", "stringField").from(TestModel.class).fetchCursor();

        assertEquals(2, cursor.getColumnCount());
        assertEquals(10, cursor.getCount());
    }

    @Test
    public void testAllOperators() {
        final String expectedSql = SELECT_PREFIX + "AS a JOIN JoinModel USING (id) WHERE id > 5 GROUP BY id HAVING id < 10 LIMIT 5 OFFSET 10";

        // Try a few different orderings, shouldn't change the output
        assertSqlEquals(expectedSql, from()
                .as("a")
                .join(JoinModel.class)
                .using("id")
                .where("id > 5")
                .groupBy("id")
                .having("id < 10")
                .limit("5")
                .offset("10"));;
    }

    private Select.From from() {
        return Select.from(TestModel.class);
    }


}
