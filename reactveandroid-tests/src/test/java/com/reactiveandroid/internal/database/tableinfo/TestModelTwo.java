package com.reactiveandroid.internal.database.tableinfo;

import com.reactiveandroid.Model;
import com.reactiveandroid.annotation.Column;
import com.reactiveandroid.annotation.PrimaryKey;
import com.reactiveandroid.annotation.Table;
import com.reactiveandroid.test.TestDatabase;

@Table(name = "MyCustomName", database = TestDatabase.class)
public class TestModelTwo extends Model {

    @PrimaryKey(name = "cat_id")
    public Long id;
    @Column
    public String name;
    @Column(name = "sort_order")
    public int sortOrder;
    @Column(name = "visible")
    public boolean isVisible;

}
