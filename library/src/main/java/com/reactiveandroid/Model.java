package com.reactiveandroid;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.reactiveandroid.internal.ModelAdapter;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.functions.Action;

@SuppressWarnings("unchecked")
public abstract class Model {

    private ModelAdapter modelAdapter;

    /**
     * Saves model to database
     */
    @NonNull
    public Long save() {
        return getModelAdapter().save(this);
    }

    public Single<Long> saveAsync() {
        return Single.fromCallable(new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                return save();
            }
        });
    }

    /**
     * Deletes model from database
     */
    public void delete() {
        getModelAdapter().delete(this);
    }

    @NonNull
    public Completable deleteAsync() {
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                delete();
            }
        });
    }

    /**
     * Load data from cursor into model
     *
     * @param cursor Cursor with data
     */
    public void loadFromCursor(Cursor cursor) {
        getModelAdapter().loadFromCursor(this, cursor);
    }

    /**
     * Load model from database by ID
     *
     * @returns Model with specified ID or null if model not found
     */
    @NonNull
    public static <TableClass> TableClass load(Class<TableClass> table, long id) {
        return (TableClass) ReActiveAndroid.getTableManager(table).load(table, id);
    }

    @NonNull
    public static <TableClass> Single<TableClass> loadAsync(final Class<TableClass> table, final long id) {
        return Single.fromCallable(new Callable<TableClass>() {
            @Override
            public TableClass call() throws Exception {
                return Model.load(table, id);
            }
        });
    }

    /**
     * Deletes model from database by ID
     */
    public static void delete(Class<?> table, long id) {
        ReActiveAndroid.getTableManager(table).delete(table, id);
    }

    @NonNull
    public static Completable deleteAsync(final Class<?> table, final long id) {
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                Model.delete(table, id);
            }
        });
    }

    public static <TableClass> void saveAll(Class<TableClass> table, List<TableClass> models) {
        ReActiveAndroid.getTableManager(table).saveAll(table, models);
    }

    @NonNull
    public static <TableClass> Completable saveAllAsync(final Class<TableClass> table, final List<TableClass> models) {
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                Model.saveAll(table, models);
            }
        });
    }

    public static <TableClass> void deleteAll(Class<TableClass> table, List<TableClass> models) {
        ReActiveAndroid.getTableManager(table).deleteAll(table, models);
    }

    @NonNull
    public static <TableClass> Completable deleteAllAsync(final Class<TableClass> table, final List<TableClass> models) {
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                Model.deleteAll(table, models);
            }
        });
    }

    private ModelAdapter getModelAdapter() {
        if (modelAdapter == null) {
            modelAdapter = ReActiveAndroid.getTableManager(getClass());
        }
        return modelAdapter;
    }

}
