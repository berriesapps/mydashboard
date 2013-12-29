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

import java.util.Calendar;

import com.berries.dashboard.R;
import com.berries.dashboard.db.WheelContentProvider;
import com.berries.dashboard.db.tables.Contract;
import com.berries.dashboard.model.Wheel;
import com.berries.dashboard.model.WheelItem;

import android.content.ContentValues;
import android.content.Context;
import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.Toast;

/**
 * This class contains methods that connect between the WheelValues table to views displaying it
 * Use {@link #getAdapter(Context, int)} to retrieve an adapter that maps WheelValuesTable row structure to list view  
 */
public class WheelValuesConnector {

	public static SimpleCursorAdapter getAdapter(Context context, int wheelNumOfItems){
		String [] from = { Contract.COLUMN_DATE};
		int [] to = { R.id.list_item_label };
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(context, R.layout.list_item, null, from, to, 0); 		
		adapter.setViewBinder(new WheelValuesBinder(wheelNumOfItems));
		return adapter;
	}

	
	public static void saveWheelValues(Context context, Wheel wheel) {
		// save the wheel values to the progress table
		ContentValues values = new ContentValues();
		values.put(Contract.COLUMN_TYPE_ID, wheel.getTypeId());
		for (int i = 0; i < wheel.getNumOfItems(); i++) {
			WheelItem item = wheel.getItemAt(i);
			values.put(Contract.COLUMN_VALUE + i, item.getValue());
		}
		Calendar cal = Calendar.getInstance();
		values.put(Contract.COLUMN_DATE, cal.getTimeInMillis());
		context.getContentResolver().insert(WheelContentProvider.CONTENT_PROGRESS_SINGLE_URI, values);
		Toast.makeText(context, R.string.message_values_saved, Toast.LENGTH_LONG).show();
	}	
	
	// don't want anyone to create an object of this class
	private WheelValuesConnector(){		
	}
}
