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

import com.berries.dashboard.db.WheelContentProvider;
import com.berries.dashboard.db.tables.Contract;
import com.berries.dashboard.model.Wheel;
import com.berries.dashboard.model.WheelItem;
import com.berries.dashboard.R;
import com.berries.dashboard.event.WheelTypeClickListener;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.support.v4.content.CursorLoader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.Toast;

/**
 * This class contains methods that connect between the WheelTypes table to views displaying it
 * Use {@link #getAdapter(Context, android.view.View.OnClickListener)} to retrieve an adapter that maps WheelTypesTable row structure to grid views  
 */
public class WheelTypesConnector  {

	/**
	 * Generates a SimpleCursorAdapter that will map WheelTypesTable row structure to grid views
	 * @param context
	 * @param listener
	 * @return
	 */
	public static SimpleCursorAdapter getAdapter(Activity activity, WheelTypeClickListener listener){
		// create an adapter that will connect each table row to the view within the grid cell 
		String [] from = { Contract.COLUMN_ID }; 
		int [] to = { R.id.wheeltypes_grid_item_label };
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(activity, R.layout.wheel_type_grid_item, null, from, to, 0);
		adapter.setViewBinder(new WheelTypesBinder(activity, listener));
		return adapter;
	}
	


	/**
	 * Delete this type of wheel both from WheelTypesTable & WheelValuesTable
	 * @param context
	 * @param wheelTypeId
	 * @return int number of rows deleted from WheelTypesTable
	 */
	public static int deleteWheelType(Context context, int wheelTypeId){
		
		// first let's delete all rows in WheelValuesTable that reference this wheel type id		
		String where = Contract.COLUMN_TYPE_ID + " = ?";
		String [] selectionArgs = {String.valueOf(wheelTypeId)};		
		int result = context.getContentResolver().delete(WheelContentProvider.CONTENT_PROGRESS_SINGLE_URI, where, selectionArgs);
		
		if ( result >= 0 ){
			// now that all wheel values for this wheel type have been deleted, we can safely delete row from WheelTypesTable
			where = Contract.COLUMN_ID + " = ?";
			result = context.getContentResolver().delete(WheelContentProvider.CONTENT_WHEEL_TYPES_SINGLE_URI, where, selectionArgs);
		}
		
		return result;
	}
	
	/**
	 * Save this wheel type in the WheelTypes table
	 * @param context
	 * @param wheel the Wheel object containing the title & item strings to save
	 * @param insertNew true if we are inserting a new row, false if just updating
	 */
	public static void saveWheelType(Context context, Wheel wheel, boolean insertNew) {
		// save the edited/new wheel in the wheel types table
		String title = wheel.getTitle();
		ContentValues values = new ContentValues();
		values.put(Contract.COLUMN_TITLE, title);
		for (int i = 0; i < wheel.getNumOfItems(); i++) {
			WheelItem item = wheel.getItemAt(i);
			values.put(Contract.COLUMN_ITEM + i, item.getName());
		}
		values.put(Contract.COLUMN_COUNT, wheel.getNumOfItems());
		Calendar cal = Calendar.getInstance();
		values.put(Contract.COLUMN_DATE, cal.getTimeInMillis());
		
		if (insertNew){
			// add new wheel -> insert new row into table
			Uri uriresult = context.getContentResolver().insert(WheelContentProvider.CONTENT_WHEEL_TYPES_SINGLE_URI, values);
			Toast.makeText(context, "New Wheel Saved " + uriresult.toString(), Toast.LENGTH_LONG).show();
		}
		else {
			// edit wheel -> update existing row
			String where = Contract.COLUMN_ID + " = ?";
			String [] selectionArgs = {String.valueOf(wheel.getTypeId())}; 
			int result = context.getContentResolver().update(WheelContentProvider.CONTENT_WHEEL_TYPES_SINGLE_URI, values, where, selectionArgs);
			Toast.makeText(context, "Updated, result: "+result, Toast.LENGTH_LONG).show();			
		}
	}
	

	/**
	 * Constructs and returns a CursorLoader for the given wheel. The loader is responsible for retrieving all wheels of the same type as the type of the wheel passed. 
	 * @param context
	 * @param wheel
	 * @return
	 */
	public static CursorLoader getQueryCursorLoaderForWheelType(Context context, Wheel wheel) {
		Uri uri = WheelContentProvider.CONTENT_PROGRESS_URI;
		String selection = Contract.COLUMN_TYPE_ID + " = ?";
		String [] selectionArgs = { String.valueOf(wheel.getTypeId())  };
		CursorLoader cursorLoader = new CursorLoader(context, uri, null, selection, selectionArgs, null);
		return cursorLoader;
	}
	
	
	// don't want anyone to create an object of this class
	private WheelTypesConnector(){}
}
