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
package com.berries.dashboard.receivers;

import java.util.Calendar;

import com.berries.dashboard.activities.WheelViewActivity;
import com.berries.dashboard.utils.AppKeys;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Receives the broadcast when the booked appointment date is reached and invokes the WheelViewActivity activity 
 */
public class AppointmentAlarmReceiver extends BroadcastReceiver {
	

	@Override
	public void onReceive(Context context, Intent intent) {		

		Intent wheelViewIntent = new Intent(context, com.berries.dashboard.activities.WheelViewActivity.class);
		wheelViewIntent.putExtra(AppKeys.APPOINTMENT_ALARM_DATE_KEY, Calendar.getInstance().getTimeInMillis());
		wheelViewIntent.putExtra(AppKeys.WHEEL_PARCEL_KEY, 		   intent.getExtras().getParcelable(AppKeys.WHEEL_PARCEL_KEY));
		wheelViewIntent.putExtra(AppKeys.WHEEL_ACTIVITY_MODE_KEY,    WheelViewActivity.MODE_SET_VALUES);
		wheelViewIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
		context.startActivity(wheelViewIntent);		
	}

}

