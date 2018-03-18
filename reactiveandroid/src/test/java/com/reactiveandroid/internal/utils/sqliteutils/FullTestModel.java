package com.reactiveandroid.internal.utils.sqliteutils;

import com.reactiveandroid.Model;
import com.reactiveandroid.annotation.Collate;
import com.reactiveandroid.annotation.Column;
import com.reactiveandroid.annotation.ConflictAction;
import com.reactiveandroid.annotation.Index;
import com.reactiveandroid.annotation.IndexGroup;
import com.reactiveandroid.annotation.PrimaryKey;
import com.reactiveandroid.annotation.Table;
import com.reactiveandroid.annotation.Unique;
import com.reactiveandroid.annotation.UniqueGroup;
import com.reactiveandroid.test.TestDatabase;

import java.util.Date;

@Table(database = TestDatabase.class,
        uniqueGroups = {
                @UniqueGroup(groupNumber = 1),
                @UniqueGroup(groupNumber = 2, onUniqueConflict = ConflictAction.ROLLBACK)
        },
        indexGroups = @IndexGroup(groupNumber = 1, name = "index_1"))
public class FullTestModel extends Model {

    @PrimaryKey
    public Long id;

    @Unique(unique = false, uniqueGroups = 1)
    @Column
    public Date dateField;

    @Unique(onUniqueConflict = ConflictAction.IGNORE)
    @Column(collate = Collate.BINARY)
    public String stringField;

    @Index(indexGroups = 1)
    @Unique(unique = false, uniqueGroups = 1)
    @Column
    public double doubleField;

    @Index(indexGroups = 1)
    @Unique(unique = false, uniqueGroups = 2)
    @Column
    public int intField;

    @Unique(unique = false, uniqueGroups = 2)
    @Column
    public boolean booleanField;

    public int nonColumnField;

}
