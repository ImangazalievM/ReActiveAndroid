package com.reactiveandroid.database.table;

/**
 * Contains information about tables column
 */

@SuppressWarnings("WeakerAccess")
public class ColumnInfo {

    public final String name;
    public final SQLiteType type;
    public final boolean notNull;
    public final int primaryKeyPosition;

    // if you change this constructor, you must change TableInfoWriter.kt
    public ColumnInfo(String name, SQLiteType type, boolean notNull, int primaryKeyPosition) {
        this.name = name;
        this.type = type;
        this.notNull = notNull;
        this.primaryKeyPosition = primaryKeyPosition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ColumnInfo column = (ColumnInfo) o;
        if (isPrimaryKey() != column.isPrimaryKey()) return false;
        if (!name.equals(column.name)) return false;
        //noinspection SimplifiableIfStatement
        if (notNull != column.notNull) return false;
        return type != null ? type.equals(column.type) : column.type == null;
    }

    public boolean isPrimaryKey() {
        return primaryKeyPosition > 0;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (notNull ? 1231 : 1237);
        result = 31 * result + primaryKeyPosition;
        return result;
    }

    @Override
    public String toString() {
        return "Column{"
                + "name='" + name + '\''
                + ", type='" + type + '\''
                + ", notNull=" + notNull
                + ", primaryKeyPosition=" + primaryKeyPosition
                + '}';
    }

}