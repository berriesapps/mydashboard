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

package com.berries.dashboard.adapters;

import java.text.DateFormat;
import java.util.Date;

import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.TextView;

import com.berries.dashboard.db.tables.Contract;

/**
 * Binds values from WheelValuesTable row to ListView
 */
public class WheelValuesBinder implements SimpleCursorAdapter.ViewBinder {
	private int mWheelNumOfItems;

	public WheelValuesBinder(int wheelNumOfItems) {
		super();
		mWheelNumOfItems = wheelNumOfItems;
	}

	@Override
	public boolean setViewValue(View view, Cursor cursor, int columnIndex) {

		if (view instanceof TextView) {

			// set the wheel values as a tag to this list item
			ValueHolder holder = new ValueHolder();
			holder.values = new int[mWheelNumOfItems];
			for (int column = 0; column < mWheelNumOfItems; column++) {
				int val = cursor.getInt(Contract.COLUMN_IDX_VAL0 + column);
				holder.values[column] = val;
			}
			holder.dateSaved = cursor.getLong(Contract.COLUMN_IDX_DATE);
			TextView listItemView = (TextView) view;
			listItemView.setTag(holder);

			// set the text to be displayed for this list item
			String formattedDateString = getFormattedDate(holder.dateSaved);
			listItemView.setText("Saved on: "+formattedDateString);
			return true;
		}

		return false;
	}

	public static String getFormattedDate(long timestamp) {
		DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
		DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
		Date date = new Date();
		date.setTime(timestamp);
		return dateFormat.format(date) + " " + timeFormat.format(date);
	}

	/**
	 * A wrapper holding an int [] array of values
	 */
	public class ValueHolder {
		public int[] values;
		public long dateSaved;
	}

}
