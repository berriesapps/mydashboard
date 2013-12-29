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
package com.berries.dashboard.handlers;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.berries.dashboard.model.Wheel;
import com.berries.dashboard.utils.AppKeys;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

/**
 * Handles booking the next appointment
 */
public class NextAppointmentHandler implements DialogInterface.OnClickListener {

	public static final String DIALOG_TAG = "NextAppointmentHandlerDialogTag";
	private static final long TEN_SECONDS_IN_MILIS = 10 * 1000; 
	private static final long TWO_MINUTES_IN_MILIS =  2 * 60 * 1000;
	private static final long DAY_IN_MILLIS       = 24 * 60 * 60 * 1000;
	private static final long WEEK_IN_MILLIS      =  7 * DAY_IN_MILLIS;
	private static final long MONTH_IN_MILLIS     = 30 * DAY_IN_MILLIS;
	
	private Calendar 		 mNextAppointment;         // time & date of the next appointment
	private Wheel 			 mWheel;				   // the wheel to display in the next appointment
	private FragmentActivity mActivity;                // the activity that wants to display the dialog
	
	public NextAppointmentHandler(FragmentActivity activity, Wheel wheel) {
		mWheel = wheel;
		mActivity = activity;
	}
	

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
		case 0: // in test time (10 seconds)
			createNewAppointment(TEN_SECONDS_IN_MILIS);
			break;
		case 1:
			createNewAppointment(TWO_MINUTES_IN_MILIS);
			break;			
		case 2: // next week
			createNewAppointment(WEEK_IN_MILLIS);
			//createNewAppointment(TWO_MINUTES);
			break;
		case 3: // next month
			//createNewAppointment(FIVE_MINUTES);
			createNewAppointment(MONTH_IN_MILLIS);
			break;
		case 4: // not interested
			break;
		default:
			break;
		}
	}
	
	private void createNewAppointment(long addTimeInMillis){
		Calendar current = Calendar.getInstance();
		long nextAptInMiliSeconds = current.getTimeInMillis() + addTimeInMillis;		
		mNextAppointment = new GregorianCalendar();
		mNextAppointment.setTimeInMillis(nextAptInMiliSeconds);

		Intent intentAlarm = new Intent(mActivity, com.berries.dashboard.receivers.AppointmentAlarmReceiver.class);
		intentAlarm.putExtra(AppKeys.WHEEL_PARCEL_KEY, mWheel);


		//an alarm that has the same request code as an already existing alarm will replace the old alarm
		//So at the moment we cannot have more than one alarms set within the same minute	
		long minuteInMilis = 60*1000;		
		int requestCode = (int) (addTimeInMillis/minuteInMilis);
				
		AlarmManager alarmManager = (AlarmManager) mActivity.getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP, mNextAppointment.getTimeInMillis(), 
				PendingIntent.getBroadcast(mActivity, requestCode, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT)); 	    		
		Toast.makeText(mActivity.getBaseContext(),
				"The appointment has been set to " + getFormattedAppointment()+" (request code="+requestCode+"). See you then!",
				Toast.LENGTH_LONG).show(); 

	}
	
	private String getFormattedAppointment(){
		Date aptDate = mNextAppointment.getTime();
		DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(mActivity.getApplicationContext());
		DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.MEDIUM);
		return dateFormat.format(aptDate) +" "+timeFormat.format(aptDate);		
	}
	
}
