package com.reactiveandroid.test.models;

import com.reactiveandroid.annotation.Column;
import com.reactiveandroid.annotation.PrimaryKey;
import com.reactiveandroid.annotation.QueryModel;
import com.reactiveandroid.test.databases.TestDatabase;

@QueryModel(database = TestDatabase.class)
public class OrderInfoQueryModel {

    @PrimaryKey
    public Long id;
    @Column(name = "order_id")
    public long orderId;
    @Column(name = "order_name")
    public String orderName;
    @Column(name = "customer_name")
    public String customerName;
    @Column(name = "city_name")
    public String cityName;

}