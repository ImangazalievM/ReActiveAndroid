package com.reactiveandroid.internal.database.migration;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;

import com.reactiveandroid.internal.log.LogLevel;
import com.reactiveandroid.internal.log.ReActiveLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A container to hold migrations. It also allows querying its contents to find migrations
 * between two versions.
 */
public class MigrationContainer {

    private SparseArray<SparseArray<Migration>> migrations = new SparseArray<>();

    public void addMigrations(Migration... migrations) {
        for (Migration migration : migrations) {
            addMigration(migration);
        }
    }

    private void addMigration(@NonNull Migration migration) {
        int start = migration.startVersion;
        int end = migration.endVersion;
        SparseArray<Migration> targetMap = migrations.get(start);
        if (targetMap == null) {
            targetMap = new SparseArray<>();
            migrations.put(start, targetMap);
        }
        Migration existing = targetMap.get(end);
        if (existing != null) {
            ReActiveLog.w(LogLevel.BASIC, "Overriding migration " + existing + " with " + migration);
        }
        targetMap.append(end, migration);
    }

    /**
     * Finds the list of migrations that should be run to move from {@code start} version to
     * {@code end} version.
     *
     * @param start The current database version
     * @param end   The target database version
     * @return An ordered list of {@link Migration} objects that should be run to migrate
     * between the given versions. If a migration path cannot be found, returns {@code null}.
     */
    @Nullable
    public List<Migration> findMigrationPath(int start, int end) {
        if (start == end) {
            return Collections.emptyList();
        }
        boolean migrateUp = end > start;
        List<Migration> result = new ArrayList<>();
        return findUpMigrationPath(result, migrateUp, start, end);
    }

    private List<Migration> findUpMigrationPath(List<Migration> result, boolean upgrade,
                                                int start, int end) {
        int searchDirection = upgrade ? -1 : 1;
        while (upgrade ? start < end : start > end) {
            SparseArray<Migration> targetNodes = migrations.get(start);
            if (targetNodes == null) {
                return null;
            }
            // keys are ordered so we can start searching from one end of them.
            int size = targetNodes.size();
            int firstIndex;
            int lastIndex;

            if (upgrade) {
                firstIndex = size - 1;
                lastIndex = -1;
            } else {
                firstIndex = 0;
                lastIndex = size;
            }
            boolean found = false;

            for (int i = firstIndex; i != lastIndex; i += searchDirection) {
                int targetVersion = targetNodes.keyAt(i);
                if (upgrade ? (targetVersion <= end && targetVersion > start)
                        : (targetVersion < start && targetVersion >= end)) {
                    result.add(targetNodes.valueAt(i));
                    start = targetVersion;
                    found = true;
                    break;
                }
            }
            if (!found) {
                return null;
            }
        }
        return result;
    }

}