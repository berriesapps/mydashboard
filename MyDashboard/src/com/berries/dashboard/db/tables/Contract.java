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


/**
 * Definition of column names and indexes
 */
public class Contract {

	public static final int MAX_ITEMS_IN_WHEEL = 8;	
	public static final int MIN_ITEMS_IN_WHEEL = 2;
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_TITLE = "title";
	public static final String COLUMN_TYPE_ID = "typeId";
	public static final String COLUMN_ITEM0 = "item0";
	public static final String COLUMN_ITEM = "item";
	public static final String COLUMN_VALUE0 = "value0";
	public static final String COLUMN_VALUE = "value";
	public static final String COLUMN_DATE = "date";
	public static final String COLUMN_COUNT = "count";
	public static final int COLUMN_IDX_ID = 0;
	public static final int COLUMN_IDX_TITLE = 1;
	public static final int COLUMN_IDX_TYPE_ID = COLUMN_IDX_TITLE;	
	public static final int COLUMN_IDX_ITEM0 = 2;
	public static final int COLUMN_IDX_VAL0 = COLUMN_IDX_ITEM0;
	public static final int COLUMN_IDX_DATE = 10;
	public static final int COLUMN_IDX_COUNT = 11;	

}
