package com.reactiveandroid.test.models;

import com.reactiveandroid.Model;
import com.reactiveandroid.annotation.Column;
import com.reactiveandroid.annotation.Table;
import com.reactiveandroid.test.databases.TestDatabase;

import java.util.ArrayList;
import java.util.List;

@Table(name = "Items", database = TestDatabase.class)
public class ItemTestModel extends Model {

    @Column(name = "Name")
    public String name;
    @Column(name = "Category")
    public CategoryTestModel category;

    public static List<ItemTestModel> createModelsWithCategory(CategoryTestModel category, int count) {
        ArrayList<ItemTestModel> models = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            ItemTestModel itemTestModel = new ItemTestModel();
            itemTestModel.name = "Item " + i;
            itemTestModel.category = category;
            models.add(itemTestModel);
        }
        return models;
    }

}