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
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * AppointmentDialogFrament.java
 * Responsible for creating a dialog with a given title and list of options 
 * and a given listener 
 */
public class AppointmentDialogFragment extends DialogFragment  {

	private DialogInterface.OnClickListener mListener;
	private int mTitleId;
	private int mItemsId;
	
	/**
	 * Create a Dialog with the given resources and listener
	 * @param titleResourceId The title resource id
	 * @param itemsResourceId id of resource containing the list of items to appear
	 * @return
	 */
	public static AppointmentDialogFragment createDialog(int titleResourceId, int itemsResourceId){
		AppointmentDialogFragment aptDialog = new AppointmentDialogFragment();
		aptDialog.setup(titleResourceId, itemsResourceId);
		return aptDialog;
	}	

	/**
	 * Setup the dialog 
	 * @param titleResourceId resource id of the title to appear in the dialog
	 * @param itemsResourceId resource id of the items to appear in the dialog
	 * @param listener the listener responsible for handling the dialog clicks
	 */
	public void setup(int titleResourceId, int itemsResourceId){
		mTitleId = titleResourceId;
		mItemsId = itemsResourceId;		
	}
	
	
	/**
	 * Set the dialog OnClickListener
	 * @param listener the listener responsible for handling the dialog clicks
	 */
	public void setListener(DialogInterface.OnClickListener listener){
		mListener = listener;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(mTitleId);
		builder.setItems(mItemsId, mListener);
		return builder.create();
	}

}
