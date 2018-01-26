package com.reactiveandroid.test.models;

import com.reactiveandroid.Model;
import com.reactiveandroid.annotation.Column;
import com.reactiveandroid.annotation.Table;
import com.reactiveandroid.test.databases.TestDatabase;

@Table(name = "TestModel", database = TestDatabase.class)
public abstract class AbstractTestModel extends Model {

    @Column
    public String stringField;

}
