package com.reactiveandroid.relation.onetomany;

import com.reactiveandroid.query.Select;
import com.reactiveandroid.test.BaseTest;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class OneToManyTest extends BaseTest {

    @Test
    public void shouldLoadWithOneToManyRelation() {
        final long categoryId = 1L;
        CategoryModel category = new CategoryModel(categoryId);
        category.save();

        for (long id : Arrays.asList(1L, 2L, 3L, 4L)) {
            new TaskModel(id, category.getId()).save();
        }

        CategoryModel resultCategory = findById(CategoryModel.class, categoryId);
        TaskModel[] tasks = resultCategory.getTasks().toArray(new TaskModel[]{});

        Assert.assertEquals(categoryId, resultCategory.getId());
        Assert.assertEquals(4, tasks.length);

        Assert.assertTrue(1L == tasks[0].getId());
        Assert.assertTrue(2L == tasks[1].getId());
        Assert.assertTrue(3L == tasks[2].getId());
        Assert.assertTrue(4L == tasks[3].getId());

        Assert.assertTrue(categoryId == tasks[0].getCategoryId());
        Assert.assertTrue(categoryId == tasks[1].getCategoryId());
        Assert.assertTrue(categoryId == tasks[2].getCategoryId());
        Assert.assertTrue(categoryId == tasks[3].getCategoryId());
    }

    private <ModelClass> ModelClass findById(Class<ModelClass> modelClass, long id) {
        return Select.from(modelClass).where("id=?", id).fetchSingle();
    }

}
