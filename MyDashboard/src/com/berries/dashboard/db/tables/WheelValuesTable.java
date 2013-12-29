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
package com.berries.dashboard.db.tables;

import android.database.sqlite.SQLiteDatabase;

/**
 * Definition of WHEEL_PROGRESS. The table has the following columns
 * <p>_id, typeId(foreign key), value0, value1, value2, value3, value5, value6, value7, value8, date</p>
 */
public class WheelValuesTable {

	// Database table
	public static final String TABLE_NAME = "WHEEL_PROGRESS";
	
	// Database creation SQL statement
	private static final String DATABASE_CREATE_START = "create table " + TABLE_NAME;
	private static final String DATABASE_CREATE_MID = " (" + Contract.COLUMN_ID     + " integer primary key autoincrement, "
	                                                       + Contract.COLUMN_TYPE_ID+ " INTEGER NOT NULL, ";
	private static final String DATABASE_CREATE_END =        Contract.COLUMN_DATE	+ " INTEGER NOT NULL DEFAULT (strftime('%s','now'))," 
			                                               +"FOREIGN KEY("+Contract.COLUMN_TYPE_ID+") REFERENCES "+WheelTypesTable.TABLE_NAME+"("+Contract.COLUMN_ID+") );";


	public static void onCreate(SQLiteDatabase database) {
		StringBuilder sqlBuilder = new StringBuilder(DATABASE_CREATE_START);
		sqlBuilder.append(DATABASE_CREATE_MID);
		for (int i = 0; i < Contract.MAX_ITEMS_IN_WHEEL; i++) {
			sqlBuilder.append(Contract.COLUMN_VALUE);
			sqlBuilder.append(i);
			sqlBuilder.append(" INTEGER DEFAULT 10, ");
		}
		sqlBuilder.append(DATABASE_CREATE_END);
		database.execSQL(sqlBuilder.toString());		
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		// no need to do anything at the moment because we are not upgrading
	}

}
