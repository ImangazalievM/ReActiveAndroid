package com.reactiveandroid.test

import com.reactiveandroid.internal.serializer.TypeSerializer
import com.reactiveandroid.internal.utils.sqliteutils.FullTestModel
import com.reactiveandroid.query.join.CityModel
import com.reactiveandroid.query.join.CustomerModel
import com.reactiveandroid.query.join.OrderInfoQueryModel
import com.reactiveandroid.query.join.OrderModel
import com.reactiveandroid.relation.onetomany.CategoryModel
import com.reactiveandroid.relation.onetomany.TaskModel

import org.junit.Rule
import org.junit.runner.RunWith

import java.util.Arrays

@RunWith(ReActiveAndroidTestRunner::class)
abstract class BaseTest {

    @get:Rule
    var rule = DataBaseTestRule.create(databaseClass(), databaseModels(), typeSerializers())

    fun databaseClass(): Class<*> = TestDatabase::class.java

    fun databaseModels(): List<Class<*>> = Arrays.asList<Class<*>>(
            TestModel::class.java,
            FullTestModel::class.java,
            CustomerModel::class.java,
            CityModel::class.java,
            OrderInfoQueryModel::class.java,
            OrderModel::class.java,
            CategoryModel::class.java,
            TaskModel::class.java
    )

    fun typeSerializers(): List<Class<out TypeSerializer<*, *>>> = Arrays.asList()

}
