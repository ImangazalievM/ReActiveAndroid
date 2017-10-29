package com.reactiveandroid.query;

import com.reactiveandroid.Model;
import com.reactiveandroid.ReActiveAndroid;

import io.reactivex.Completable;
import io.reactivex.functions.Action;

public abstract class ExecutableQueryBase<T extends Model> extends QueryBase<T> {

    public ExecutableQueryBase(Query parent, Class<T> table) {
        super(parent, table);
    }

    public void execute() {
        ReActiveAndroid.getWritableDatabaseForTable(table).execSQL(getSql(), getArgs());
    }

    public Completable executeAsync() {
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                execute();
            }
        });
    }

}
