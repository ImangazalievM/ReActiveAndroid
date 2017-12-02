package com.reactiveandroid.relation;

import com.reactiveandroid.query.Delete;
import com.reactiveandroid.query.Select;
import com.reactiveandroid.test.BaseTest;
import com.reactiveandroid.test.models.OneToManyModel;
import com.reactiveandroid.test.models.OneToManyRelationModel;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OneToManyTest extends BaseTest {

    @Test
    public void shouldLoadWithOneToManyRelation() {
        long modelId = 1L;
        OneToManyModel model = new OneToManyModel(modelId);
        model.save();

        for (long id : Arrays.asList(1L, 2L, 3L, 4L)) {
            new OneToManyRelationModel(id, model.getId()).save();
        }

        OneToManyModel result = findById(OneToManyModel.class, modelId);

        Assert.assertEquals(4, result.getModels().size());

        Assert.assertEquals(1L, result.getModels().get(0).getId(), 0f);
        Assert.assertEquals(2L, result.getModels().get(1).getId(), 0f);
        Assert.assertEquals(3L, result.getModels().get(2).getId(), 0f);
        Assert.assertEquals(4L, result.getModels().get(3).getId(), 0f);

        Assert.assertEquals(result.getId(), result.getModels().get(0).getForeignTableModelId(), 0f);
        Assert.assertEquals(result.getId(), result.getModels().get(1).getForeignTableModelId(), 0f);
        Assert.assertEquals(result.getId(), result.getModels().get(2).getForeignTableModelId(), 0f);
        Assert.assertEquals(result.getId(), result.getModels().get(3).getForeignTableModelId(), 0f);
    }

    //@Test
    //public void shouldRemoveOneOfManyToOneRelation() {
    //    OneToManyModel model = new OneToManyModel(1L);
//
    //    for (long id : Arrays.asList(1L, 2L, 3L, 4L)) {
    //        new OneToManyRelationModel(id, model.getId()).save();
    //    }
//
    //    OneToManyModel result = findById(OneToManyModel.class, 1L);
//
    //    Assert.assertEquals(4, result.getModels().size());
//
    //    Delete.from(OneToManyRelationModel.class).where("id = ?", String.valueOf(3L)).execute();
//
    //    result = findById(OneToManyModel.class, 1L);
//
    //    Assert.assertEquals(3, result.getModels().size());
//
    //    Assert.assertTrue(result.getModels().get(0).getId() != 3L);
    //    Assert.assertTrue(result.getModels().get(1).getId() != 3L);
    //    Assert.assertTrue(result.getModels().get(2).getId() != 3L);
    //}
//
//
    //@Test
    //public void shouldNotRemoveRelation() {
    //    OneToManyModel model = new OneToManyModel(1L);
    //    model.save();
//
    //    for (long i : Arrays.asList(1L, 2L, 3L, 4L)) {
    //        new OneToManyRelationModel(i, model).save();
    //    }
//
    //    OneToManyModel result = findById(OneToManyModel.class, 1L);
//
    //    result.getModels().clear();
//
    //    model.save();
//
    //    result = Select.from(OneToManyModel.class).where("id=?", 1L).fetchSingle();
//
    //    Assert.assertEquals(4, result.getModels().size());
    //}
//
    //@Test
    //public void shouldNotAddRelation() {
    //    List<Long> relationIds = Arrays.asList(1L, 2L, 3L, 4L);
    //    OneToManyModel model = new OneToManyModel(1L);
    //    model.save();
//
    //    for (long i : relationIds) {
    //        new OneToManyRelationModel(i, model).save();
    //    }
//
    //    new OneToManyRelationModel(5l, null).save();
//
    //    OneToManyModel result = Select.from(OneToManyModel.class).where("id=?", 1l).fetchSingle();
//
    //    Assert.assertEquals(4, result.getModels().size());
//
    //    Assert.assertTrue(relationIds.contains(result.getModels().get(0).getId()));
    //    Assert.assertTrue(relationIds.contains(result.getModels().get(1).getId()));
    //    Assert.assertTrue(relationIds.contains(result.getModels().get(2).getId()));
    //    Assert.assertTrue(relationIds.contains(result.getModels().get(3).getId()));
//
    //    Assert.assertEquals(result, result.getModels().get(0).getForeignTableModelId());
    //    Assert.assertEquals(result, result.getModels().get(1).getForeignTableModelId());
    //    Assert.assertEquals(result, result.getModels().get(2).getForeignTableModelId());
    //    Assert.assertEquals(result, result.getModels().get(3).getForeignTableModelId());
    //}
//
    //@Test
    //public void shouldNotInflateList() {
    //    List<Long> relationIds = Arrays.asList(1L, 2L, 3L, 4L);
    //    WithoutOneToManyAnnotationModel model = new WithoutOneToManyAnnotationModel(1l);
    //    model.save();
//
    //    for (long i : relationIds) {
    //        new WithoutOneToManyAnnotationRelationModel(i, model);
    //    }
//
    //    WithoutOneToManyAnnotationModel result = findById(WithoutOneToManyAnnotationModel.class, 1l);
//
    //    Assert.assertEquals(null, result.getModels());
    //}
//
    private <ModelClass> ModelClass findById(Class<ModelClass> modelClass, long id) {
        return Select.from(modelClass).where("id=?", id).fetchSingle();
    }

}
