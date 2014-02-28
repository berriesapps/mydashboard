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

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.berries.dashboard.R;
import com.berries.dashboard.adapters.WheelTypesConnector;
import com.berries.dashboard.adapters.WheelValuesConnector;
import com.berries.dashboard.dialogs.AlertDialogUtils;
import com.berries.dashboard.model.Wheel;
import com.berries.dashboard.utils.AppKeys;
import com.berries.dashboard.utils.Prefs;
import com.berries.dashboard.views.WheelGroupView;

/**
 * <p>
 * The WheelViewActivity can display a "Wheel" in 3 different modes
 * <ul>
 * {@link #MODE_SET_VALUES} - User can drag each item in the wheel and set the
 * item's value from 1 to 10
 * </ul>
 * <ul>
 * {@link #MODE_EDIT_WHEEL_TYPE} - user can edit the wheel title and item names
 * and save in database
 * </ul>
 * <ul>
 * {@link #MODE_ADD_NEW_WHEEL_TYPE} - user can create a completely new wheel
 * type and save in database
 * </ul>
 * <br>
 * This activity needs to be invoked with an Intent that contains the wheel to
 * display (stored using AppKeys.WHEEL_PARCEL_KEY) and the activity mode stored
 * in (AppKeys.WHEEL_ACTIVITY_MODE_KEY).
 * 
 * @see {@link AppKeys}
 */
public class WheelViewActivity extends FragmentActivity implements
		DialogInterface.OnClickListener {

	/** User can drag each item on wheel and set it's value from 1 to 10 */
	public static final byte MODE_SET_VALUES = 0;
	/** user can edit the wheel title and item names and save in db */
	public static final byte MODE_EDIT_WHEEL_TYPE = 1;
	/** user can create a completely new wheel type and save in db */
	public static final byte MODE_ADD_NEW_WHEEL_TYPE = 2;

	private WheelGroupView mWheelGroupView; // Wheel UI
	private byte mActivityMode; // can be one of MODE_SET_VALUES,
								// MODE_EDIT_WHEEL_TYPE,MODE_ADD_NEW_WHEEL_TYPE
	private boolean mAppointmentDialogDisplayed = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		Bundle intentParams = this.getIntent().getExtras();

		if (intentParams == null
				|| !intentParams.containsKey(AppKeys.WHEEL_PARCEL_KEY)
				|| !intentParams.containsKey(AppKeys.WHEEL_ACTIVITY_MODE_KEY)) {

			throw new IllegalArgumentException(
					"Intent needs to contain WHEEL_PARCEL_KEY and WHEEL_ACTIVITY_MODE_KEY params in order for the activity to work correctly ");
		}

		Wheel wheel = null;

		if (savedInstanceState != null) {
			// retrieve the wheel object from savedInstanceState
			wheel = savedInstanceState.getParcelable(AppKeys.WHEEL_PARCEL_KEY);
			mAppointmentDialogDisplayed = savedInstanceState
					.getBoolean(AppKeys.APPOINTMENT_REACHED_DIALOG_DISPLAYED);
		}

		if (wheel == null) {
			// retrieve the wheel object from Intent parameters
			wheel = intentParams.getParcelable(AppKeys.WHEEL_PARCEL_KEY);
		}

		// retrieve activity mode
		mActivityMode = intentParams.getByte(AppKeys.WHEEL_ACTIVITY_MODE_KEY);
		Prefs.getPreviousSavedValues(this, wheel);

		// setup views
		setContentView(R.layout.activity_main);
		mWheelGroupView = (WheelGroupView) findViewById(R.id.wheel_group_view);
		mWheelGroupView.setWheel(wheel);
		mWheelGroupView.setShowTextViews(true);

		if (mActivityMode == MODE_SET_VALUES) {
			setTitle(R.string.activity_title_wheel_set_values);
			mWheelGroupView.setDraggable(true);
			mWheelGroupView.setEditable(false);
		} else { // MODE_EDIT_WHEEL_TYPE or MODE_ADD_NEW_WHEEL_TYPE
			setTitle(R.string.activity_title_wheel_edit_type);
			mWheelGroupView.setDraggable(false);
			mWheelGroupView.setEditable(true);
		}

		if (intentParams.containsKey(AppKeys.APPOINTMENT_ALARM_DATE_KEY)
				&& !mAppointmentDialogDisplayed) {
			mAppointmentDialogDisplayed = true;
			AlertDialogUtils.showAlert(this, R.string.welcome_title,
					R.string.appointment_date_reached);
		}

	}

	protected void onSaveInstanceState(Bundle bundle) {
		// save the state of the wheel object
		Wheel wheel = mWheelGroupView.getWheel();
		bundle.putParcelable(AppKeys.WHEEL_PARCEL_KEY, wheel);
		bundle.putBoolean(AppKeys.APPOINTMENT_REACHED_DIALOG_DISPLAYED,
				mAppointmentDialogDisplayed);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.wheel_menu, menu);
		if (mActivityMode != MODE_SET_VALUES) {
			menu.removeItem(R.id.action_show_progress);
		}
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle menu item selection
		switch (item.getItemId()) {
		case R.id.action_help:
			actionHelpCalled();
			return true;
		case R.id.action_save:
			actionSaveCalled();
			return true;
		case R.id.action_show_progress:
			actionShowProgressCalled();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void actionHelpCalled() {
		switch (mActivityMode) {
		case MODE_SET_VALUES:
			AlertDialogUtils.showAlert(this, R.string.help_title,
					R.string.help_set_values);
			break;
		case MODE_EDIT_WHEEL_TYPE:
			AlertDialogUtils.showAlert(this, R.string.help_title,
					R.string.help_edit_wheel);
			break;
		case MODE_ADD_NEW_WHEEL_TYPE:
			AlertDialogUtils.showAlert(this, R.string.help_title,
					R.string.help_add_new_wheel);
			break;
		default:
			break;
		}

	}

	private void actionSaveCalled() {
		Wheel wheel = mWheelGroupView.getWheel();

		if (mActivityMode == MODE_SET_VALUES) {
			WheelValuesConnector.saveWheelValues(getBaseContext(), wheel);
			Prefs.setValuesSavedInPrefs(this, wheel);
			AlertDialogUtils.showAlert(this, wheel.getTitle(),
					R.string.message_values_saved, this);

		} else {
			// save the new/edited wheel and then go back to the
			// WheelTypesManagerActivity
			boolean insertNew = (mActivityMode == MODE_ADD_NEW_WHEEL_TYPE);
			WheelTypesConnector.saveWheelType(getBaseContext(), wheel,
					insertNew);
			Intent wheelTypesIntent = new Intent(this,
					com.berries.dashboard.activities.DashboardActivity.class);
			startActivity(wheelTypesIntent);
		}
	}

	private void actionShowProgressCalled() {
		// open the WheelProgressActivity to display the progress made
		// throughout appointments
		Intent progressIntent = new Intent(this,
				com.berries.dashboard.activities.WheelProgressActivity.class);
		progressIntent.putExtra(AppKeys.WHEEL_PARCEL_KEY,
				mWheelGroupView.getWheel());
		startActivity(progressIntent);
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (which == DialogInterface.BUTTON_POSITIVE) {
			actionShowProgressCalled();
		}
		finish();
	}

}
