/*
 * Copyright (C) 2014 Michael Pardo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.reactiveandroid.query;

import android.database.Cursor;

import com.reactiveandroid.QueryModelManager;
import com.reactiveandroid.ReActiveAndroid;
import com.reactiveandroid.database.DatabaseInfo;
import com.reactiveandroid.utils.QueryUtils;

import java.util.ArrayList;
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
        List<TableClass> results = QueryUtils.rawQuery(table, getSql(), getArgs(), disableCacheForThisQuery);
        if (!results.isEmpty()) {
            return results.get(0);
        }
        return null;
    }

    public <CustomClass> List<CustomClass> fetchAs(Class<CustomClass> customType) {
        DatabaseInfo databaseInfo = ReActiveAndroid.getDatabaseForTable(customType);
        QueryModelManager<CustomClass> queryModelManager = databaseInfo.getQueryModelManager(customType);
        Cursor cursor = databaseInfo.getWritableDatabase().rawQuery(getSql(), getArgs());
        cursor.moveToFirst();
        List<CustomClass> entities = new ArrayList<>();
        do {
            entities.add(queryModelManager.createFromCursor(cursor));
        } while (cursor.moveToNext());
        return entities;
    }

    public <CustomClass> CustomClass fetchSingleAs(Class<CustomClass> modelType) {
        List<CustomClass> results = fetchAs(modelType);
        if (!results.isEmpty()) {
            return results.get(0);
        }
        return null;
    }

    public <ValueType> ValueType fetchValue(Class<ValueType> type) {
        Cursor cursor = ReActiveAndroid.getWritableDatabaseForTable(table).rawQuery(getSql(), getArgs());
        if (!cursor.moveToFirst()) {
            return null;
        }

        ValueType value = null;
        if (type.equals(Byte[].class) || type.equals(byte[].class)) {
            value = (ValueType) cursor.getBlob(0);
        } else if (type.equals(double.class) || type.equals(Double.class)) {
            value = (ValueType) Double.valueOf(cursor.getDouble(0));
        } else if (type.equals(float.class) || type.equals(Float.class)) {
            value = (ValueType) Float.valueOf(cursor.getFloat(0));
        } else if (type.equals(int.class) || type.equals(Integer.class)) {
            value = (ValueType) Integer.valueOf(cursor.getInt(0));
        } else if (type.equals(long.class) || type.equals(Long.class)) {
            value = (ValueType) Long.valueOf(cursor.getLong(0));
        } else if (type.equals(short.class) || type.equals(Short.class)) {
            value = (ValueType) Short.valueOf(cursor.getShort(0));
        } else if (type.equals(String.class)) {
            value = (ValueType) cursor.getString(0);
        }

        cursor.close();
        return value;
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

    public <CustomClass> Single<List<CustomClass>> fetchAsAsync(final Class<CustomClass> customType) {
        return Single.fromCallable(new Callable<List<CustomClass>>() {
            @Override
            public List<CustomClass> call() throws Exception {
                return fetchAs(customType);
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

    public <CustomClass> Single<CustomClass> fetchSingleAsAsync(final Class<CustomClass> customType) {
        return Single.fromCallable(new Callable<CustomClass>() {
            @Override
            public CustomClass call() throws Exception {
                return fetchSingleAs(customType);
            }
        });
    }

    public <E> Single<E> fetchValueAsync(final Class<E> type) {
        return Single.fromCallable(new Callable<E>() {
            @Override
            public E call() throws Exception {
                return fetchValue(type);
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

}
