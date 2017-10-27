package com.reactiveandroid.query;

import com.reactiveandroid.Model;
import com.reactiveandroid.ReActiveAndroid;

public abstract class ExecutableQueryBase<T extends Model> extends QueryBase<T> {

    public ExecutableQueryBase(Query parent, Class<T> table) {
        super(parent, table);
    }

    public void execute() {
        ReActiveAndroid.getWritableDatabaseForTable(table).execSQL(getSql(), getArgs());
    }

}
