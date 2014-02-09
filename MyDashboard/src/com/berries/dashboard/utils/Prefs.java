package com.berries.dashboard.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.berries.dashboard.model.Wheel;

public class Prefs {
	public static final String NAME = "com.berries.dashboard.preferences";
	
	public  static int getPreviousSavedValues(Context context, Wheel wheel){
		SharedPreferences prefs = context.getSharedPreferences(NAME, Activity.MODE_PRIVATE);
		return prefs.getInt(String.valueOf(wheel.getTypeId()), 0);
	}
	
	public static void setValuesSavedInPrefs(Context context, Wheel wheel){
		int numOfSavedValues = getPreviousSavedValues(context, wheel);
		numOfSavedValues ++;
		String wheelKey = String.valueOf(wheel.getTypeId());
		SharedPreferences prefs = context.getSharedPreferences(NAME,  Activity.MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.putInt(wheelKey, numOfSavedValues);
		editor.commit();
	}
}
