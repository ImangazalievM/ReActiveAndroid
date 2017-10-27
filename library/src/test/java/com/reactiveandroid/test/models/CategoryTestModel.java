package com.reactiveandroid.test.models;

import com.reactiveandroid.Model;
import com.reactiveandroid.annotation.Column;
import com.reactiveandroid.annotation.Table;
import com.reactiveandroid.test.databases.TestDatabase;

import java.util.List;

@Table(name = "Categories", database = TestDatabase.class, idName = "cat_id")
public class CategoryTestModel extends Model {

    @Column
    public String name;

    @Column(name = "sort_order")
    public int sortOrder;

    @Column(name = "visible")
    public boolean isVisible;

    public List<ItemTestModel> getItems() {
        return getMany(ItemTestModel.class, "Category");
    }

}
