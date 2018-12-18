package com.reactiveandroid.query.join;

import com.reactiveandroid.Model;
import com.reactiveandroid.annotation.Column;
import com.reactiveandroid.annotation.PrimaryKey;
import com.reactiveandroid.annotation.Table;
import com.reactiveandroid.test.TestDatabase;

@Table(database = TestDatabase.class)
public class OrderModel extends Model {

    @PrimaryKey
    public Long id;
    @Column(name = "name")
    public String name;
    @Column(name = "customer_id")
    public long customerId;

    public OrderModel(String name, long customerId) {
        this.name = name;
        this.customerId = customerId;
    }

}