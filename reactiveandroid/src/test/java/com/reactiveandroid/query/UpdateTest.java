package com.reactiveandroid.query;

import com.reactiveandroid.test.BaseTest;
import com.reactiveandroid.test.models.TestModel;

import org.junit.Test;

import static com.reactiveandroid.test.TestUtils.assertSqlEquals;
import static org.junit.Assert.assertArrayEquals;

public class UpdateTest extends BaseTest {

    private static final String UPDATE_PREFIX = "UPDATE TestModel";

    @Test
    public void testUpdate() {
        assertSqlEquals(UPDATE_PREFIX, update());
    }

    @Test
    public void testUpdateSet() {
        assertSqlEquals(UPDATE_PREFIX + " SET id = 5", update().set("id = 5"));
    }

    @Test
    public void testUpdateWhereNoArguments() {
        assertSqlEquals(UPDATE_PREFIX + " SET id = 5 WHERE id = 1", update().set("id = 5").where("id = 1"));
    }

    @Test
    public void testUpdateWhereWithArguments() {
        Update.Where set = update()
                .set("id = 5")
                .where("id = ?", 1);

        assertArrayEquals(set.getArgs(), new String[]{"1"});
        assertSqlEquals(UPDATE_PREFIX + " SET id = 5 WHERE id = ?", set);

        set = update()
                .set("id = 5")
                .where("id IN (?, ?, ?)", 5, 4, 3);

        assertArrayEquals(set.getArgs(), new String[]{"5", "4", "3"});
        assertSqlEquals(UPDATE_PREFIX + " SET id = 5 WHERE id IN (?, ?, ?)", set);
    }

    private Update.Table update() {
        return Update.table(TestModel.class);
    }

}
