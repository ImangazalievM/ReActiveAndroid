package com.reactiveandroid;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.reactiveandroid.internal.ModelAdapter;
import com.reactiveandroid.internal.database.table.TableInfo;
import com.reactiveandroid.query.Delete;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.functions.Action;

@SuppressWarnings("unchecked")
public abstract class Model {

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
    @Nullable
    public static <TableClass> TableClass load(Class<TableClass> table, long id) {
        return (TableClass) getModelAdapter(table).load(table, id);
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
        TableInfo tableInfo = ReActiveAndroid.getTableInfo(table);
        Delete.from(tableInfo.getModelClass()).where(tableInfo.getPrimaryKeyColumnName() + "=?", id).execute();

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

    public static <ModelClass> void saveAll(Class<ModelClass> table, List<ModelClass> models) {
        if (models.isEmpty()) {
            return;
        }

        ModelAdapter modelAdapter = getModelAdapter(table);
        SQLiteDatabase sqliteDatabase = ReActiveAndroid.getWritableDatabaseForTable(table);
        try {
            sqliteDatabase.beginTransaction();
            for (ModelClass model : models) {
                modelAdapter.save(model);
            }
            sqliteDatabase.setTransactionSuccessful();
        } finally {
            sqliteDatabase.endTransaction();
        }
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
        if (models.isEmpty()) {
            return;
        }

        ModelAdapter modelAdapter = getModelAdapter(table);
        TableInfo tableInfo = modelAdapter.getTableInfo();
        SQLiteDatabase sqliteDatabase = ReActiveAndroid.getWritableDatabaseForTable(table);
        Long[] ids = new Long[models.size()];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = modelAdapter.getModelId(models.get(i));
        }

        String idsArg = TextUtils.join(", ", ids);
        sqliteDatabase.execSQL(String.format("DELETE FROM %s WHERE %s IN (%s);",
                tableInfo.getTableName(), tableInfo.getPrimaryKeyColumnName(), idsArg));
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

    @NonNull
    private static ModelAdapter getModelAdapter(Class<?> table) {
        return ReActiveAndroid.getModelAdapter(table);
    }

    private ModelAdapter modelAdapter;

    /**
     * Saves model to database
     */
    @NonNull
    public Long save() {
        return getModelAdapter().save(this);
    }

    @NonNull
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

    private ModelAdapter getModelAdapter() {
        if (modelAdapter == null) {
            modelAdapter = getModelAdapter(getClass());
        }
        return modelAdapter;
    }

}
