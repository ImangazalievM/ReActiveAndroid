package com.reactiveandroid.internal.database;

import com.reactiveandroid.annotation.Database;
import com.reactiveandroid.internal.database.migration.Migration;
import com.reactiveandroid.internal.database.migration.MigrationContainer;
import com.reactiveandroid.internal.serializer.TypeSerializer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Contains configuration for database
 */
public class DatabaseConfig {

    public final Class<?> databaseClass;
    public final String databaseName;
    public final int databaseVersion;
    public final List<Class<?>> modelClasses;
    public final List<Class<? extends TypeSerializer>> typeSerializers;
    public final MigrationContainer migrationContainer;
    public final boolean requireMigration;

    private DatabaseConfig(Class<?> databaseClass,
                          String databaseName,
                          int databaseVersion,
                          List<Class<?>> modelClasses,
                          List<Class<? extends TypeSerializer>> typeSerializers,
                          MigrationContainer migrationContainer,
                          boolean requireMigration) {
        this.databaseClass = databaseClass;
        this.databaseName = databaseName;
        this.databaseVersion = databaseVersion;
        this.modelClasses = modelClasses;
        this.typeSerializers = typeSerializers;
        this.migrationContainer = migrationContainer;
        this.requireMigration = requireMigration;
    }

    public boolean isValid() {
        return modelClasses != null && modelClasses.size() > 0;
    }

    public static class Builder {

        private Class<?> databaseClass;
        private String databaseName;
        private int databaseVersion;
        private List<Class<?>> modelClasses;
        private List<Class<? extends TypeSerializer>> typeSerializers;
        private MigrationContainer migrationContainer;
        private boolean requireMigration;

        public Builder(Class<?> databaseClass) {
            if (!databaseClass.isAnnotationPresent(Database.class)) {
                throw new IllegalArgumentException("Annotation @Database not found for class " + databaseClass.getName());
            }

            Database databaseAnnotation = databaseClass.getAnnotation(Database.class);

            this.databaseClass = databaseClass;
            this.databaseName = databaseAnnotation.name() + ".db";
            this.databaseVersion = databaseAnnotation.version();
            this.modelClasses = new ArrayList<>();
            this.typeSerializers = new ArrayList<>();
            this.migrationContainer = new MigrationContainer();
            this.requireMigration = true;
        }

        public Builder addModelClasses(Class<?>... modelClasses) {
            this.modelClasses.addAll(Arrays.asList(modelClasses));
            return this;
        }

        public Builder addTypeSerializers(Class<? extends TypeSerializer>... typeSerializers) {
            this.typeSerializers.addAll(Arrays.asList(typeSerializers));
            return this;
        }

        public Builder addMigrations(Migration... migrations) {
            this.migrationContainer.addMigrations(migrations);
            return this;
        }

        public Builder disableMigrationsChecking() {
            this.requireMigration = false;
            return this;
        }

        public DatabaseConfig build() {
            return new DatabaseConfig(databaseClass,
                    databaseName,
                    databaseVersion,
                    modelClasses,
                    typeSerializers,
                    migrationContainer,
                    requireMigration);
        }

    }

}
