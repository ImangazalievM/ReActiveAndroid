package com.reactiveandroid.test.models;

import com.reactiveandroid.Model;
import com.reactiveandroid.annotation.Column;
import com.reactiveandroid.annotation.PrimaryKey;
import com.reactiveandroid.annotation.Table;
import com.reactiveandroid.test.databases.TestDatabase;

@Table(database = TestDatabase.class)
public class OneToManyRelationModel extends Model {

    @PrimaryKey
    private long id;
    @Column(name = "model")
    private Long foreignTableModelId;

    public OneToManyRelationModel() {
    }

    public OneToManyRelationModel(Long id, Long foreignTableModelId) {
        this.id = id;
        this.foreignTableModelId = foreignTableModelId;
    }

    public long getId() {
        return id;
    }

    public long getForeignTableModelId() {
        return foreignTableModelId;
    }

    public void setForeignTableModelId(Long model) {
        this.foreignTableModelId = model;
    }

}
