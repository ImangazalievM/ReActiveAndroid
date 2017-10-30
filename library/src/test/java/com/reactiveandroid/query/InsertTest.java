package com.reactiveandroid.query;

import com.reactiveandroid.test.BaseTest;
import com.reactiveandroid.test.models.TestModel;

import org.junit.Test;

import static com.reactiveandroid.test.TestUtils.assertSqlEquals;

public class InsertTest extends BaseTest {

    private static final String SELECT_PREFIX = "SELECT * FROM TestModel ";

    @Test
    public void testInsertInto() {
        assertSqlEquals("INSERT INTO TestModel", Insert.into(TestModel.class));
    }

    @Test
    public void testInsertColumns() {
        assertSqlEquals("INSERT INTO TestModel (a, b, c)", Insert.into(TestModel.class).columns("a", "b", "c"));
    }

    @Test
    public void testInsertColumnsValues() {
        String expectedSql = "INSERT INTO TestModel (a, b, c) VALUES(?, ?, ?)";
        assertSqlEquals(expectedSql, Insert.into(TestModel.class).columns("a", "b", "c").values("e", "f", "g"));
    }

    @Test(expected = Query.MalformedQueryException.class)
    public void testSelectDistinctColumns() {
        Insert.into(TestModel.class).columns("a", "b").values("e", "f", "g");
    }

}
