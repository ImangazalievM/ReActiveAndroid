package com.reactiveandroid.test.models;

import com.reactiveandroid.Model;
import com.reactiveandroid.annotation.Column;
import com.reactiveandroid.annotation.Table;
import com.reactiveandroid.test.databases.TestDatabase;

@Table(database = TestDatabase.class)
public class JoinModelCustomer extends Model {

    @Column(name = "name")
    public String name;
    @Column(name = "city_id")
    public long cityId;

    public JoinModelCustomer(String name, long cityId) {
        this.name = name;
        this.cityId = cityId;
    }

}