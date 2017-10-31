package com.reactiveandroid.utils;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.SparseArray;

import com.reactiveandroid.ReActiveAndroid;
import com.reactiveandroid.annotation.Collate;
import com.reactiveandroid.annotation.Unique;
import com.reactiveandroid.database.ReActiveMasterTable;
import com.reactiveandroid.database.table.ColumnInfo;
import com.reactiveandroid.database.table.IndexGroupInfo;
import com.reactiveandroid.database.table.SQLiteType;
import com.reactiveandroid.database.table.TableInfo;
import com.reactiveandroid.annotation.Column;
import com.reactiveandroid.database.table.UniqueGroupInfo;
import com.reactiveandroid.internal.log.LogLevel;
import com.reactiveandroid.internal.log.ReActiveLog;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class SQLiteUtils {

    @NonNull
    public static String getIdentityHash(Collection<TableInfo> tableInfos) {
        ArrayList<String> tableDefinitions = new ArrayList<>();
        for (TableInfo tableInfo : tableInfos) {
            tableDefinitions.add(createTableDefinition(tableInfo));
        }
        for (TableInfo tableInfo : tableInfos) {
            tableDefinitions.addAll(SQLiteUtils.createIndexDefinition(tableInfo));
        }
        Collections.sort(tableDefinitions, new AlphabeticalComparator());

        return getDatabaseSchemaHash(tableDefinitions);
    }

    @NonNull
    public static ArrayList<String> getAllTableNames(SQLiteDatabase database) {
        Cursor cursor = database.rawQuery("SELECT name FROM sqlite_master  WHERE type='table' ORDER BY name;", new String[]{});
        int nameColumnIndex = cursor.getColumnIndex("name");

        cursor.moveToFirst();
        ArrayList<String> tableNames = new ArrayList<>();
        do {
            String tableName = cursor.getString(nameColumnIndex);
            if (!isInternalTable(tableName)) {
                tableNames.add(cursor.getString(nameColumnIndex));
            }

        } while (cursor.moveToNext());

        return tableNames;
    }

    @NonNull
    public static String createTableDefinition(TableInfo tableInfo) {
        ArrayList<String> definitions = new ArrayList<>();
        for (Field field : tableInfo.getFields()) {
            String definition = createColumnDefinition(tableInfo, field);
            if (!TextUtils.isEmpty(definition)) {
                definitions.add(definition);
            }
        }

        definitions.addAll(createUniqueDefinition(tableInfo));

        return String.format("CREATE TABLE IF NOT EXISTS `%s` (%s);", tableInfo.getTableName(), TextUtils.join(", ", definitions));
    }

    @NonNull
    @SuppressWarnings("unchecked")
    static String createColumnDefinition(TableInfo tableInfo, Field field) {
        StringBuilder definition = new StringBuilder();

        Column column = field.getAnnotation(Column.class);
        Unique uniqueAnnotation = field.getAnnotation(Unique.class);
        Class type = field.getType();
        ColumnInfo columnInfo = tableInfo.getColumnInfo(field);
        SQLiteType sqliteType = columnInfo.type;

        definition.append("`");
        definition.append(columnInfo.name);
        definition.append("`");
        definition.append(" ");
        definition.append(sqliteType.getName());

        if (!TextUtils.isEmpty(definition)) {
            if (columnInfo.name.equals(tableInfo.getPrimaryKeyColumnName())) {
                definition.append(" PRIMARY KEY AUTOINCREMENT");
            } else if (column != null) {
                if (column.length() > -1) {
                    definition.append("(");
                    definition.append(column.length());
                    definition.append(")");
                }

                if (column.collate() != Collate.NONE) {
                    definition.append(" COLLATE ");
                    definition.append(column.collate().toString());
                }

                if (column.notNull()) {
                    definition.append(" NOT NULL ON CONFLICT ");
                    definition.append(column.onNullConflict().toString());
                }

                if (uniqueAnnotation != null && uniqueAnnotation.unique()) {
                    definition.append(" UNIQUE ON CONFLICT ");
                    definition.append(uniqueAnnotation.onUniqueConflict().toString());
                }

            }

            if (ReflectionUtils.isModel(type)) {
                TableInfo foreignKeyTableInfo = ReActiveAndroid.getTableInfo(type);
                definition.append(" REFERENCES ");
                definition.append(foreignKeyTableInfo.getTableName());
                definition.append("(`").append(foreignKeyTableInfo.getPrimaryKeyColumnName()).append("`)");
                definition.append(" ON DELETE ");
                definition.append(column.onDelete().toString().replace("_", " "));
                definition.append(" ON UPDATE ");
                definition.append(column.onUpdate().toString().replace("_", " "));
            }
        } else {
            ReActiveLog.e(LogLevel.BASIC, "No type mapping for: " + type.toString());
        }

        return definition.toString();
    }

    @NonNull
    static List<String> createUniqueDefinition(TableInfo tableInfo) {
        ArrayList<String> definitions = new ArrayList<>();
        SparseArray<UniqueGroupInfo> uniqueGroups = tableInfo.getUniqueGroups();
        for (int i = 0; i < uniqueGroups.size(); i++) {
            int key = uniqueGroups.keyAt(i);
            UniqueGroupInfo uniqueGroupInfo = uniqueGroups.get(key);
            if (uniqueGroupInfo.columns.size() > 0) {
                definitions.add(String.format("UNIQUE (%s) ON CONFLICT %s",
                        join(uniqueGroupInfo.columns),
                        uniqueGroupInfo.uniqueConflict.toString()));
            }
        }
        return definitions;
    }

    @NonNull
    public static List<String> createIndexDefinition(TableInfo tableInfo) {
        ArrayList<String> definitions = new ArrayList<>();
        SparseArray<IndexGroupInfo> indexGroups = tableInfo.getIndexGroups();
        for (int i = 0; i < indexGroups.size(); i++) {
            int key = indexGroups.keyAt(i);
            IndexGroupInfo indexGroupInfo = indexGroups.get(key);
            String name = indexGroupInfo.name != null ? indexGroupInfo.name : tableInfo.getTableName();
            if (indexGroupInfo.columns.size() > 0) {
                definitions.add(String.format("CREATE INDEX IF NOT EXISTS %s on %s(%s);",
                        "index_" + tableInfo.getTableName() + "_" + name,
                        tableInfo.getTableName(),
                        join(indexGroupInfo.columns)));
            }
        }
        return definitions;
    }


    private static boolean isInternalTable(String tableName) {
        return tableName.equals("android_metadata")
                || tableName.equals("sqlite_sequence")
                || tableName.equals(ReActiveMasterTable.TABLE_NAME);
    }

    @NonNull
    private static String getDatabaseSchemaHash(ArrayList<String> tableDefinitions) {
        String databaseSchema = TextUtils.join("\n", tableDefinitions);
        return MiscUtils.md5Hex(databaseSchema);
    }

    @NonNull
    private static String join(List<ColumnInfo> columnInfos) {
        StringBuilder sb = new StringBuilder();
        boolean firstTime = true;
        for (ColumnInfo columnInfo : columnInfos) {
            if (firstTime) {
                firstTime = false;
            } else {
                sb.append(", ");
            }
            sb.append("`");
            sb.append(columnInfo.name);
            sb.append("`");
        }
        return sb.toString();
    }

}
