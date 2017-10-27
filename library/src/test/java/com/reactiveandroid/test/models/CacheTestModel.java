package com.reactiveandroid.test.models;

import com.reactiveandroid.Model;
import com.reactiveandroid.annotation.Column;
import com.reactiveandroid.annotation.Table;
import com.reactiveandroid.test.databases.TestDatabase;

@Table(name = "CacheTestModel", database = TestDatabase.class)
public class CacheTestModel extends Model {

    @Column
    public int intField;

}