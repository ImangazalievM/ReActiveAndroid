package com.reactiveandroid.query.join;

import com.reactiveandroid.Model;
import com.reactiveandroid.annotation.Column;
import com.reactiveandroid.annotation.PrimaryKey;
import com.reactiveandroid.annotation.Table;
import com.reactiveandroid.test.TestDatabase;

@Table(database = TestDatabase.class)
public class CityModel extends Model {

    @PrimaryKey
    public Long id;
    @Column(name = "name")
    public String name;

    public CityModel(String name) {
        this.name = name;
    }

}