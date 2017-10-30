package com.reactiveandroid.test.models;

import com.reactiveandroid.Model;
import com.reactiveandroid.annotation.Column;
import com.reactiveandroid.annotation.Table;
import com.reactiveandroid.test.databases.TestDatabase;

@Table(database = TestDatabase.class)
public class JoinModelOrder extends Model {

    @Column(name = "name")
    public String name;
    @Column(name = "customer_id")
    public long customerId;

    public JoinModelOrder(String name, long customerId) {
        this.name = name;
        this.customerId = customerId;
    }

}