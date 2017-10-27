package com.reactiveandroid.database;

/**
 * Schema information about ReActive's master table.
 */
public class ReActiveMasterTable {

    public static final String TABLE_NAME = "reactive_master_table";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_IDENTITY_HASH = "identity_hash";
    public static final String DEFAULT_ID = "42";

    public static final String CREATE_QUERY = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY,"
            + COLUMN_IDENTITY_HASH + " TEXT)";

    public static final String READ_QUERY = "SELECT " + COLUMN_IDENTITY_HASH
            + " FROM " + TABLE_NAME + " WHERE "
            + COLUMN_ID + " = " + DEFAULT_ID + " LIMIT 1";

    public static String createInsertQuery(String hash) {
        return "INSERT OR REPLACE INTO " + TABLE_NAME + " ("
                + COLUMN_ID + ", " + COLUMN_IDENTITY_HASH + ")"
                + " VALUES(" + DEFAULT_ID + ", \"" + hash + "\")";
    }

}
