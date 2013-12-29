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

import java.util.ArrayList;

import android.app.Activity;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.TextView;

import com.berries.dashboard.db.tables.Contract;
import com.berries.dashboard.event.WheelTypeClickListener;
import com.berries.dashboard.handlers.ResourcesHandler;
import com.berries.dashboard.model.Wheel;
import com.berries.dashboard.model.WheelItem;

/**
 * Binds WheelTypesTable row structure to grid views
 */
public class WheelTypesBinder implements SimpleCursorAdapter.ViewBinder
{
	WheelTypeClickListener mClickListener;
	Activity         mActivity;
	
	public WheelTypesBinder(Activity activity, WheelTypeClickListener clickListener){
		super();
		mClickListener = clickListener;
		mActivity = activity;
	}
	
	

	@Override
	public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
		
		if ( view instanceof TextView ){
		
			int id = cursor.getInt(Contract.COLUMN_IDX_ID);
			int numOfItemsInWheel = cursor.getInt(Contract.COLUMN_IDX_COUNT);
			String wheelName = cursor.getString(Contract.COLUMN_IDX_TITLE);
			
			int [] colorList = ResourcesHandler.getAppColorList(mActivity);
			ArrayList<WheelItem> listOfItems = new ArrayList<WheelItem>(numOfItemsInWheel);
			for (int column = 0; column < numOfItemsInWheel; column++) {					
				String itemName = cursor.getString(Contract.COLUMN_IDX_ITEM0+column);
				listOfItems.add(column, new WheelItem(itemName, colorList[column]));
			}
			Wheel wheel = new Wheel(id, wheelName, listOfItems);

			TextView label = (TextView)view;
			label.setOnClickListener(mClickListener);
			label.setOnLongClickListener(mClickListener);
			mActivity.registerForContextMenu(label);
			label.setTag(wheel);
			label.setText(wheelName);
		
			return true;
		}
		
		return false;
	}				
	
}	
