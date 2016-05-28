/*
 * Copyright 2016 Flinbor Aleksandr Bogdanov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package in.flinbor.demo.smsreceiver.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Content Provider with SQLite used as datalayer for
 * store received SMSObject
 */
public class SmsContentProvider extends ContentProvider {
    private SmsDatabaseHelper database;
    private static final int    MESSAGES        = 1;
    private static final int    MESSAGE_ID      = 2;
    private static final String AUTHORITY       = "in.flinbor.demo.smsreceiver";
    private static final String BASE_PATH       = "sms";
    public static final Uri     CONTENT_URI     = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);
    public static final String  CONTENT_TYPE    = ContentResolver.CURSOR_DIR_BASE_TYPE + "/messages";
    public static final String  CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/message";

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, MESSAGES);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", MESSAGE_ID);
    }

    @Override
    public boolean onCreate() {
        database = new SmsDatabaseHelper(getContext());
        return false;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        checkColumns(projection);

        queryBuilder.setTables(SmsTable.TABLE_SMS);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case MESSAGES:
                break;
            case MESSAGE_ID:
                queryBuilder.appendWhere(SmsTable.COLUMN_ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        long id;
        switch (uriType) {
            case MESSAGES:
                id = sqlDB.insert(SmsTable.TABLE_SMS, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        // no need to implement for current project
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // no need to implement for current project
        return 0;
    }

    private void checkColumns(String[] projection) {
        String[] available = {
                SmsTable.COLUMN_TIMESTAMP,
                SmsTable.COLUMN_CONTENT,
                SmsTable.COLUMN_ID,
                SmsTable.COLUMN_SENDER
        };
        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<>(Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<>(Arrays.asList(available));
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException("Unknown columns in projection");
            }
        }
    }
}
