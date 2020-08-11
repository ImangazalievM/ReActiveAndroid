package com.reactiveandroid.internal

import com.reactiveandroid.Model
import com.reactiveandroid.query.Select
import com.reactiveandroid.test.BaseTest
import com.reactiveandroid.test.TestModel

import org.junit.Test

import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNull

class ModelTest : BaseTest() {

    private val count: Int
        get() = Select.from(TestModel::class.java).count()

    @Test
    fun testLoad() {
        val model = TestModel(1L)
        model.stringField = "Hello"
        model.save()

        val resultModel = Model.load(TestModel::class.java, 1L)
        assertEquals(1f, resultModel!!.id.toFloat(), 0f)
        assertEquals("Hello", resultModel.stringField)
    }

    @Test
    fun testDelete() {
        val model = TestModel(1L)
        model.save()
        assertEquals(1, count)

        Model.delete(TestModel::class.java, 1L)
        assertEquals(0, count)
    }

    @Test
    fun testSaveAll() {
        val models = TestModel.createEmptyModels(10)

        Model.saveAll(TestModel::class.java, models)
        assertEquals(10, count)
    }

    @Test
    fun testDeleteAll() {
        val models = TestModel.createEmptyModels(10)

        Model.saveAll(TestModel::class.java, models)
        assertEquals(10, count)

        Model.deleteAll(TestModel::class.java, models)
        assertEquals(0, count)
    }


}
