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
package com.berries.dashboard.activities;

import com.berries.dashboard.R;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.support.v4.app.FragmentActivity;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;

import com.berries.dashboard.adapters.WheelTypesConnector;
import com.berries.dashboard.adapters.WheelValuesConnector;
import com.berries.dashboard.adapters.WheelValuesBinder;
import com.berries.dashboard.model.Wheel;
import com.berries.dashboard.utils.AppKeys;
import com.berries.dashboard.views.*;

/**
 * <p> 
 * The Activity displays a list of appointments on which the user has previously set values for a certain wheel. 
 * Each time the user clicks on a list item, the wheel values are updated in the wheel view displayed
 * Animation is used to move from one set of values to another
 * </p>
 */
public class WheelProgressActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor>, 
																			 AdapterView.OnItemClickListener {

	private WheelGroupView 	mWheelGroupView;
    private SimpleCursorAdapter mAdapter;
    private ListView mList;
     
  
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

 
		setContentView(R.layout.activity_progress);
		setTitle(R.string.activity_title_show_progress);
		
		Bundle extras = this.getIntent().getExtras();
		Wheel wheel = extras.getParcelable(AppKeys.WHEEL_PARCEL_KEY);
		wheel.resetValues(); // in progress view we want to initially show all items at max value
		mAdapter = WheelValuesConnector.getAdapter(getBaseContext(), wheel.getNumOfItems());
						
		mWheelGroupView = (WheelGroupView) this.findViewById(R.id.WheelProgressView);
		mWheelGroupView.setWheel(wheel);
		mWheelGroupView.setDraggable(false);
		mWheelGroupView.setEditable(false);
		mWheelGroupView.setShowTextViews(false);

        mList = (ListView)findViewById(android.R.id.list);   
        mList.setScrollBarStyle(ListView.SCROLLBARS_OUTSIDE_INSET);       
        mList.setOnItemClickListener(this);
        mList.setAdapter(mAdapter);
		loadWheels();
				
	}

	private void loadWheels() {
		LoaderCallbacks<Cursor> callbackHandler = (LoaderCallbacks<Cursor>) this;
		LoaderManager manager = this.getSupportLoaderManager();
		manager.restartLoader(0, null, callbackHandler);
	}


	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		CursorLoader cursorLoader = WheelTypesConnector.getQueryCursorLoaderForWheelType(getBaseContext(), mWheelGroupView.getWheel());
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		// The loading finished
		mAdapter.swapCursor(cursor);
	}
	

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}
	

    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
    		
    		Object tag = v.getTag();

    		if ( tag instanceof WheelValuesBinder.ValueHolder ){
    		
    			WheelValuesBinder.ValueHolder valueHolder = (WheelValuesBinder.ValueHolder)tag;
        		Wheel nextAppointmentWheel = mWheelGroupView.getWheel().copy();
 
				for (int i=0;  i<nextAppointmentWheel.getNumOfItems(); i++){
					nextAppointmentWheel.getItemAt(i).setValue(valueHolder.values[i]);
    			}
    			
			    // move to the next appointment wheel values using animation
				mWheelGroupView.animateTo(nextAppointmentWheel);
    		}    		
    }
		
}
		