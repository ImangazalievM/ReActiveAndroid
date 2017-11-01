package com.reactiveandroid.internal.database.table;

import java.util.HashMap;

/**
 * Represents the types supported by SQLite
 */
public enum SQLiteType {

    INTEGER("INTEGER"), REAL("REAL"), TEXT("TEXT"), BLOB("BLOB");

    private String name;

    SQLiteType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    private static final HashMap<Class<?>, SQLiteType> TYPE_MAP = new HashMap<Class<?>, SQLiteType>() {
        {
            put(byte.class, SQLiteType.INTEGER);
            put(short.class, SQLiteType.INTEGER);
            put(int.class, SQLiteType.INTEGER);
            put(long.class, SQLiteType.INTEGER);
            put(float.class, SQLiteType.REAL);
            put(double.class, SQLiteType.REAL);
            put(boolean.class, SQLiteType.INTEGER);
            put(char.class, SQLiteType.TEXT);
            put(byte[].class, SQLiteType.BLOB);
            put(Byte.class, SQLiteType.INTEGER);
            put(Short.class, SQLiteType.INTEGER);
            put(Integer.class, SQLiteType.INTEGER);
            put(Long.class, SQLiteType.INTEGER);
            put(Float.class, SQLiteType.REAL);
            put(Double.class, SQLiteType.REAL);
            put(Boolean.class, SQLiteType.INTEGER);
            put(Character.class, SQLiteType.TEXT);
            put(String.class, SQLiteType.TEXT);
            put(Byte[].class, SQLiteType.BLOB);
        }
    };

    public static SQLiteType getTypeByName(String name) {
        for (SQLiteType type : values()) {
            if (type.name.equals(name)) {
                return type;
            }
        }
        return null;
    }

    public static boolean containsType(Class<?> type) {
        return TYPE_MAP.containsKey(type);
    }

    public static SQLiteType getSQLiteTypeForClass(Class<?> type) {
        return TYPE_MAP.get(type);
    }

}