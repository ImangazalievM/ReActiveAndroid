package com.reactiveandroid.internal.database.databaseinfo;

import com.reactiveandroid.Model;
import com.reactiveandroid.annotation.Column;
import com.reactiveandroid.annotation.Table;
import com.reactiveandroid.test.TestDatabase;

@Table(database = TestDatabase.class)
public abstract class AbstractModel extends Model {

    @Column
    public String stringField;

}
