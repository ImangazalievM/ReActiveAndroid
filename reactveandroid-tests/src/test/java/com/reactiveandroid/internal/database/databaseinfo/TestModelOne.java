package com.reactiveandroid.internal.database.databaseinfo;

import com.reactiveandroid.Model;
import com.reactiveandroid.annotation.Column;
import com.reactiveandroid.annotation.PrimaryKey;
import com.reactiveandroid.annotation.Table;

@Table(database = MyDatabase.class)
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
