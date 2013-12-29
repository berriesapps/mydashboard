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
import com.berries.dashboard.adapters.WheelTypesConnector;
import com.berries.dashboard.db.WheelContentProvider;
import com.berries.dashboard.dialogs.AlertDialogUtils;
import com.berries.dashboard.event.WheelTypeClickListener;
import com.berries.dashboard.handlers.ResourcesHandler;
import com.berries.dashboard.model.Wheel;
import com.berries.dashboard.model.WheelItem;
import com.berries.dashboard.utils.AppKeys;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

/**
 * <p> 
 * The Activity displays a grid of wheels. User can click on a wheel to display it's values, 
 * or click on edit/remove/add new buttons to manage the wheels
 * </p>
 */
public class WheelTypesManagerActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor>, 
																		   DialogInterface.OnClickListener,
																	       WheelTypeClickListener {
	 
	private SimpleCursorAdapter mAdapter;
	private GridView mGridView;
	private int mClickedWheelTypeId;
	private View mSelectedViewForAction;
	  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_types_manager);
		setTitle(R.string.activity_title_wheel_types);
		
		// setup the grid view
		mAdapter = WheelTypesConnector.getAdapter(this, this);
		mGridView = (GridView)this.findViewById(R.id.gridview);		
		mGridView.setAdapter(mAdapter);
		mGridView.setScrollBarStyle(GridView.SCROLLBARS_OUTSIDE_INSET);
		
		// load the list of wheel types from content provider
		loadWheelTypes();			
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
	                                ContextMenuInfo menuInfo) {
		//menuInfo
	    super.onCreateContextMenu(menu, v, menuInfo);
	    mSelectedViewForAction = v;
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.wheel_types_context_menu, menu);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.wheel_types_menu, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.action_help:
	        	AlertDialogUtils.showAlert(this, R.string.welcome_title, R.string.help_wheel_types);
	            return true;
	        case R.id.action_add:	        	
				Intent wheelViewIntent = new Intent(this, com.berries.dashboard.activities.WheelViewActivity.class);
				wheelViewIntent.putExtra(AppKeys.WHEEL_PARCEL_KEY,  generateTemplateWheel() );
				wheelViewIntent.putExtra(AppKeys.WHEEL_ACTIVITY_MODE_KEY, WheelViewActivity.MODE_ADD_NEW_WHEEL_TYPE);
				startActivity(wheelViewIntent);			            
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}	
	
	
	private void loadWheelTypes() {
		// start a loader that will load the wheels on the backgroun
		LoaderCallbacks<Cursor> callbackHandler = (LoaderCallbacks<Cursor>) this;
		LoaderManager manager = this.getSupportLoaderManager();
		manager.restartLoader(0, null, callbackHandler);
	}


	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		// Create a cursor loader that will retrieve all wheel types 
		Uri uri = WheelContentProvider.CONTENT_WHEEL_TYPES_URI;
		CursorLoader cursorLoader = new CursorLoader(this, uri, null, null, null, null);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		// The loading finished, swap the cursor
		mAdapter.swapCursor(cursor);
	}
	

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}

	public boolean onLongClick(View view) {
   		return false;
	}

	
	
	@Override
	public void onClick(View view) {
		// clicked on one of the views on the grid
		
		// view tag is always a wheel object
		Object tag = view.getTag();
		Wheel wheel = (Wheel)tag; 
		
		Intent wheelViewIntent;
		switch (view.getId()){
		
			case R.id.wheeltypes_grid_item_label:
				// start the WheelViewActivity activity passing the particular wheel object which was attached to the view
				wheelViewIntent = new Intent(this, com.berries.dashboard.activities.WheelViewActivity.class);
				wheelViewIntent.putExtra(AppKeys.WHEEL_ACTIVITY_MODE_KEY, WheelViewActivity.MODE_SET_VALUES);
				wheelViewIntent.putExtra(AppKeys.WHEEL_PARCEL_KEY, wheel);
				startActivity(wheelViewIntent);
				break;	
		}

	}
	
	
	private Wheel generateTemplateWheel() {
		
		int [] colors = ResourcesHandler.getAppColorList(getBaseContext());
		Wheel wheel = new Wheel(-1, "Wheel Title",
								new WheelItem("Item0", colors[0]),
								new WheelItem("Item1", colors[1]),
								new WheelItem("Item2", colors[2]),
								new WheelItem("Item3", colors[3]),
								new WheelItem("Item4", colors[4]),
								new WheelItem("Item5", colors[5]),
								new WheelItem("Item6", colors[6]),
								new WheelItem("Item7", colors[7]));						
		return wheel;
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
	    return performWheelAction(item.getItemId(), mSelectedViewForAction);	    
	}

	private boolean performWheelAction(int itemId, View view){
		Object tag = view.getTag();
		Wheel wheel = (Wheel)tag;         		    	
		switch (itemId) {
	        case R.id.remove_wheel:
	        	mClickedWheelTypeId = wheel.getTypeId();
				AlertDialogUtils.showAlertWithCancel(this, -1, R.string.confirmation_delete, (DialogInterface.OnClickListener)this);
				return true;
	        case R.id.edit_wheel:
				// start the WheelViewActivity activity in edit mode
				Intent wheelViewIntent = new Intent(this, com.berries.dashboard.activities.WheelViewActivity.class);
				wheelViewIntent.putExtra(AppKeys.WHEEL_ACTIVITY_MODE_KEY, WheelViewActivity.MODE_EDIT_WHEEL_TYPE);				
				wheelViewIntent.putExtra(AppKeys.WHEEL_PARCEL_KEY, wheel);
				startActivity(wheelViewIntent);
				return true;
	    }
		return false;
	}	
		
	
	public void onClick(DialogInterface dialog, int which) {
		
		if (which == DialogInterface.BUTTON_POSITIVE ){
			Context context = getBaseContext();
			int result = WheelTypesConnector.deleteWheelType(context, mClickedWheelTypeId);
			if ( result > 0)
				Toast.makeText(context, R.string.message_removed_wheel_success, Toast.LENGTH_LONG).show();
			else Toast.makeText(context, R.string.message_removed_wheel_fail, Toast.LENGTH_LONG).show(); 				
		}
		else {
			// do nothing
		}
		
	}
		

}
