package com.reactiveandroid.relation.onetomany;

import com.reactiveandroid.Model;
import com.reactiveandroid.annotation.Column;
import com.reactiveandroid.annotation.OneToMany;
import com.reactiveandroid.annotation.PrimaryKey;
import com.reactiveandroid.annotation.Table;
import com.reactiveandroid.test.TestDatabase;

import java.util.List;

@Table(database = TestDatabase.class)
public class CategoryModel extends Model {

    @PrimaryKey
    private long id;
    @Column
    private String stringField;
    @OneToMany(foreignColumnName = "category_id")
    private List<TaskModel> tasks;

    public CategoryModel(long id) {
        this.id = id;
    }

    public CategoryModel() {
    }

    public long getId() {
        return id;
    }

    public List<TaskModel> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskModel> tasks) {
        this.tasks = tasks;
    }

}
