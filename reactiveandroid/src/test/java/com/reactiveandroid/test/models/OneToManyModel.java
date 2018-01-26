package com.reactiveandroid.test.models;

import com.reactiveandroid.Model;
import com.reactiveandroid.annotation.Column;
import com.reactiveandroid.annotation.OneToMany;
import com.reactiveandroid.annotation.PrimaryKey;
import com.reactiveandroid.annotation.Table;
import com.reactiveandroid.test.databases.TestDatabase;

import java.util.List;

@Table(database = TestDatabase.class)
public class OneToManyModel extends Model {

    @PrimaryKey
    private long id;
    @Column
    private String stringField;
    @OneToMany(foreignColumnName = "model")
    private List<OneToManyRelationModel> models;

    public OneToManyModel(long id) {
        this.id = id;
    }

    public OneToManyModel() {
    }

    public long getId() {
        return id;
    }

    public List<OneToManyRelationModel> getModels() {
        return models;
    }

    public void setModels(List<OneToManyRelationModel> models) {
        this.models = models;
    }

}
