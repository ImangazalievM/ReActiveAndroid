package com.reactiveandroid.query;

import android.database.Cursor;

import com.reactiveandroid.ReActiveAndroid;
import com.reactiveandroid.internal.utils.QueryUtils;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Single;

public abstract class ResultQueryBase<TableClass> extends QueryBase<TableClass> {

    private boolean disableCacheForThisQuery = false;

    public void disableCaching() {
        this.disableCacheForThisQuery = true;
    }

    public ResultQueryBase(Query parent, Class<TableClass> table) {
        super(parent, table);
    }

    public List<TableClass> fetch() {
        return QueryUtils.rawQuery(table, getSql(), getArgs(), disableCacheForThisQuery);
    }

    public TableClass fetchSingle() {
        List<TableClass> results = QueryUtils.rawQuery(table, getSingleSql(), getArgs(), disableCacheForThisQuery);
        if (!results.isEmpty()) {
            return results.get(0);
        }
        return null;
    }

    public <CustomClass> List<CustomClass> fetchCustom(Class<CustomClass> customType) {
        return QueryUtils.rawQueryCustom(customType, getSql(), getArgs());
    }

    public <CustomClass> CustomClass fetchCustomSingle(Class<CustomClass> customType) {
        List<CustomClass> results = QueryUtils.rawQueryCustom(customType, getSingleSql(), getArgs());
        if (!results.isEmpty()) {
            return results.get(0);
        }
        return null;
    }

    public Cursor fetchCursor() {
        return ReActiveAndroid.getWritableDatabaseForTable(table).rawQuery(getSql(), getArgs());
    }

    public Single<List<TableClass>> fetchAsync() {
        return Single.fromCallable(new Callable<List<TableClass>>() {
            @Override
            public List<TableClass> call() throws Exception {
                return fetch();
            }
        });
    }

    public <CustomClass> Single<List<CustomClass>> fetchCustomAsync(final Class<CustomClass> customType) {
        return Single.fromCallable(new Callable<List<CustomClass>>() {
            @Override
            public List<CustomClass> call() throws Exception {
                return fetchCustom(customType);
            }
        });
    }

    public Single<TableClass> fetchSingleAsync() {
        return Single.fromCallable(new Callable<TableClass>() {
            @Override
            public TableClass call() throws Exception {
                return fetchSingle();
            }
        });
    }

    public <CustomClass> Single<CustomClass> fetchCustomSingleAsync(final Class<CustomClass> customType) {
        return Single.fromCallable(new Callable<CustomClass>() {
            @Override
            public CustomClass call() throws Exception {
                return fetchCustomSingle(customType);
            }
        });
    }

    public Single<Cursor> fetchCursorAsync() {
        return Single.fromCallable(new Callable<Cursor>() {
            @Override
            public Cursor call() throws Exception {
                return fetchCursor();
            }
        });
    }

    //sets LIMIT 1 to optimize queries with single result
    private String getSingleSql() {
        String originalSql = getSql();
        if (originalSql.contains("LIMIT")) {
            return getSql().replaceAll("LIMIT [0-9]*]", "LIMIT 1");
        } else {
            return originalSql + " LIMIT 1";
        }
    }

}
