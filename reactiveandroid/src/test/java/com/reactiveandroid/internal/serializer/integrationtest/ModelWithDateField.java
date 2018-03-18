package com.reactiveandroid.internal.serializer.integrationtest;

import com.reactiveandroid.Model;
import com.reactiveandroid.annotation.Column;
import com.reactiveandroid.annotation.PrimaryKey;
import com.reactiveandroid.annotation.Table;

import java.util.Date;

@Table(database = MyDatabase.class)
public class ModelWithDateField extends Model {

    @PrimaryKey
    public Long id;
    @Column
    public Date dateField;

    public ModelWithDateField() {
    }

    public ModelWithDateField(Date dateField) {
        this.dateField = dateField;
    }

}
