package com.reactiveandroid.test.models;

import com.reactiveandroid.Model;
import com.reactiveandroid.annotation.Collate;
import com.reactiveandroid.annotation.Column;
import com.reactiveandroid.annotation.ConflictAction;
import com.reactiveandroid.annotation.IndexGroup;
import com.reactiveandroid.annotation.Table;
import com.reactiveandroid.annotation.UniqueGroup;
import com.reactiveandroid.test.databases.TestDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Table(database = TestDatabase.class, uniqueGroups = {@UniqueGroup(groupNumber = 1),
        @UniqueGroup(groupNumber = 2, onUniqueConflict = ConflictAction.ROLLBACK)},
        indexGroups = @IndexGroup(groupNumber = 1, name = "index_1"))
public class FullTestModel extends Model {

    @Column(uniqueGroups = 1)
    public Date dateField;

    @Column(unique = true, onUniqueConflict = ConflictAction.IGNORE, collate = Collate.BINARY,
            index = true)
    public String stringField;

    @Column(uniqueGroups = 1,
            index = true, indexGroups = 1)
    public double doubleField;

    @Column(uniqueGroups = 2,
            index = true, indexGroups = 1)
    public int intField;

    @Column(uniqueGroups = 2)
    public boolean booleanField;

    public int nonColumnField;

    public static List<FullTestModel> createEmptyModels(int count) {
        ArrayList<FullTestModel> models = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            models.add(new FullTestModel());
        }
        return models;
    }

    public static List<FullTestModel> createFilledModels(int count) {
        Date date = new Date();
        String stringField = "Lorem ipsum dolor ";
        int intField = 161496265;
        double doubleField = 1325.054;

        ArrayList<FullTestModel> models = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            FullTestModel model = new FullTestModel();
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
