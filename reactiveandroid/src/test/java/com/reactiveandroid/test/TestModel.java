package com.reactiveandroid.test;

import com.reactiveandroid.Model;
import com.reactiveandroid.annotation.Column;
import com.reactiveandroid.annotation.PrimaryKey;
import com.reactiveandroid.annotation.Table;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Table(database = TestDatabase.class)
public class TestModel extends Model {

    @PrimaryKey
    public Long id;
    @Column
    public Date dateField;
    @Column
    public String stringField;
    @Column
    public double doubleField;
    @Column
    public int intField;
    @Column
    public boolean booleanField;

    public int nonColumnField;

    public TestModel() {
    }

    public TestModel(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "TestModelOne{" +
                "id=" + id +
                ", dateField=" + dateField +
                ", stringField='" + stringField + '\'' +
                ", doubleField=" + doubleField +
                ", intField=" + intField +
                ", booleanField=" + booleanField +
                ", nonColumnField=" + nonColumnField +
                '}';
    }

    public static List<TestModel> createEmptyModels(int count) {
        ArrayList<TestModel> models = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            models.add(new TestModel());
        }
        return models;
    }

    public static List<TestModel> createFilledModels(int count) {
        Date date = new Date();
        String stringField = "Lorem ipsum dolor ";
        int intField = 161496265;
        double doubleField = 1325.054;

        ArrayList<TestModel> models = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            TestModel model = new TestModel();
            model.dateField = date;
            model.stringField = stringField + i;
            model.doubleField = doubleField + i;
            model.intField = intField + i;
            model.booleanField = i % 2 == 0;
            models.add(model);
        }

        return models;
    }

}
