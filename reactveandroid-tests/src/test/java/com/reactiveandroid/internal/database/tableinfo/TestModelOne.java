package com.reactiveandroid.internal.database.tableinfo;

import com.reactiveandroid.Model;
import com.reactiveandroid.annotation.Column;
import com.reactiveandroid.annotation.PrimaryKey;
import com.reactiveandroid.annotation.Table;
import com.reactiveandroid.test.TestDatabase;

@Table(database = TestDatabase.class, createWithDatabase = false)
public class TestModelOne extends Model {

    @PrimaryKey
    public Long id;
    @Column
    public String stringField;
    @Column
    public double doubleField;
    @Column
    public int intField;
    @Column
    public boolean booleanField;

    public int nonColumnField;

}
