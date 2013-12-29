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

import android.content.Context;
import android.content.res.Resources;
import com.berries.dashboard.R;

/**
 * Handle common application resources
 * At the moment this class just handles retrieving a pre-defined color list for the wheel items
  */
public class ResourcesHandler {

	
	private volatile static int [] mColorList; // singleton 
	
	
	/**
	 * Retrieve list of colors to be used for all wheels displayed through out the application
	 * @param context the context to get the resources from
	 * @return int [] array of colors
	 */
	public static int [] getAppColorList(Context context){
		
		if ( mColorList != null ){
			// color list is ready so simply return it
			return mColorList;
		}
		else {			
			// not ready yet, call synchronized method to retrieve it
			return retrieveColorList(context);		
		}
	}
	
	private static synchronized int [] retrieveColorList(Context context){
		
		// check again if mColorList is null in case a different thread called this method first and already retrieved the resources
		if ( mColorList == null )
		{
			// do the retrieving
			mColorList = new int [8];
			Resources res = context.getResources();

			mColorList[0] = res.getColor(R.color.DarkMagenta);			
			mColorList[1] = res.getColor(R.color.SkyBlue);
			mColorList[2] = res.getColor(R.color.MediumTurquoise);			
			mColorList[3] = res.getColor(R.color.DodgerBlue);
			mColorList[4] = res.getColor(R.color.Blue);
			mColorList[5] = res.getColor(R.color.Violet);			
			mColorList[6] = res.getColor(R.color.DarkViolet);
			mColorList[7] = res.getColor(R.color.DarkOrchid);			
		}		
		
		return mColorList;
		
	}
	
}

