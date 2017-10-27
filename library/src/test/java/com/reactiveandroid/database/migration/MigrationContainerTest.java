package com.reactiveandroid.database.migration;

import android.database.sqlite.SQLiteDatabase;

import com.reactiveandroid.test.BaseTest;

import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

public class MigrationContainerTest extends BaseTest {

    @Test
    public void testFindMigrationPath() {
        MigrationContainer migrationContainer = new MigrationContainer();
        migrationContainer.addMigrations(MIGRATION_12, MIGRATION_23, MIGRATION_34);

        assertEquals(MIGRATION_12, migrationContainer.findMigrationPath(1, 2).get(0));
        assertEquals(MIGRATION_23, migrationContainer.findMigrationPath(2, 3).get(0));
        assertEquals(MIGRATION_34, migrationContainer.findMigrationPath(3, 4).get(0));

        assertEquals(0, migrationContainer.findMigrationPath(1, 1).size());
        assertEquals(1, migrationContainer.findMigrationPath(1, 2).size());
        assertEquals(2, migrationContainer.findMigrationPath(1, 3).size());
        assertEquals(3, migrationContainer.findMigrationPath(1, 4).size());
    }

    @Test
    public void testFindMigrationPath_shouldReturnNullIfMigrationNotFound() {
        MigrationContainer migrationContainer = new MigrationContainer();

        assertNull(migrationContainer.findMigrationPath(1, 2));
    }

    @Test
    public void testFindMigrationPath_shouldOverrideMigration() {
        MigrationContainer migrationContainer = new MigrationContainer();
        migrationContainer.addMigrations(MIGRATION_12, MIGRATION_12_2);

        assertEquals(MIGRATION_12_2, migrationContainer.findMigrationPath(1, 2).get(0));
    }

    @Test
    public void testFindMigrationPath_shouldOverrideAllMigrations() {
        MigrationContainer migrationContainer = new MigrationContainer();
        migrationContainer.addMigrations(MIGRATION_12, MIGRATION_23, MIGRATION_34);

        assertEquals(3, migrationContainer.findMigrationPath(1, 4).size());

        migrationContainer.addMigrations(MIGRATION_14);

        List<Migration> resultMigrations = migrationContainer.findMigrationPath(1, 4);
        assertEquals(1, resultMigrations.size());
        assertEquals(MIGRATION_14, resultMigrations.get(0));
    }

    @Test
    public void testFindMigrationPathOnDowngrade() {
        MigrationContainer migrationContainer = new MigrationContainer();
        migrationContainer.addMigrations(MIGRATION_41);

        assertEquals(MIGRATION_41, migrationContainer.findMigrationPath(4, 1).get(0));
    }

    private static final Migration MIGRATION_12 = new Migration(1, 2) {
        @Override
        public void migrate(SQLiteDatabase database) {

        }
    };

    private static final Migration MIGRATION_23 = new Migration(2, 3) {
        @Override
        public void migrate(SQLiteDatabase database) {

        }
    };

    private static final Migration MIGRATION_34 = new Migration(3, 4) {
        @Override
        public void migrate(SQLiteDatabase database) {

        }
    };

    private static final Migration MIGRATION_14 = new Migration(1, 4) {
        @Override
        public void migrate(SQLiteDatabase database) {

        }
    };

    private static final Migration MIGRATION_12_2 = new Migration(1, 2) {
        @Override
        public void migrate(SQLiteDatabase database) {

        }
    };

    private static final Migration MIGRATION_41 = new Migration(4, 1) {
        @Override
        public void migrate(SQLiteDatabase database) {

        }
    };

}
