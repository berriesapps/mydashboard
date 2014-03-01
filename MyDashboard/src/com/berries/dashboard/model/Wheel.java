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

import java.util.ArrayList;
import java.util.List;

import com.berries.dashboard.db.tables.Contract;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * The Wheel model 
 */
public class Wheel implements Parcelable {

	private int mTypeId;
	private String mTitle;
	private List<WheelItem> mItems;
	private List<PieceAngles> mAngles; 
	private long mTimestamp;
	
	/**
	 * Create a new Wheel with the given type id, title and items
	 * @param typeId
	 * @param title
	 * @param items a maximum of Contract.MAX_ITEMS_IN_WHEEL can be passed 
	 */
	public Wheel(int typeId, String title, WheelItem... items){
		setTypeId(typeId);
		setTitle(title);
		setItems(items);
		calculateAngles();
	}
	
	/**
	 * Create a new Wheel with the given type id, title and items
	 * @param typeId
	 * @param title
	 * @param items should not be null and size of items list should be maximum Contract.MAX_ITEMS_IN_WHEEL
	 */
	public Wheel(int typeId, String title, List<WheelItem> items){
		setTypeId(typeId);
		setTitle(title);
		setItems(items);
		calculateAngles();		
	}

	public float calcAverageValue(){
		int total = 0;
		
		if ( mItems==null || mItems.size()== 0){
			return 0;
		}
		
		for (int i=0;i<mItems.size(); i++){
			total = total + mItems.get(i).getValue();
		}
		
		return total / mItems.size();
	}
	
	/** @param id The wheel type id	 */
	public void setTypeId(int id){
		mTypeId = id;
	}
	/** @return int the wheel type id */
	public int getTypeId(){
		return mTypeId;
	}
	
	/** @param id the timestamp of the date/time that the specific values where saved */ 
	public void setTimeSaved(long t){
		mTimestamp = t;
	}
	/** @return long the timestamp of the date/time that the specific values where saved */
	public long getTimeSaved(){
		return mTimestamp;
	}
	
	/** @param title The wheel title	 */	
	public void setTitle(String title){
		mTitle = title;
	}	 
	/** @return String the wheel title */
	public String getTitle(){
		return mTitle;
	}

		
	
	/** @return int the number of Items */
	public int getNumOfItems(){
		return mItems==null?0:mItems.size();
	}
	
	/**  
	 * @param index 
	 * @return WheelItem the item at the given index
	 */
	public WheelItem getItemAt(int index){
		return mItems==null?null:mItems.get(index);
	}
	
	/** reset all wheel item values to the default value */
	public void resetValues(){
		for (WheelItem item:mItems){
			item.resetValue();
		}
	}
	
	/**
	 * Generate a copy of this wheel
	 * @return Wheel the copy
	 */
	public Wheel copy(){

		ArrayList<WheelItem> copyOfItems = new ArrayList<WheelItem>(mItems.size());
		for (WheelItem item:mItems){
			copyOfItems.add(item.copy());
		}		
		return new Wheel(mTypeId, getTitle(), copyOfItems);		

	}

	public int getStartAngleAt(int position){
		return mAngles.get(position).start;
	}
	
	public int getEndAngleAt(int position){
		return mAngles.get(position).end;
	}

	
	// Parcelable implementation
	//====================================================================================
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	public static final Parcelable.Creator<Wheel> CREATOR = new Parcelable.Creator<Wheel>() {
		public Wheel createFromParcel(Parcel in) {
				return new Wheel(in);
		}

		public Wheel[] newArray(int size) {
				return new Wheel[size];
		}
	};

	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(mTypeId);
		dest.writeString(mTitle);
		int count = 0;		
		if ( mItems == null ){
			dest.writeInt(count);
			return;
		}
		count = mItems.size();
		dest.writeInt(count);
		for ( int i=0; i<count; i++){
			WheelItem item = mItems.get(i);			
			dest.writeString(item.getName());
			dest.writeInt(item.getValue());
			dest.writeInt(item.getColor());
		}		
	}
	
	private Wheel(Parcel in){
		setTypeId(in.readInt());
		setTitle(in.readString());
		int count = in.readInt();
		mItems = new ArrayList<WheelItem>(count);
		for ( int i=0; i<count; i++){
			String name = in.readString();
			int value = in.readInt();			
			int color = in.readInt();
			mItems.add(new WheelItem(name,value,color));
		}		
	}
	

	// Private methods dealing with list of items
	//===================================================

	private void setItems(WheelItem... items){
		if ( items == null ){
			throw new IllegalArgumentException("Cannot set wheel items to null");
		}
		if ( items.length > Contract.MAX_ITEMS_IN_WHEEL ){
			throw new IllegalArgumentException("Wheel cannot have more than "+Contract.MAX_ITEMS_IN_WHEEL+" items");
		}
		if ( items.length < Contract.MIN_ITEMS_IN_WHEEL ){
			throw new IllegalArgumentException("Wheel cannot have less than "+Contract.MIN_ITEMS_IN_WHEEL+" items");
		}
		mItems = java.util.Arrays.asList(items);		
	}	
	

	// sets the list of items to the list given
	private void setItems(List<WheelItem> items){
		if ( items == null ){
			throw new IllegalArgumentException("Cannot set wheel items to null");
		}

		if ( items.size() > Contract.MAX_ITEMS_IN_WHEEL ){
			throw new IllegalArgumentException("Wheel cannot have more than "+Contract.MAX_ITEMS_IN_WHEEL+" items");
		}
		if ( items.size() < Contract.MIN_ITEMS_IN_WHEEL ){
			throw new IllegalArgumentException("Wheel cannot have less than "+Contract.MIN_ITEMS_IN_WHEEL+" items");
		}

		
		mItems = items;
	}
	
	
	
	// Calculate start/end angles for each item
    private void calculateAngles() {
        	
    	mAngles = new ArrayList<PieceAngles>(mItems.size());
    	int currentAngle = 0;
    	for (int i =0; i<mItems.size(); i++) {
    		PieceAngles pa = new PieceAngles();
    		pa.start = currentAngle;
    		pa.end = (int) ((float) currentAngle + 1 * 360.0f / mItems.size());
    		currentAngle = pa.end;
    		mAngles.add(pa);
    	}
        
    	if ( mItems.size() > 0 ){
    		PieceAngles lastAngle = mAngles.get(mItems.size()-1);
    		lastAngle.end = 360;
    	}
        
    }
	
	/**
	 * A simple structure to hold the starting & ending angles of each visual piece of the wheel
	 */
	private class PieceAngles {
    	private int start;
    	private int end;
	}
		
}
