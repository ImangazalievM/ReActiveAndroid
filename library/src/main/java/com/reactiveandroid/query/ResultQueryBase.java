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

import com.reactiveandroid.Model;
import com.reactiveandroid.ReActiveAndroid;
import com.reactiveandroid.utils.QueryUtils;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Single;

public abstract class ResultQueryBase<T extends Model> extends QueryBase<T> {

    private boolean disableCacheForThisQuery = false;

    public void disableCaching() {
        this.disableCacheForThisQuery = true;
    }

    public ResultQueryBase(Query parent, Class<T> table) {
        super(parent, table);
    }

    public <CustomClass extends Model> List<CustomClass> fetchAs(Class<CustomClass> customType) {
        return QueryUtils.rawQuery(customType, getSql(), getArgs(), disableCacheForThisQuery);
    }

    public List<T> fetch() {
        return fetchAs(table);
    }

    public <CustomClass extends Model> CustomClass fetchSingleAs(Class<CustomClass> customType) {
        List<CustomClass> results = QueryUtils.rawQuery(customType, getSql(), getArgs(), disableCacheForThisQuery);
        if (!results.isEmpty()) {
            return results.get(0);
        }
        return null;
    }

    public T fetchSingle() {
        return fetchSingleAs(table);
    }

    public <E> E fetchValue(Class<E> type) {
        Cursor cursor = ReActiveAndroid.getWritableDatabaseForTable(table).rawQuery(getSql(), getArgs());
        if (!cursor.moveToFirst()) {
            return null;
        }

        E value = null;
        if (type.equals(Byte[].class) || type.equals(byte[].class)) {
            value = (E) cursor.getBlob(0);
        } else if (type.equals(double.class) || type.equals(Double.class)) {
            value = (E) Double.valueOf(cursor.getDouble(0));
        } else if (type.equals(float.class) || type.equals(Float.class)) {
            value = (E) Float.valueOf(cursor.getFloat(0));
        } else if (type.equals(int.class) || type.equals(Integer.class)) {
            value = (E) Integer.valueOf(cursor.getInt(0));
        } else if (type.equals(long.class) || type.equals(Long.class)) {
            value = (E) Long.valueOf(cursor.getLong(0));
        } else if (type.equals(short.class) || type.equals(Short.class)) {
            value = (E) Short.valueOf(cursor.getShort(0));
        } else if (type.equals(String.class)) {
            value = (E) cursor.getString(0);
        }

        cursor.close();
        return value;
    }

    public Cursor fetchCursor() {
        return ReActiveAndroid.getWritableDatabaseForTable(table).rawQuery(getSql(), getArgs());
    }

    public <CustomClass extends Model> Single<List<CustomClass>> fetchAsAsync(final Class<CustomClass> customType) {
        return Single.fromCallable(new Callable<List<CustomClass>>() {
            @Override
            public List<CustomClass> call() throws Exception {
                return fetchAs(customType);
            }
        });
    }

    public Single<List<T>> fetchAsync() {
        return fetchAsAsync(table);
    }

    public <CustomClass extends Model> Single<CustomClass> fetchSingleAsAsync(final Class<CustomClass> customType) {
        return Single.fromCallable(new Callable<CustomClass>() {
            @Override
            public CustomClass call() throws Exception {
                return fetchSingleAs(customType);
            }
        });
    }

    public Single<T> fetchSingleAsync() {
        return fetchSingleAsAsync(table);
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
