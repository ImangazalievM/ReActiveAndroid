package com.reactiveandroid.internal;

import com.reactiveandroid.ReActiveAndroid;
import com.reactiveandroid.query.Select;
import com.reactiveandroid.test.BaseTest;
import com.reactiveandroid.test.TestModel;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

public class ModelAdapterTest extends BaseTest {

    private ModelAdapter<TestModel> testModelModelAdapter;

    @Before
    public void setUp() {
        testModelModelAdapter = ReActiveAndroid.getModelAdapter(TestModel.class);
    }

    @Test
    public void testIdValueAfterSave() {
        TestModel model = new TestModel();
        assertNull(model.id);

        testModelModelAdapter.save(model);

        assertEquals(1L, model.id, 0);
        assertEquals(1, getCount());
    }

    @Test
    public void testPredefinedIdValueAfterSave() {
        TestModel model = new TestModel(100L);
        assertEquals(100L, model.id, 0);

        testModelModelAdapter.save(model);

        assertEquals(100L, model.id, 0);
    }

    @Test
    public void testSave() {
        TestModel model = new TestModel();

        testModelModelAdapter.save(model);

        assertEquals(1, getCount());
    }

    @Test
    public void testDelete() {
        TestModel model = new TestModel();

        testModelModelAdapter.save(model);
        assertEquals(1, getCount());

        testModelModelAdapter.delete(model);
        assertEquals(0, getCount());
    }

    private int getCount() {
        return Select.from(TestModel.class).count();
    }


}
