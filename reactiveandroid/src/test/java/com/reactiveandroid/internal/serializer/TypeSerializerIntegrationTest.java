package com.reactiveandroid.internal.serializer;

import android.support.annotation.Nullable;

import com.reactiveandroid.Model;
import com.reactiveandroid.ReActiveAndroid;
import com.reactiveandroid.ReActiveConfig;
import com.reactiveandroid.annotation.Column;
import com.reactiveandroid.annotation.Database;
import com.reactiveandroid.annotation.PrimaryKey;
import com.reactiveandroid.annotation.Table;
import com.reactiveandroid.internal.database.DatabaseConfig;
import com.reactiveandroid.query.Select;
import com.reactiveandroid.test.ReActiveAndroidTestRunner;
import com.reactiveandroid.test.TestUtils;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import java.util.Date;

import static junit.framework.Assert.assertEquals;

@RunWith(ReActiveAndroidTestRunner.class)
public class TypeSerializerIntegrationTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testModelSavingWithCustomFieldPredefinedType_shouldCorrectlySaveAndLoadModel() {
        initDatabase(AnyTypeDeserializer.class);

        Date currentTime = new Date();
        long modelId = new ModelWithCustomField(currentTime).save();
        ModelWithCustomField model = Select.from(ModelWithCustomField.class).where("id = ?", modelId).fetchSingle();
        assertEquals(currentTime, model.dateField);
    }

    @Test
    public void testModelSavingWithCustomFieldType_shouldCorrectlySaveAndLoadModel() {
        initDatabase(AnyTypeDeserializer.class);

        long modelId = new ModelWithCustomField2(ModelWithCustomField2.AnyType.TWO).save();
        ModelWithCustomField2 model = Select.from(ModelWithCustomField2.class).where("id = ?", modelId).fetchSingle();
        assertEquals(ModelWithCustomField2.AnyType.TWO, model.myCustomField);
    }

    @Test
    public void testModelSavingWithCustomField_shouldThrowExceptionSoSerializerNotFound() {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Type serializer for type " +
                "com.reactiveandroid.internal.serializer.TypeSerializerIntegrationTest.ModelWithCustomField2.AnyType not found");

        initDatabase();

        long modelId = new ModelWithCustomField2(ModelWithCustomField2.AnyType.TWO).save();
        ModelWithCustomField2 model = Select.from(ModelWithCustomField2.class).where("id = ?", modelId).fetchSingle();
        assertEquals(ModelWithCustomField2.AnyType.TWO, model.myCustomField);
    }

    private void initDatabase(Class<? extends TypeSerializer>... typeSerializers) {
        DatabaseConfig testDatabaseConfig = new DatabaseConfig.Builder(TestDatabase.class)
                .addTypeSerializers(typeSerializers)
                .addModelClasses(ModelWithCustomField.class, ModelWithCustomField2.class)
                .build();

        ReActiveAndroid.init(new ReActiveConfig.Builder(TestUtils.getApplication())
                .addDatabaseConfigs(testDatabaseConfig)
                .build());
    }

    @After
    public void destroyDatabase() {
        ReActiveAndroid.destroy();
    }

    @Database(name = "TestDatabase2", version = 1)
    public class TestDatabase {
    }

    @Table(database = TestDatabase.class)
    public static class ModelWithCustomField extends Model {

        @PrimaryKey
        public Long id;
        @Column
        public Date dateField;

        public ModelWithCustomField() {
        }

        public ModelWithCustomField(Date dateField) {
            this.dateField = dateField;
        }

    }

    @Table(database = TestDatabase.class)
    public static class ModelWithCustomField2 extends Model {

        enum AnyType {
            ONE("ONE"), TWO("TWO");

            private final String name;

            AnyType(String name) {
                this.name = name;
            }

            public boolean equals(String name) {
                return this.name.equals(name);
            }

            public String toString() {
                return this.name;
            }

            public static AnyType getByName(String name) {
                for (AnyType anyType : values()) {
                    if (anyType.equals(name)) return anyType;
                }
                return null;
            }

        }

        @PrimaryKey
        public Long id;
        @Column
        public AnyType myCustomField;

        public ModelWithCustomField2() {
        }

        public ModelWithCustomField2(AnyType myCustomField) {
            this.myCustomField = myCustomField;
        }

    }

    public static class AnyTypeDeserializer extends TypeSerializer<ModelWithCustomField2.AnyType, String> {

        @Nullable
        @Override
        public String serialize(@Nullable ModelWithCustomField2.AnyType data) {
            return data.toString();
        }

        @Nullable
        @Override
        public ModelWithCustomField2.AnyType deserialize(@Nullable String data) {
            return ModelWithCustomField2.AnyType.getByName(data);
        }

    }

}
