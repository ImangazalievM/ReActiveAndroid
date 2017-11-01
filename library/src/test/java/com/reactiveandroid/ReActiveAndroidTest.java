package com.reactiveandroid;

import com.reactiveandroid.internal.database.table.TableInfo;
import com.reactiveandroid.test.BaseTest;
import com.reactiveandroid.test.databases.TestDatabase;
import com.reactiveandroid.test.models.CacheTestModel;
import com.reactiveandroid.test.models.CacheTestModel2;

import org.junit.Test;

import java.util.Collection;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class ReActiveAndroidTest extends BaseTest {

    @Test
    public void testGetTableInfos() {
        assertNotNull(ReActiveAndroid.getContext());
        Collection<TableInfo> tableInfos = ReActiveAndroid.getDatabaseTablesInfos(TestDatabase.class);
        assertEquals(10, tableInfos.size());

        TableInfo testModelTableInfo = ReActiveAndroid.getTableInfo(CacheTestModel.class);
        assertNotNull(testModelTableInfo);
        assertEquals("CacheTestModel", testModelTableInfo.getTableName());

        TableInfo testModel2TableInfo = ReActiveAndroid.getTableInfo(CacheTestModel2.class);
        assertNotNull(testModel2TableInfo);
        assertEquals("CacheTestModel2", testModel2TableInfo.getTableName());
    }

}
