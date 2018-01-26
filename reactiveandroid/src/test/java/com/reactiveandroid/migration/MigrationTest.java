package com.reactiveandroid.migration;

import com.reactiveandroid.Model;
import com.reactiveandroid.ReActiveAndroid;
import com.reactiveandroid.ReActiveConfig;
import com.reactiveandroid.annotation.Column;
import com.reactiveandroid.annotation.Database;
import com.reactiveandroid.annotation.PrimaryKey;
import com.reactiveandroid.annotation.Table;
import com.reactiveandroid.internal.database.DatabaseConfig;
import com.reactiveandroid.test.ReActiveAndroidTestRunner;
import com.reactiveandroid.test.TestUtils;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

@RunWith(ReActiveAndroidTestRunner.class)
public class MigrationTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testVersionNotIncreasedWhenSchemaChanged() {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("ReActiveAndroid cannot verify the data integrity. Looks like"
                + " you've changed schema but forgot to update the version number. You can"
                + " simply fix this by increasing the version number.");
        initDatabase(TestDatabaseV1.class, Model1.class);
        ReActiveAndroid.destroy();
        initDatabase(TestDatabaseV1.class, Model2.class, Model2.class);
    }

    @Test
    public void testMigrationNotProvidedWhenVersionIsChanged_shouldThrowException() {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("A migration from 1 to 2"
                + " is necessary. Please provide a Migration in the builder or call"
                + " disableMigrationsChecking in the builder in which case ReActiveAndroid will"
                + " re-create all of the tables.");
        initDatabase(TestDatabaseV1.class);
        ReActiveAndroid.destroy();
        initDatabase(TestDatabaseV2.class);

    }

    private void initDatabase(Class<?> databaseClass, Class<?>... modelClasses) {
        DatabaseConfig databaseConfig = new DatabaseConfig.Builder(databaseClass)
                .addModelClasses(modelClasses)
                .build();

        ReActiveAndroid.init(new ReActiveConfig.Builder(TestUtils.getApplication())
                .addDatabaseConfigs(databaseConfig)
                .build());
    }

    @Database(name = "TestDatabase", version = 1)
    public class TestDatabaseV1 {
    }

    @Database(name = "TestDatabase", version = 2)
    public class TestDatabaseV2 {
    }

    @Table(database = TestDatabaseV1.class)
    public class Model1 extends Model {

        @PrimaryKey
        public Long id;
        @Column
        public int intField;

    }

    @Table(database = TestDatabaseV1.class)
    public class Model2 extends Model {

        @PrimaryKey
        public Long id;
        @Column
        public int intField;

    }

}
