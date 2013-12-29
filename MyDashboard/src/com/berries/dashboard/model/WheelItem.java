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

For the development of this class (Wheel.java, WheelItem.java, WheelBackgroundView.java, WheelDragView.java, WheelGroupView.java)
I have obtained help and copied parts of code from the PieChart example provided in CustomView.zip which can be 
downloaded from http://developer.android.com/training/custom-views/index.html.
Customview.zip is provided with the following license: 
Copyright (C) 2012 The Android Open Source Project
Licensed under the Apache License, Version 2.0 (the "License");
You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*/
package com.berries.dashboard.model;

/**
 * WheelItem represents one item in the wheel
 * @see Wheel
 */
public class WheelItem {
		
		private static final short DEFAULT_VALUE = 10;
		private static final short MIN_VALUE = 1;
		private static final short MAX_VALUE = 10;
		private String mName;
		private int mColor;
		private int mValue;

		/**
		 * Create a new WheelItem with the given name and color. Value will be set to default (10)
		 * @param name
		 * @param color
		 */
		public WheelItem(String name, int color){
			this(name, DEFAULT_VALUE, color);
		}

		/**
		 * Create a new WheelItem with the given name value and color
		 * @param name
		 * @param value
		 * @param color
		 */		
		public WheelItem(String name, int value, int color)
		{
		       setName(name);
		       setValue(value);
		       setColor(color);
		}
		
		
		
		/** @return String the name of the item */
		public String getName() {
			return mName;
		}
		/** @param name the name of the item */
		public void setName(String name) {
			this.mName = name;
		}
		
		
		
		/** @return int the color of the item */
		public int getColor() {
			return mColor;
		}
		/** @param color the color of the item */
		public void setColor(int color) {
			this.mColor = color;
		}
	
		
		
		/** @return int the value of the item */
		public int getValue() {
			return mValue;
		}
		/** @param value the value of the item, can be 1 to 10 */
		public void setValue(int value) {
			if ( value < MIN_VALUE || value > MAX_VALUE ){
				throw new IllegalArgumentException("The value should be between "+MIN_VALUE+" and "+MAX_VALUE+". The value given is "+value);
			}
			this.mValue = value;
		}		
				
		/** This method resets the item value to the default value */
		public void resetValue(){
			mValue = DEFAULT_VALUE;
		}
		
		
		/**
		 * Generate a copy of this item
		 * @return WheelItem a copy of this item
		 */
		public WheelItem copy(){
			return new WheelItem(mName, mValue, mColor);
		}
		
}
