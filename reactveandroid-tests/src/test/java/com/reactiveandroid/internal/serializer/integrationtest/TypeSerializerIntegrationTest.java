package com.reactiveandroid.internal.serializer.integrationtest;

import com.reactiveandroid.ReActiveAndroid;
import com.reactiveandroid.ReActiveConfig;
import com.reactiveandroid.internal.database.DatabaseConfig;
import com.reactiveandroid.internal.serializer.TypeSerializer;
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

@SuppressWarnings("ALL")
@RunWith(ReActiveAndroidTestRunner.class)
public class TypeSerializerIntegrationTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testModelSavingWithCustomFieldPredefinedType_shouldCorrectlySaveAndLoadModel() {
        initDatabase(CustomTypeSerializer.class);

        Date currentTime = new Date();
        long modelId = new ModelWithDateField(currentTime).save();
        ModelWithDateField model = Select.from(ModelWithDateField.class).where("id = ?", modelId).fetchSingle();
        assertEquals(currentTime, model.dateField);
    }

    @Test
    public void testModelSavingWithCustomFieldType_shouldCorrectlySaveAndLoadModel() {
        initDatabase(CustomTypeSerializer.class);

        long modelId = new ModelWithCustomField(ModelWithCustomField.MyCustomType.TWO).save();
        ModelWithCustomField model = Select.from(ModelWithCustomField.class).where("id = ?", modelId).fetchSingle();
        assertEquals(ModelWithCustomField.MyCustomType.TWO, model.myCustomField);
    }

    @Test
    public void testModelSavingWithCustomField_shouldThrowExceptionSoSerializerNotFound() {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Type serializer for type " +
                "com.reactiveandroid.internal.serializer.integrationtest.ModelWithCustomField.MyCustomType not found");

        initDatabase();

        long modelId = new ModelWithCustomField(ModelWithCustomField.MyCustomType.TWO).save();
        ModelWithCustomField model = Select.from(ModelWithCustomField.class).where("id = ?", modelId).fetchSingle();
        assertEquals(ModelWithCustomField.MyCustomType.TWO, model.myCustomField);
    }

    private void initDatabase(Class<? extends TypeSerializer>... typeSerializers) {
        DatabaseConfig testDatabaseConfig = new DatabaseConfig.Builder(MyDatabase.class)
                .addTypeSerializers(typeSerializers)
                .addModelClasses(ModelWithDateField.class, ModelWithCustomField.class)
                .build();

        ReActiveAndroid.init(new ReActiveConfig.Builder(TestUtils.getApplication())
                .addDatabaseConfigs(testDatabaseConfig)
                .build());
    }

    @After
    public void destroyDatabase() {
        ReActiveAndroid.destroy();
    }

}
