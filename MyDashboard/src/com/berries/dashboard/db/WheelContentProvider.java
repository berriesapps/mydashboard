/*
The MIT License (MIT)

Copyright (c) 2013 Berry Ventura, berriesapps@gmail.com

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
 */
package com.berries.dashboard.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.berries.dashboard.db.tables.Contract;
import com.berries.dashboard.db.tables.WheelTypesTable;
import com.berries.dashboard.db.tables.WheelValuesTable;

/**
 * Handles access to WheelTypesTable & WheelValuesTable
 */
public class WheelContentProvider extends ContentProvider {

	private WheelDBManager mDatabase;

	private static final String SINGLE_RESULTS_LIMIT = "1";
	private static final String MULTI_RESULTS_LIMIT = "50";
	private static final String BASE_PATH = "wheelprogress";
	private static final String WHEEL_TYPES_PATH = "wheeltypes";

	public static final Uri CONTENT_BASE_URI = Uri
			.parse("content://com.berries.dashboard.wheelcontent/");
	public static final Uri CONTENT_PROGRESS_SINGLE_URI = Uri
			.parse("content://com.berries.dashboard.wheelcontent/" + BASE_PATH);
	public static final Uri CONTENT_PROGRESS_URI = Uri
			.parse("content://com.berries.dashboard.wheelcontent/" + BASE_PATH
					+ "/*");
	public static final Uri CONTENT_WHEEL_TYPES_SINGLE_URI = Uri
			.parse("content://com.berries.dashboard.wheelcontent/"
					+ WHEEL_TYPES_PATH);
	public static final Uri CONTENT_WHEEL_TYPES_URI = Uri
			.parse("content://com.berries.dashboard.wheelcontent/"
					+ WHEEL_TYPES_PATH + "/*");

	private static final int WHEEL_VALUES_TABLE_CODE_BASE = 100;
	private static final int WHEEL_SINGLE = WHEEL_VALUES_TABLE_CODE_BASE + 0;
	private static final int WHEEL_SINGLE_ID = WHEEL_VALUES_TABLE_CODE_BASE + 1;
	private static final int WHEEL_PROGRESS = WHEEL_VALUES_TABLE_CODE_BASE + 2;

	private static final int WHEEL_TYPES_TABLE_CODE_BASE = 200;
	private static final int WHEEL_TYPE_SINGLE = WHEEL_TYPES_TABLE_CODE_BASE + 0;
	private static final int WHEEL_TYPE_SINGLE_ID = WHEEL_TYPES_TABLE_CODE_BASE + 1;
	private static final int WHEEL_TYPE_LIST = WHEEL_TYPES_TABLE_CODE_BASE + 2;

	private static final String AUTHORITY = "com.berries.dashboard.wheelcontent";
	private static final UriMatcher sURIMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);

	static {
		sURIMatcher.addURI(AUTHORITY, BASE_PATH, WHEEL_SINGLE);
		sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", WHEEL_SINGLE_ID);
		sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/*", WHEEL_PROGRESS);

		sURIMatcher.addURI(AUTHORITY, WHEEL_TYPES_PATH, WHEEL_TYPE_SINGLE);
		sURIMatcher.addURI(AUTHORITY, WHEEL_TYPES_PATH + "/#", WHEEL_TYPE_SINGLE_ID);
		sURIMatcher.addURI(AUTHORITY, WHEEL_TYPES_PATH + "/*", WHEEL_TYPE_LIST);
	}

	public WheelContentProvider() {
	}

	@Override
	public boolean onCreate() {
		mDatabase = new WheelDBManager(getContext());
		return false;
	}

	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		// Using SQLiteQueryBuilder instead of query() method
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		String selectionToUse = selection;
		String[] selectionArgsToUse = selectionArgs;

		// Set the table
		int uriType = sURIMatcher.match(uri);
		String limit = null;

		if (uriType >= WHEEL_TYPES_TABLE_CODE_BASE) {
			queryBuilder.setTables(WheelTypesTable.TABLE_NAME);
		} else {
			queryBuilder.setTables(WheelValuesTable.TABLE_NAME);
			if (sortOrder == null || (sortOrder.trim().length() == 0)) {
				sortOrder = Contract.COLUMN_DATE + " DESC";
			}
		}

		if (uriType == WHEEL_SINGLE) {
			limit = SINGLE_RESULTS_LIMIT;
		} else if ((uriType == WHEEL_TYPE_LIST) || (uriType == WHEEL_PROGRESS)) {
			limit = MULTI_RESULTS_LIMIT;
		} else if ((uriType == WHEEL_TYPE_SINGLE_ID)
				|| (uriType == WHEEL_SINGLE_ID)) {
			limit = SINGLE_RESULTS_LIMIT;
			String id = uri.getLastPathSegment();
			selectionToUse = addFirstToSelection(Contract.COLUMN_ID,
					selectionToUse);
			selectionArgsToUse = addFirstToSelectionArgs(id, selectionArgs);
		} else {
			throw new IllegalArgumentException("Unsupported URI for db query: "
					+ uri);
		}

		SQLiteDatabase db = mDatabase.getWritableDatabase();

		Cursor cursor = queryBuilder.query(db, projection, selectionToUse,
				selectionArgsToUse, null, null, sortOrder, limit);
		// Make sure that potential listeners are getting notified
		cursor.setNotificationUri(getContext().getContentResolver(), uri);

		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		Uri retUri = uri;
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = mDatabase.getWritableDatabase();
		long id = 0;
		switch (uriType) {
		case WHEEL_SINGLE:
			id = sqlDB.insert(WheelValuesTable.TABLE_NAME, null, values);
			retUri = Uri.parse(BASE_PATH + "/" + id);
			break;
		case WHEEL_TYPE_SINGLE:
			id = sqlDB.insert(WheelTypesTable.TABLE_NAME, null, values);
			retUri = Uri.parse(WHEEL_TYPES_PATH + "/" + id);
			break;
		default:
			throw new IllegalArgumentException(
					"Unsupported URI for db insert: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return retUri;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {

		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = mDatabase.getWritableDatabase();
		String tableName = "";
		String selectionToUse = selection;
		String[] selectionArgsToUse = selectionArgs;
		String id;
		int rowsDeleted = 0;
		boolean addId = false;

		switch (uriType) {
		case WHEEL_SINGLE:
			tableName = WheelValuesTable.TABLE_NAME;
			break;
		case WHEEL_SINGLE_ID:
			tableName = WheelValuesTable.TABLE_NAME;
			addId = true;
			break;
		case WHEEL_TYPE_SINGLE:
			tableName = WheelTypesTable.TABLE_NAME;
			break;
		case WHEEL_TYPE_SINGLE_ID:
			tableName = WheelTypesTable.TABLE_NAME;
			addId = true;
			break;
		default:
			throw new IllegalArgumentException(
					"Unsupported URI for db delete: " + uri);
		}

		if (addId) {
			id = uri.getLastPathSegment();
			selectionToUse = addFirstToSelection(Contract.COLUMN_ID, selection);
			selectionArgsToUse = addFirstToSelectionArgs(id, selectionArgs);
		}

		rowsDeleted = sqlDB.delete(tableName, selectionToUse,
				selectionArgsToUse);
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsDeleted;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		String tableName = "";
		String selectionToUse = selection;
		String[] selectionArgsToUse = selectionArgs;
		int uriType = sURIMatcher.match(uri);
		String id;
		SQLiteDatabase sqlDB = mDatabase.getWritableDatabase();
		int rowsUpdated = 0;
		switch (uriType) {
		case WHEEL_SINGLE:
			tableName = WheelValuesTable.TABLE_NAME;
			break;
		case WHEEL_SINGLE_ID:
			tableName = WheelValuesTable.TABLE_NAME;
			id = uri.getLastPathSegment();
			selectionToUse = addFirstToSelection(Contract.COLUMN_ID, selection);
			selectionArgsToUse = addFirstToSelectionArgs(id, selectionArgs);
			break;
		case WHEEL_TYPE_SINGLE:
			tableName = WheelTypesTable.TABLE_NAME;
			break;
		case WHEEL_TYPE_SINGLE_ID:
			tableName = WheelTypesTable.TABLE_NAME;
			id = uri.getLastPathSegment();
			selectionToUse = addFirstToSelection(Contract.COLUMN_ID, selection);
			selectionArgsToUse = addFirstToSelectionArgs(id, selectionArgs);
			break;
		default:
			throw new IllegalArgumentException(
					"Unsupported URI for db update: " + uri);
		}
		rowsUpdated = sqlDB.update(tableName, values, selectionToUse,
				selectionArgsToUse);
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsUpdated;
	}

	private String addFirstToSelection(String columnIdString, String selection) {
		String newSelection;
		if (TextUtils.isEmpty(selection)) {
			newSelection = columnIdString + "= ? ";
		} else {
			newSelection = columnIdString + "= ? " + " and " + selection;
		}
		return newSelection;
	}

	private String[] addFirstToSelectionArgs(String id, String[] arr) {
		String[] retArgs;

		if (arr == null) {
			retArgs = new String[1];
		} else {
			retArgs = new String[arr.length + 1];
			for (int i = 1; i < retArgs.length; i++) {
				retArgs[i] = arr[i - 1];
			}
		}
		retArgs[0] = id;
		return retArgs;
	}

}
