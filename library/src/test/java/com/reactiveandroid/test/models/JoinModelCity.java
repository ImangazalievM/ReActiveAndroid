package com.reactiveandroid.test.models;

import com.reactiveandroid.Model;
import com.reactiveandroid.annotation.Column;
import com.reactiveandroid.annotation.Table;
import com.reactiveandroid.test.databases.TestDatabase;

@Table(database = TestDatabase.class)
public class JoinModelCity extends Model {

    @Column(name = "name")
    public String name;

    public JoinModelCity(String name) {
        this.name = name;
    }

}