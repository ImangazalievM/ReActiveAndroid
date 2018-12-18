package com.reactiveandroid.query.join;

import com.reactiveandroid.Model;
import com.reactiveandroid.annotation.Column;
import com.reactiveandroid.annotation.PrimaryKey;
import com.reactiveandroid.annotation.Table;
import com.reactiveandroid.test.TestDatabase;

@Table(database = TestDatabase.class)
public class CustomerModel extends Model {

    @PrimaryKey
    public Long id;
    @Column(name = "name")
    public String name;
    @Column(name = "city_id")
    public long cityId;

    public CustomerModel(String name, long cityId) {
        this.name = name;
        this.cityId = cityId;
    }

}