package com.reactiveandroid.internal.database.databaseinfo;

import com.reactiveandroid.Model;
import com.reactiveandroid.annotation.Column;
import com.reactiveandroid.annotation.PrimaryKey;
import com.reactiveandroid.annotation.Table;

@Table(name = "MyCustomName", database = MyDatabase.class)
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
