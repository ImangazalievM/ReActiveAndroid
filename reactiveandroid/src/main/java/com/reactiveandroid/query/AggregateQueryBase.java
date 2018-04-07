package com.reactiveandroid.query;

import android.database.Cursor;

import com.reactiveandroid.ReActiveAndroid;

import java.util.concurrent.Callable;

import io.reactivex.Single;

/**
 *
 */
public abstract class AggregateQueryBase<TableClass> extends ResultQueryBase<TableClass> {

    public AggregateQueryBase(Query parent, Class<TableClass> table) {
        super(parent, table);
    }

    public int count() {
        return getAsInt(getAggregateFunctionSql("COUNT(*)"));
    }

    public float avg(String columnName) {
        return getAsFloat(getAggregateFunctionSql("AVG(" + columnName + ")"));
    }

    public float min(String columnName) {
        return getAsFloat(getAggregateFunctionSql("MIN(" + columnName + ")"));
    }

    public float max(String columnName) {
        return getAsFloat(getAggregateFunctionSql("MAX(" + columnName + ")"));
    }

    public float sum(String columnName) {
        return getAsFloat(getAggregateFunctionSql("SUM(" + columnName + ")"));
    }

    public float total(String columnName) {
        return getAsFloat(getAggregateFunctionSql("TOTAL(" + columnName + ")"));
    }

    public String groupConcat(String columnName) {
        return getAsString(getAggregateFunctionSql("GROUP_CONCAT(" + columnName + ")"));
    }

    public Single<Integer> countAsync() {
        return Single.fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return count();
            }
        });
    }

    public Single<Float> avgAsync(final String columnName) {
        return Single.fromCallable(new Callable<Float>() {
            @Override
            public Float call() throws Exception {
                return avg(columnName);
            }
        });
    }

    public Single<Float> minAsync(final String columnName) {
        return Single.fromCallable(new Callable<Float>() {
            @Override
            public Float call() throws Exception {
                return min(columnName);
            }
        });
    }

    public Single<Float> maxAsync(final String columnName) {
        return Single.fromCallable(new Callable<Float>() {
            @Override
            public Float call() throws Exception {
                return max(columnName);
            }
        });
    }

    public Single<Float> sumAsync(final String columnName) {
        return Single.fromCallable(new Callable<Float>() {
            @Override
            public Float call() throws Exception {
                return sum(columnName);
            }
        });
    }

    public Single<Float> totalAsync(final String columnName) {
        return Single.fromCallable(new Callable<Float>() {
            @Override
            public Float call() throws Exception {
                return total(columnName);
            }
        });
    }

    public Single<String> groupConcatAsync(final String columnName) {
        return Single.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return groupConcat(columnName);
            }
        });
    }

    private String getAggregateFunctionSql(String aggregateFunction) {
        String originalSql = getSql();
        int fromIndex = originalSql.indexOf(" FROM");
        return "SELECT " + aggregateFunction + originalSql.substring(fromIndex);
    }

    private int getAsInt(String sql) {
        int result;
        Cursor cursor = ReActiveAndroid.getWritableDatabaseForTable(table).rawQuery(sql, getArgs());
        if (!cursor.moveToFirst()) {
            result = 0;
        } else {
            result = cursor.getInt(0);
        }
        cursor.close();
        return result;
    }

    private float getAsFloat(String sql) {
        float result;
        Cursor cursor = ReActiveAndroid.getWritableDatabaseForTable(table).rawQuery(sql, getArgs());
        if (!cursor.moveToFirst()) {
            result = 0f;
        } else {
            result = cursor.getFloat(0);
        }
        cursor.close();
        return result;
    }

    private String getAsString(String sql) {
        String result;
        Cursor cursor = ReActiveAndroid.getWritableDatabaseForTable(table).rawQuery(sql, getArgs());
        if (!cursor.moveToFirst()) {
            result = null;
        } else {
            result = cursor.getString(0);
        }
        cursor.close();
        return result;
    }

}
