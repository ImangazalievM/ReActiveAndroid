package com.reactiveandroid.test.models;

import com.reactiveandroid.annotation.QueryColumn;
import com.reactiveandroid.annotation.QueryModel;
import com.reactiveandroid.test.databases.TestDatabase;

@QueryModel(database = TestDatabase.class)
public class OrderInfoQueryModel {

    @QueryColumn(name = "order_id")
    public long orderId;
    @QueryColumn(name = "order_name")
    public String orderName;
    @QueryColumn(name = "customer_name")
    public String customerName;
    @QueryColumn(name = "city_name")
    public String cityName;

}