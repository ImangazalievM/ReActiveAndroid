package com.reactiveandroid.test.models;

import com.reactiveandroid.Model;
import com.reactiveandroid.annotation.Column;
import com.reactiveandroid.annotation.PrimaryKey;
import com.reactiveandroid.annotation.Table;
import com.reactiveandroid.test.databases.TestDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Table(name = "BenchmarkModel", database = TestDatabase.class)
public class BenchmarkModel extends Model {

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

    public static List<BenchmarkModel> createEmptyModels(int count) {
        ArrayList<BenchmarkModel> models = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            models.add(new BenchmarkModel());
        }
        return models;
    }

    public static List<BenchmarkModel> createFilledModels(int count) {
        Date date = new Date();
        String stringField = "Lorem ipsum dolor ";
        int intField = 161496265;
        double doubleField = 1325.054;

        ArrayList<BenchmarkModel> models = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            BenchmarkModel model = new BenchmarkModel();
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
