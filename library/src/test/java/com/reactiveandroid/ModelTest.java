package com.reactiveandroid;

import com.reactiveandroid.query.Delete;
import com.reactiveandroid.query.Select;
import com.reactiveandroid.test.BaseTest;
import com.reactiveandroid.test.TestUtils;
import com.reactiveandroid.test.models.CategoryTestModel;
import com.reactiveandroid.test.models.ItemTestModel;
import com.reactiveandroid.test.models.TestModel;

import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

public class ModelTest extends BaseTest {

    @Test
    public void testGetId() {
        TestModel model = new TestModel();

        assertNull(model.getId());
    }

    @Test
    public void testGetIdAfterSave() {
        cleanTable();
        TestModel model = new TestModel();
        model.save();

        assertEquals(1L, model.getId(), 0);
        assertEquals(1, getCount());
    }

    @Test
    public void testSave() {
        cleanTable();
        TestModel model = new TestModel();

        model.save();
        assertEquals(1, getCount());
    }

    @Test
    public void testDelete() {
        cleanTable();
        TestModel model = new TestModel();

        model.save();
        assertEquals(1, getCount());

        model.delete();
        assertEquals(0, getCount());
    }

    @Test
    public void testStaticDelete() {
        cleanTable();
        TestModel model = new TestModel();

        model.save();
        assertEquals(1, getCount());

        Model.delete(model.getClass(), model.getId());
        assertEquals(0, getCount());
    }

    @Test
    public void testStaticLoad() {
        cleanTable();
        TestModel model = new TestModel();
        model.stringField = "Random text";

        model.save();
        assertEquals(1, getCount());

        TestModel resultModel = Model.load(model.getClass(), model.getId());

        assertEquals(model.getId(), resultModel.getId());
        assertEquals(model.stringField, resultModel.stringField);
    }

    @Test
    public void testStaticSaveAll() {
        cleanTable();
        List<TestModel> models = TestModel.createEmptyModels(10);

        Model.saveAll(TestModel.class, models);
        assertEquals(10, getCount());
    }

    @Test
    public void testStaticDeleteAll() {
        cleanTable();
        List<TestModel> models = TestModel.createEmptyModels(10);

        Model.saveAll(TestModel.class, models);
        assertEquals(10, getCount());

        Model.deleteAll(TestModel.class, models);
        assertEquals(0, getCount());
    }

    @Test
    public void testGetMany() {
        CategoryTestModel categoryModel = new CategoryTestModel();
        Long categoryModeId = categoryModel.save();
        List<ItemTestModel> itemModels = ItemTestModel.createModelsWithCategory(categoryModel, 10);
        TestUtils.saveModels(itemModels);

        List<ItemTestModel> resultItemModels = categoryModel.getItems();
        assertEquals(itemModels.size(), resultItemModels.size());
        for (ItemTestModel itemTestModel : resultItemModels) {
            assertEquals(categoryModeId, itemTestModel.category.getId());
        }
    }

    private void cleanTable() {
        Delete.from(TestModel.class).execute();
    }

    private int getCount() {
        return Select.count().from(TestModel.class).fetchValue(int.class);
    }

}
