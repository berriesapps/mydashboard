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
package com.berries.dashboard.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.berries.dashboard.R;

/**
 * Contains methods to generate and display different types of AlertDialogs
 */
public class AlertDialogUtils {

	 /**
	  * Creates an AlertDialog with the given content resource id (no title) and shows it on screen.
	  * The dialog will contain a single "ok" button
	  * @param context
	  * @param contentStringId
	  */
	public static void showAlert(Context context, int contentStringId){
		showAlert(context, -1, contentStringId);
	}

	 /**
	  * Creates an AlertDialog with the given title & content resource ids and shows it on screen.
	  * The dialog will contain a single "ok" button
	  * Set titleStringId to -1 if you don't want a title
	  * @param context
	  * @param titleStringId 
	  * @param contentStringId
	  */
	public static void showAlert(Context context, int titleStringId, int contentStringId){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setPositiveButton(R.string.action_ok, null);
		builder.setMessage(contentStringId);
		if ( titleStringId != -1 )
			builder.setTitle(titleStringId);
		AlertDialog dialog = builder.create();		
		dialog.show();	            		
	}


	
	 /**
	  * Creates an AlertDialog with the given title & content resource ids and shows it on screen.
	  * The dialog will contain "ok" and "cancel" buttons
	  * Set titleStringId to -1 if you don't want a title
	  * @param context
	  * @param titleStringId 
	  * @param contentStringId
	  */
	public static void showAlertWithCancel(Context context, int titleStringId, int contentStringId, DialogInterface.OnClickListener clickListener){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setCancelable(true);
		builder.setPositiveButton(R.string.action_ok, clickListener);
		builder.setNegativeButton(R.string.action_cancel, clickListener );
		builder.setMessage(contentStringId);
		if ( titleStringId != -1 )
			builder.setTitle(titleStringId);
		AlertDialog dialog = builder.create();		
		dialog.show();	            		
	}
	
	
	
}
