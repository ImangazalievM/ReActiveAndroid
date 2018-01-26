package com.reactiveandroid.internal.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import com.reactiveandroid.internal.database.migration.Migration;
import com.reactiveandroid.internal.database.table.TableInfo;
import com.reactiveandroid.internal.utils.SQLiteUtils;

import java.util.Collection;
import java.util.List;

/**
 * An open helper that holds a reference to the configuration until the database is opened.
 */
public final class ReActiveOpenHelper extends SQLiteOpenHelper {

    private DatabaseConfig databaseConfig;
    private Collection<TableInfo> tableInfos;
    private String currentIdentityHash;

    ReActiveOpenHelper(Context context,
                       @NonNull DatabaseConfig databaseConfig,
                       Collection<TableInfo> tableInfos) {
        super(context, databaseConfig.databaseName, null, databaseConfig.databaseVersion);
        this.databaseConfig = databaseConfig;
        this.tableInfos = tableInfos;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createAllTables(db);
        createIndicesForAllTables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        executeMigrations(db, oldVersion, newVersion);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        checkIdentity(db);
        executePragmas(db);
    }

    private void executePragmas(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys = ON;");
    }

    private void checkIdentity(SQLiteDatabase db) {
        String newDatabaseIdentityHash = getNewSchemaHash();

        createMasterTableIfNotExists(db);
        currentIdentityHash = getCurrentDatabaseIdentityHash(db);

        //first database version
        if (currentIdentityHash == null) {
            currentIdentityHash = newDatabaseIdentityHash;
            updateIdentity(db);
        } else {
            if (!currentIdentityHash.equals(newDatabaseIdentityHash)) {
                throw new IllegalStateException("ReActiveAndroid cannot verify the data integrity. Looks like"
                        + " you've changed schema but forgot to update the version number. You can"
                        + " simply fix this by increasing the version number.");
            }
        }
    }

    private String getNewSchemaHash() {
        return SQLiteUtils.getIdentityHash(tableInfos);
    }

    private String getCurrentDatabaseIdentityHash(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery(ReActiveMasterTable.READ_QUERY, new String[]{});
        try {
            if (cursor.moveToFirst()) {
                return cursor.getString(0);
            }
        } finally {
            cursor.close();
        }
        return null;
    }

    private void updateIdentity(SQLiteDatabase db) {
        createMasterTableIfNotExists(db);
        db.execSQL(ReActiveMasterTable.createInsertQuery(currentIdentityHash));
    }

    private void createMasterTableIfNotExists(SQLiteDatabase db) {
        db.execSQL(ReActiveMasterTable.CREATE_QUERY);
    }

    private void createAllTables(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            for (TableInfo tableInfo : tableInfos) {
                String tableDefinition = SQLiteUtils.createTableDefinition(tableInfo);
                db.execSQL(tableDefinition);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private void createIndicesForAllTables(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            for (TableInfo tableInfo : tableInfos) {
                List<String> definitions = SQLiteUtils.createIndexDefinition(tableInfo);
                for (String definition : definitions) {
                    db.execSQL(definition);
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private void dropAllTables(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            for (String tableName : SQLiteUtils.getAllTableNames(db)) {
                db.execSQL("DROP TABLE " + tableName);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private void executeMigrations(SQLiteDatabase db, int oldVersion, int newVersion) {
        boolean migrated = false;
        List<Migration> migrations = databaseConfig.migrationContainer.findMigrationPath(oldVersion, newVersion);
        if (migrations != null) {
            for (Migration migration : migrations) {
                migration.migrate(db);
            }
            currentIdentityHash = getNewSchemaHash();
            updateIdentity(db);
            migrated = true;
        }

        if (!migrated) {
            if (databaseConfig.requireMigration) {
                throw new IllegalStateException("A migration from " + oldVersion + " to "
                        + newVersion + " is necessary. Please provide a Migration in the builder or call"
                        + " disableMigrationsChecking in the builder in which case ReActiveAndroid will"
                        + " re-create all of the tables.");
            }
            dropAllTables(db);
            createAllTables(db);
            createIndicesForAllTables(db);
        }
    }

}
