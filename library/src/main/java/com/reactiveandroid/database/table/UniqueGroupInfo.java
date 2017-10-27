package com.reactiveandroid.database.table;

import com.reactiveandroid.annotation.ConflictAction;

import java.util.ArrayList;
import java.util.List;

public class UniqueGroupInfo {

    public final List<ColumnInfo> columns = new ArrayList<>();
    public final ConflictAction uniqueConflict;

    public UniqueGroupInfo(ConflictAction uniqueConflict) {
        this.uniqueConflict = uniqueConflict;
    }

    public void addColumn(ColumnInfo columnInfo) {
        columns.add(columnInfo);
    }

}
