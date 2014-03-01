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
 * Definition of WHEEL_TYPES_TABLE. The table has the following columns
 * <p>_id, title, item0, item1, item2, item3, item4, item5, item6, item7, date, count</p>
 */
public class WheelTypesTable {


	public static final String TABLE_NAME = "WHEEL_TYPES";

	// Database creation SQL statement
	private static final String DATABASE_CREATE_START = "create table " + TABLE_NAME;
	private static final String DATABASE_CREATE_MID = " (" + Contract.COLUMN_ID  + " integer primary key autoincrement, "+ Contract.COLUMN_TITLE+ " TEXT NOT NULL, ";
	private static final String DATABASE_CREATE_END = Contract.COLUMN_DATE		+ " INTEGER NOT NULL DEFAULT (strftime('%s','now')), "+ Contract.COLUMN_COUNT  +" INTEGER NOT NULL DEFAULT 8);";
	

	private static final String DB_INSERT_CMD =   "insert into "+ TABLE_NAME + " (title, item0, item1, item2, item3, item4, item5, item6, item7, count)" ;
	
	private static final String DATABASE_INSERT1 = DB_INSERT_CMD +
			" VALUES ('Wheel of Life', 'Environment', 'Career', 'Money', 'Health', 'Friends&Family', 'Significant Other','Personal Growth','Recreation/Fun', 8);";
	private static final String DATABASE_INSERT2 = "insert into "+ TABLE_NAME + " (title, item0, item1, item2, item3, item4, item5, item6, item7, count)" +
			" VALUES ('Keeping Healthy', 'Healthy Eating', 'Drinking Water', 'Exercise', 'Meditation', 'Sleep Well', 'Me time/Hobbies','','', 6);";	
	private static final String DATABASE_INSERT3 = "insert into "+ TABLE_NAME + " (title, item0, item1, item2, item3, item4, item5, item6, item7, count)" +
			" VALUES ('My Mood Today', 'Happy', 'Optimistic', 'Motivated', 'Confident', 'Excited','Playful','Loved', 'Relaxed', 8);";
	private static final String DATABASE_INSERT4 = DB_INSERT_CMD +
			" VALUES ('Working Skills', 'Communication', 'Fast-Learning', 'Team Player', 'Creative Thinking', 'Problem Solving', 'Task Management', 'Motivation', 'Independence', 8);";
	private static final String DATABASE_INSERT5 = DB_INSERT_CMD +
			" VALUES ('Mobile Developer Skills', 'Android', 'Java', 'iOS', 'ObjectiveC', 'C/C++', 'Multithreading', 'Design Patterns', 'HTML5/CSS/Javascript', 8);";
	private static final String DATABASE_INSERT6 = DB_INSERT_CMD +
			" VALUES ('Mobile App Health', 'OO Design', 'Short and Clear Code','Documentation', 'Input Validation','Thread Happy', 'Performance', 'Memory Usage',  'Battery Consumption', 8);";
		

	public static void onCreate(SQLiteDatabase database) {
		StringBuilder sqlBuilder = new StringBuilder(DATABASE_CREATE_START);
		sqlBuilder.append(DATABASE_CREATE_MID);
		for (int i = 0; i < Contract.MAX_ITEMS_IN_WHEEL; i++) {
			sqlBuilder.append(Contract.COLUMN_ITEM);
			sqlBuilder.append(i);
			sqlBuilder.append(" TEXT, ");
		}
		sqlBuilder.append(DATABASE_CREATE_END);
		database.execSQL(sqlBuilder.toString());				
		database.execSQL(DATABASE_INSERT1);
		database.execSQL(DATABASE_INSERT2);
		database.execSQL(DATABASE_INSERT3);	
		database.execSQL(DATABASE_INSERT4);	
		database.execSQL(DATABASE_INSERT5);
		database.execSQL(DATABASE_INSERT6);
	}


	public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		// no need to do anything at the moment because we are not upgrading
	}

}
