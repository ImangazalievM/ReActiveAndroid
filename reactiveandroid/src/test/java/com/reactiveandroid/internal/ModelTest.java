package com.reactiveandroid.internal;

import com.reactiveandroid.Model;
import com.reactiveandroid.ReActiveAndroid;
import com.reactiveandroid.query.Select;
import com.reactiveandroid.test.BaseTest;
import com.reactiveandroid.test.models.TestModel;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

public class ModelTest extends BaseTest {

    @Test
    public void testLoad() {
        TestModel model = new TestModel(1L);
        model.stringField = "Hello";
        model.save();

        TestModel resultModel = Model.load(TestModel.class, 1L);
        assertEquals(1L, resultModel.id, 0f);
        assertEquals("Hello", resultModel.stringField);
    }

    @Test
    public void testDelete() {
        TestModel model = new TestModel(1L);
        model.save();
        assertEquals(1, getCount());

        Model.delete(TestModel.class, 1L);
        assertEquals(0, getCount());
    }

    @Test
    public void testSaveAll() {
        List<TestModel> models = TestModel.createEmptyModels(10);

        Model.saveAll(TestModel.class, models);
        assertEquals(10, getCount());
    }

    @Test
    public void testDeleteAll() {
        List<TestModel> models = TestModel.createEmptyModels(10);

        Model.saveAll(TestModel.class, models);
        assertEquals(10, getCount());

        Model.deleteAll(TestModel.class, models);
        assertEquals(0, getCount());
    }

    private int getCount() {
        return Select.from(TestModel.class).count();
    }


}
