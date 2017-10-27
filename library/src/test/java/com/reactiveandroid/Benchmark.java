package com.reactiveandroid;

import com.reactiveandroid.query.Delete;
import com.reactiveandroid.query.Select;
import com.reactiveandroid.test.BaseTest;
import com.reactiveandroid.test.TestUtils;
import com.reactiveandroid.test.models.BenchmarkModel;
import com.reactiveandroid.test.models.TestModel;

import org.junit.Test;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class Benchmark extends BaseTest {

    @Test
    public void insertModels() {
        List<BenchmarkModel> models = BenchmarkModel.createFilledModels(100);

        long startTime = getNow();
        TestUtils.saveModels(models);
        printResult("Insert time", startTime);
    }

    @Test
    public void insertModelsWithTransaction() {
        List<BenchmarkModel> models = BenchmarkModel.createFilledModels(100);

        long startTime = getNow();
        TestUtils.saveModels(models);
        printResult("Insert time", startTime);
    }

    @Test
    public void updateModels() {
        cleanTable();
        List<BenchmarkModel> models = BenchmarkModel.createFilledModels(100);
        TestUtils.saveModels(models);
        for (BenchmarkModel model : models) {
            model.dateField = new Date();
        }

        long startTime = getNow();
        TestUtils.saveModels(models);
        printResult("Insert time", startTime);
    }

    @Test
    public void readModels() {
        cleanTable();
        populateTable();

        long startTime = getNow();
        List<TestModel> models = Select.from(TestModel.class).fetch();
        printResult("Read time", startTime);
    }

    @Test
    public void deleteModels() {
        cleanTable();
        populateTable();

        long startTime = getNow();
        Delete.from(BenchmarkModel.class).execute();
        printResult("Delete time", startTime);
    }

    private void cleanTable() {
        Delete.from(BenchmarkModel.class).execute();
    }

    private void populateTable() {
        List<BenchmarkModel> models = BenchmarkModel.createFilledModels(100);
        TestUtils.saveModels(models);
    }

    private long getNow() {
        return System.nanoTime();
    }

    private void printResult(String text, long startTime) {
        System.out.print(text + ": " + TimeUnit.NANOSECONDS.toMillis(getNow() - startTime));
    }

}
