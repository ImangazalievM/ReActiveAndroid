package com.reactiveandroid;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.SparseArray;

import com.reactiveandroid.utils.ContentUtils;

// ToDo: do it later
public class ReActiveContentProvider extends ContentProvider {

    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    private static final SparseArray<Class<? extends Model>> TYPE_CODES = new SparseArray<>();

    private static String authority;
    private static SparseArray<String> mimeTypeCache = new SparseArray<>();

    public static String getAuthority() {
        return authority;
    }

    @Override
    public boolean onCreate() {
        ReActiveAndroid.init(getConfiguration());
        authority = getContext().getPackageName();


        //List<TableInfo> tableInfos = new ArrayList<>();
        //int size = tableInfos.size();

        // for (int i = 0; i < size; i++) {
        //     final TableInfo tableInfo = tableInfos.get(i);
        //     final int tableKey = (i * 2) + 1;
        //     final int itemKey = (i * 2) + 2;
//
        //     // content://<authority>/<table>
        //     URI_MATCHER.addURI(authority, tableInfo.getTableName().toLowerCase(), tableKey);
        //     TYPE_CODES.put(tableKey, tableInfo.getTableClass());
//
        //     // content://<authority>/<table>/<id>
        //     URI_MATCHER.addURI(authority, tableInfo.getTableName().toLowerCase() + "/#", itemKey);
        //     TYPE_CODES.put(itemKey, tableInfo.getTableClass());
        // }

        return true;
    }

    @Override
    public String getType(Uri uri) {
        int match = URI_MATCHER.match(uri);

        String cachedMimeType = mimeTypeCache.get(match);
        if (cachedMimeType != null) {
            return cachedMimeType;
        }

        Class<? extends Model> table = getModelTableClass(uri);
        boolean single = ((match % 2) == 0);

        String mimeType = String.format("vnd.%s.%s/vnd.%s.%s", authority, single ? "item" : "dir", authority, ReActiveAndroid.getTableName(table));
        mimeTypeCache.append(match, mimeType);

        return mimeType;
    }

    // SQLite methods

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Class<? extends Model> table = getModelTableClass(uri);
        Long id = ReActiveAndroid.getWritableDatabaseForTable(table)
                .insert(ReActiveAndroid.getTableName(table), null, values);

        if (id != null && id > 0) {
            Uri retUri = ContentUtils.createUri(authority, table, id);
            notifyChange(retUri);

            return retUri;
        }

        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Class<? extends Model> table = getModelTableClass(uri);
        int count = getDatabase(table).update(ReActiveAndroid.getTableName(table), values, selection, selectionArgs);

        notifyChange(uri);

        return count;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Class<? extends Model> table = getModelTableClass(uri);
        int count = getDatabase(table)
                .delete(ReActiveAndroid.getTableName(table), selection, selectionArgs);

        notifyChange(uri);

        return count;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Class<? extends Model> table = getModelTableClass(uri);
        Cursor cursor = getDatabase(table)
                .query(ReActiveAndroid.getTableName(table), projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    protected ReActiveConfig getConfiguration() {
        return new ReActiveConfig.Builder(getContext()).build();
    }

    private Class<? extends Model> getModelTableClass(Uri uri) {
        int code = URI_MATCHER.match(uri);
        if (code != UriMatcher.NO_MATCH) {
            return TYPE_CODES.get(code);
        }

        return null;
    }

    private SQLiteDatabase getDatabase(Class<? extends Model> table) {
        return ReActiveAndroid.getWritableDatabaseForTable(table);
    }

    private void notifyChange(Uri uri) {
        getContext().getContentResolver().notifyChange(uri, null);
    }

}
