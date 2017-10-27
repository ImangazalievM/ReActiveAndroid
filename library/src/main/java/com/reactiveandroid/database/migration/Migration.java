package com.reactiveandroid.database.migration;

import android.database.sqlite.SQLiteDatabase;

/**
 * Base class for a database migration.
 * <p>
 * Each migration can move between 2 versions that are defined by {@link #startVersion} and
 * {@link #endVersion}.
 * <p>
 * A migration can handle more than 1 version (e.g. if you have a faster path to choose when
 * going version 3 to 5 without going to version 4). If Room opens a database at version
 * 3 and latest version is &gt;= 5, Room will use the migration object that can migrate from
 * 3 to 5 instead of 3 to 4 and 4 to 5.
 * <p>
 * If there are not enough migrations provided to move from the current version to the latest
 * version, Room will clear the database and recreate so even if you have no changes between 2
 * versions, you should still provide a Migration object to the builder.
 */
public abstract class Migration {

    public final int startVersion;
    public final int endVersion;

    public Migration(int startVersion, int endVersion) {
        this.startVersion = startVersion;
        this.endVersion = endVersion;
    }

    public abstract void migrate(SQLiteDatabase database);

}
