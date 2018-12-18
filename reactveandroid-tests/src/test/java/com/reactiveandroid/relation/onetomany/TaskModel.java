package com.reactiveandroid.relation.onetomany;

import com.reactiveandroid.Model;
import com.reactiveandroid.annotation.Column;
import com.reactiveandroid.annotation.PrimaryKey;
import com.reactiveandroid.annotation.Table;
import com.reactiveandroid.test.TestDatabase;

@Table(database = TestDatabase.class)
public class TaskModel extends Model {

    @PrimaryKey
    private long id;
    @Column(name = "category_id")
    private Long categoryId;

    public TaskModel() {
    }

    public TaskModel(Long id, Long categoryId) {
        this.id = id;
        this.categoryId = categoryId;
    }

    public long getId() {
        return id;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long model) {
        this.categoryId = model;
    }

}
