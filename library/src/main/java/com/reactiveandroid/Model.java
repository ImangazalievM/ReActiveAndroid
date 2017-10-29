package com.reactiveandroid;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.reactiveandroid.query.Select;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.functions.Action;

@SuppressWarnings("unchecked")
public abstract class Model {

    Long id = null;
    private TableManager tableManager;

    /**
     * @return Model ID
     */
    @Nullable
    public Long getId() {
        return id;
    }

    /**
     * Saves model to database
     */
    @NonNull
    public Long save() {
        getTableManager().save(this);
        return id;
    }

    public Single<Long> saveAsync() {
        return Single.fromCallable(new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                save();
                return id;
            }
        });
    }

    /**
     * Deletes model from database
     */
    public void delete() {
        getTableManager().delete(this);
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
        getTableManager().loadFromCursor(this, cursor);
    }

    /**
     * Load model from database by ID
     *
     * @returns Model with specified ID or null if model not found
     */
    @NonNull
    public static <TableClass extends Model> TableClass load(Class<TableClass> table, long id) {
        return (TableClass) ReActiveAndroid.getTableManager(table).load(table, id);
    }

    @NonNull
    public static <TableClass extends Model> Single<TableClass> loadAsync(final Class<TableClass> table, final long id) {
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
    public static void delete(Class<? extends Model> table, long id) {
        ReActiveAndroid.getTableManager(table).delete(table, id);
    }

    @NonNull
    public static Completable deleteAsync(final Class<? extends Model> table, final long id) {
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                Model.delete(table, id);
            }
        });
    }

    public static <TableClass extends Model> void saveAll(Class<TableClass> table, List<TableClass> models) {
        ReActiveAndroid.getTableManager(table).saveAll(table, models);
    }

    @NonNull
    public static <TableClass extends Model> Completable  saveAllAsync(final Class<TableClass> table, final List<TableClass> models) {
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                Model.saveAll(table, models);
            }
        });
    }

    public static <TableClass extends Model> void deleteAll(Class<TableClass> table, List<TableClass> models) {
        ReActiveAndroid.getTableManager(table).deleteAll(table, models);
    }

    @NonNull
    public static <TableClass extends Model> Completable deleteAllAsync(final Class<TableClass> table, final List<TableClass> models) {
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                Model.deleteAll(table, models);
            }
        });
    }

    @NonNull
    protected <TableClass extends Model> List<TableClass> getMany(Class<TableClass> table, String foreignKey) {
        if (getId() != null) {
            return Select
                    .from(table)
                    .where(ReActiveAndroid.getTableName(table) + "." + foreignKey + "=?", getId())
                    .fetch();
        } else {
            return new ArrayList<>();
        }
    }

    private TableManager getTableManager() {
        if (tableManager == null) {
            tableManager = ReActiveAndroid.getTableManager(getClass());
        }
        return tableManager;
    }

}
