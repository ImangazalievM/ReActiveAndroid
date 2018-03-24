package com.reactiveandroid.internal;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.reactiveandroid.ReActiveAndroid;
import com.reactiveandroid.internal.database.table.ColumnInfo;
import com.reactiveandroid.internal.database.table.QueryModelInfo;
import com.reactiveandroid.internal.log.LogLevel;
import com.reactiveandroid.internal.log.ReActiveLog;
import com.reactiveandroid.internal.utils.SQLiteUtils;
import com.reactiveandroid.query.Select;
import com.reactiveandroid.internal.utils.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QueryModelAdapter<QueryModelClass> {

    private QueryModelInfo queryModelInfo;

    public QueryModelAdapter(@NonNull QueryModelInfo queryModelInfo) {
        this.queryModelInfo = queryModelInfo;
    }

    public QueryModelClass createFromCursor(@NonNull Cursor cursor) {
        QueryModelClass model = newInstance();
        List<String> columnsOrdered = new ArrayList<>(Arrays.asList(cursor.getColumnNames()));

        for (Field field : queryModelInfo.getFields()) {
            field.setAccessible(true);
            Class<?> fieldType = field.getType();
            ColumnInfo columnInfo = queryModelInfo.getColumnInfo(field);
            String columnName = columnInfo.name;
            int columnIndex = columnsOrdered.indexOf(columnName);
            if (columnIndex == -1 || cursor.isNull(columnIndex)) {
                continue;
            }

            Object value;
            if (ReflectionUtils.isModel(fieldType)) {
                value = getModelFieldValue(fieldType, cursor, columnIndex);
            } else {
                value = SQLiteUtils.getColumnFieldValue(queryModelInfo.getModelClass(), fieldType, cursor, columnIndex);
            }

            try {
                if (value != null) {
                    field.set(model, value);
                }
            } catch (IllegalArgumentException | IllegalAccessException | SecurityException e) {
                ReActiveLog.e(LogLevel.BASIC, e.getClass().getName(), e);
            }
        }

        return model;
    }

    private Object getModelFieldValue(Class<?> fieldType, Cursor cursor, int columnIndex) {
        long entityId = cursor.getLong(columnIndex);
        String foreignKeyIdName = ReActiveAndroid.getTableInfo(fieldType).getPrimaryKeyColumnName();
        return Select.from(fieldType).where(foreignKeyIdName + "=?", entityId).fetchSingle();
    }

    private QueryModelClass newInstance() {
        try {
            return (QueryModelClass) queryModelInfo.getModelClass().getConstructor().newInstance();
        } catch (Exception e) {
            throw new IllegalStateException(queryModelInfo.getModelClass().getName() + " should have constructor without arguments");
        }
    }

    @NonNull
    private SQLiteDatabase getDatabase() {
        return ReActiveAndroid.getWritableDatabaseForTable(queryModelInfo.getModelClass());
    }

}
