package com.reactiveandroid.database.table;

import java.util.ArrayList;
import java.util.List;

public class IndexGroupInfo {

    public final String name;
    public final List<ColumnInfo> columns = new ArrayList<>();
    public final boolean isUnique;

    public IndexGroupInfo(String name, boolean isUnique) {
        this.name = name;
        this.isUnique = isUnique;
    }

    public void addColumn(ColumnInfo columnInfo) {
        columns.add(columnInfo);
    }

}
