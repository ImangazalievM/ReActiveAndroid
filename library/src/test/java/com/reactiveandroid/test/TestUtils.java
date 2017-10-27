package com.reactiveandroid.test;

import android.database.sqlite.SQLiteDatabase;

import com.reactiveandroid.Model;
import com.reactiveandroid.ReActiveAndroid;
import com.reactiveandroid.Query;

import org.robolectric.RuntimeEnvironment;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestUtils {

    public static TestApp getApplication() {
        return (TestApp) RuntimeEnvironment.application;
    }

    public static void assertSqlEquals(String expected, com.reactiveandroid.query.Query actual) {
        assertEquals(expected, actual.getSql());
    }

    public static void assertSqlEquals(String expected, Query actual) {
        assertEquals(expected, actual.getSql());
    }

    public static void assertSqlEquals(Query expected, Query actual) {
        assertEquals(expected.getSql(), actual.getSql());
    }

    public static void saveModels(List<? extends Model> models) {
        Class<?> table = models.get(0).getClass();
        SQLiteDatabase sqliteDatabase = ReActiveAndroid.getWritableDatabaseForTable(table);
        try {
            sqliteDatabase.beginTransaction();
            for (Model model : models) {
                model.save();
            }
            sqliteDatabase.setTransactionSuccessful();
        } finally {
            sqliteDatabase.endTransaction();
        }
    }

    public static void testNotImplemented() {
        throw new RuntimeException("Test not implemented");
    }

}
