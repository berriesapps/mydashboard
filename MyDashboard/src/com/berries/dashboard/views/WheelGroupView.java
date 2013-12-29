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
package com.berries.dashboard.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.berries.dashboard.R;
import com.berries.dashboard.model.Wheel;
import com.berries.dashboard.model.WheelItem;
import com.berries.dashboard.event.OnWheelItemClickedListener;

/**
 * WheelGroupView is a ViewGroup that will lay out the WheelBackgroundView, WheelDragView and needed TextViews  
 */
public class WheelGroupView extends ViewGroup implements View.OnClickListener, 
														  TextView.OnEditorActionListener {

	private enum ViewTag {
		TITLE, ITEM, WHEEL
	}
	
	private enum TextStatus {
		VALID, EXCEED_MAX, EMPTY
	}
	
	private WheelBackgroundView mWheelBgView;
	private WheelDragView 	 	mWheelDragView;
	
	private TextView 			mWheelTitleView;
	private EditText 			mWheelTitleEdit;
	private TextView 			mWheelItemView;
	private EditText 			mWheelItemEdit;
	
	private boolean  			mShowTextViews;	
	private boolean  			mIsEditable;	
	private int      			mClickedItemIndex;
	
	// TODO move the below to resources 
	private static final int INTERNAL_PADDING = 40;  
	private static final int TEXT_SIZE = 20;
	private static final int EDIT_TEXT_SIZE = 16;
	private static final int TEXT_COLOR = Color.rgb(64, 0, 128);
	private static final int MAX_NUM_OF_CHARACTERS = 20;


	public WheelGroupView(Context context) {
		super(context);
		initViews();
	}
	
	public WheelGroupView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initViews();		
	}

   
	/**
	 * @param draggable true if we want to be able to drag and set the values for each item
	 */
	public void setDraggable(boolean draggable){
		mWheelDragView.setDraggable(draggable);
	}

	/**
	 * @param editable true if we want to be able to edit the title and item text values
	 */
	public void setEditable(boolean editable){
		mIsEditable = editable;		
		if ( mShowTextViews ){
			mWheelTitleView.setClickable(editable);
		}

	}

	/**
	 * @param show true if we want to enable displaying the title and item text views
	 */
	public void setShowTextViews(boolean show){		
		if ( show && !mShowTextViews ){
			mShowTextViews = show;
			Context context = getContext();
			initTextViews(context);
			initTextViewContent(context);
			invalidate();
		}	
		mShowTextViews = show;
	}

	
	
   /**
    * Set the wheel model. The model is copied and will be changed if the user drags and sets different values. 
    * Use getWheel() to get the updated wheel.
    * @param wheel the wheel model we want to view
    */
   public void setWheel(Wheel wheel){
	     Wheel wheelCopy = wheel.copy();
	     mWheelBgView.setWheel(wheelCopy);
	     mWheelDragView.setWheel(wheelCopy);	     
	     if ( mShowTextViews ){
	    	 initTextViewContent(getContext());
	     }	     
	     invalidate();
   }
   
   /**
    * @return Wheel the updated wheel model
    */
   public Wheel getWheel(){
	   return mWheelBgView.getWheel();
   }
   
   
   /**
    * Perform an animation of the values of each item. The animation will start with the current values and end with the values given in targetWheel.
    * In the case we are running in a lower than HONEYCOMB (Android 3.0) version, the method will simply set the wheel to the new targetWheel 
    * @param targetWheel the target wheel to reach at the end of the animation
    */
   public void animateTo(Wheel targetWheel){	   
   		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
   		   mWheelDragView.animateTo(targetWheel);
   		}
   		else {
   		   mWheelDragView.setWheel(targetWheel.copy());
   		   invalidate();
   		}	   
   }
      
    @Override
    protected int getSuggestedMinimumWidth() {
        return 120;  // suggest a minimum
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        return 120; // suggest a minimum
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        
    	// Try for a width based on minimum
        int minw = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();

        // Make width as big as possible
        int width = Math.max(minw, MeasureSpec.getSize(widthMeasureSpec));

        // try for height based on minimum
        int minh = getSuggestedMinimumHeight() + getPaddingBottom() + getPaddingTop();

        // make height as big as possible
        int height = Math.max(MeasureSpec.getSize(heightMeasureSpec), minh);

        setMeasuredDimension(width, height);
    }

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// do nothing, layout is taken care of on onSizeChanged
	} 
	
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        
    	super.onSizeChanged(w, h, oldw, oldh);
  	
        // Account for padding
        int xpad = getPaddingLeft() + getPaddingRight();
        int ypad = getPaddingTop() + getPaddingBottom();
        int width  = w - xpad;
        int height = h - ypad;

        // Layout the wheel background view, so that it occupies all of the available frame
        Rect bgBounds = new Rect( 0, 0, width, height);        
        bgBounds.offsetTo(getPaddingLeft(), getPaddingTop());       
        mWheelBgView.layout(bgBounds.left, bgBounds.top, bgBounds.right, bgBounds.bottom);

        // radius of the circle that will contain the wheel "dragging view"
        int radius = Math.min(width, height) / 2;         

        if ( !mShowTextViews ){
            // layout the wheel dragging view 
            Rect dragViewBounds = new Rect(bgBounds.centerX()-radius, bgBounds.centerY()-radius,bgBounds.centerX()+radius, bgBounds.centerY()+radius);
            mWheelDragView.layout(dragViewBounds.left, dragViewBounds.top, dragViewBounds.right, dragViewBounds.bottom); 
        }
        else {
        	layoutWithTextViews(width, height, bgBounds, radius);
        }
        
    }
        
    private void layoutWithTextViews(int width, int height, Rect bgBounds, int radius){
        int centerX = bgBounds.centerX();
    	Size titleSize 	   = getTextSize(mWheelTitleView);
    	Size itemTextSize  = getTextSize(mWheelItemView);
    
    	if ( width > height ) { 
    		// LANDSCAPE       		
    		// we will position the CENTER of the bgview and dragviews so that we have space for the text on 
    		// the left but the combination of circle + text remains in the middle of the screen
    		int landscapePadding = (int)(width/2 - titleSize.width/2 - radius);
    		centerX = bgBounds.right - radius - landscapePadding;
    		mWheelBgView.setCircleCenter(centerX, bgBounds.centerY()); 
    	}	
    	
        Rect dragViewBounds = new Rect(centerX-radius, bgBounds.centerY()-radius,centerX+radius, bgBounds.centerY()+radius);
        mWheelDragView.layout(dragViewBounds.left, dragViewBounds.top, dragViewBounds.right, dragViewBounds.bottom);

    	// Calculate positions for the texts to be displayed	        
        Rect titleRect = new Rect();
        Rect itemRect  = new Rect();
        int  editLeft, editRight;    
    	
        if ( height>= width ) {     		
   			// PORTRAIT 
        	titleRect.left     = (width - titleSize.width)/2;
        	titleRect.right    = titleRect.left + titleSize.width;
    		titleRect.top      = bgBounds.top+ INTERNAL_PADDING;
    		titleRect.bottom   = titleRect.top + titleSize.height;    		
    		editLeft 		   = INTERNAL_PADDING;
    		editRight          = width - INTERNAL_PADDING;    		    		
    		if ( itemTextSize.width > 0 )
    		{
    			itemRect.left     = (width - itemTextSize.width)/2;;
    			itemRect.right    = itemRect.left + itemTextSize.width;
    			itemRect.top      = bgBounds.bottom - itemTextSize.height - INTERNAL_PADDING;
    			itemRect.bottom   = itemRect.top + itemTextSize.height;
        	}
        }
    	else {     		
    		// LANDSCAPE
    		titleRect.right      = dragViewBounds.left - INTERNAL_PADDING;
    		titleRect.left       = titleRect.right - titleSize.width;
    		titleRect.bottom     = bgBounds.top + height/2 ;
    		titleRect.top        = titleRect.bottom - titleSize.height;
    		editLeft   			 = INTERNAL_PADDING;
    		editRight  			 = titleRect.right;
    		
    		if ( itemTextSize.width > 0 )
    		{
    			itemRect.right      = dragViewBounds.left - INTERNAL_PADDING;
    			itemRect.left       = itemRect.right - itemTextSize.width;
    			itemRect.top        = titleRect.bottom + 5;
    			itemRect.bottom     = itemRect.top + itemTextSize.height ;
    		}    	
    	}
    	mWheelTitleView.layout(titleRect.left, titleRect.top , titleRect.right, titleRect.bottom );
    	mWheelTitleEdit.layout(editLeft, titleRect.top -10, editRight, titleRect.bottom+10 );
    	if ( itemTextSize.width > 0 ){
    		mWheelItemView.layout(itemRect.left, itemRect.top , itemRect.right, itemRect.bottom );
    		mWheelItemEdit.layout(editLeft+INTERNAL_PADDING, itemRect.top-10, editRight-INTERNAL_PADDING, itemRect.bottom+10);	    		
    	}
    	
    }

    
    // returns the text width and height in a "Size" Structure
    private Size getTextSize(TextView textView){
        // Find out what is the size of the title text
    	Size s = new Size();
   		Paint paint    = textView.getPaint();
   		String text    = (String)textView.getText();
   		s.width  = (int)paint.measureText(text);
   		s.height = (int)(Math.abs(paint.getFontMetrics().bottom) + Math.abs(paint.getFontMetrics().top)+1);
    	return s;
    }
    
	// Simple structure to hold width and height together 
	private class Size {		
		private int width=0;
		private int height=0;		
	}
  
		    

	// Methods dealing with editing
	//===========================================================
	
	@Override
	public void onClick(View v) {

 		if ( v.getTag().equals(ViewTag.TITLE) ){
 			// title view was clicked, start editing of title
 			stopEditing(mWheelItemEdit);                       
			startEditing(mWheelTitleView, mWheelTitleEdit, mWheelBgView.getWheel().getTitle());
			onSizeChanged(getWidth(), getHeight(), 0, 0);
			invalidate();
		}		

	}
	
	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
        	// keyboard done button was clicked   
        	TextStatus status = stopEditing(v);
    		onSizeChanged(getWidth(), getHeight(), 0, 0);
        	invalidate();
        	displayErrors(status);
        }
        return false;
    }

	private TextStatus stopEditing(View v){
		return stopEditing(v, mClickedItemIndex);
	}

	
	private TextStatus stopEditing(View v, int index){
		String newValue;        			
		TextStatus status = TextStatus.VALID;
		if ( v.getTag().equals(ViewTag.TITLE) ){ 
			status = checkValidation(mWheelTitleEdit.getText().toString());
			// title finished editing, switch to regular view and set new title string
    		newValue = finishedEditing(mWheelTitleView, mWheelTitleEdit, status);
    		if ( status == TextStatus.VALID )
    			mWheelBgView.getWheel().setTitle(newValue);
    	}
		else if ( v.getTag().equals(ViewTag.ITEM) && index >= 0 ){
			// item finished editing, switch to regular view and set new item string
			status = checkValidation(mWheelItemEdit.getText().toString());
    		newValue = finishedEditing(mWheelItemView, mWheelItemEdit, status);
    		if ( status == TextStatus.VALID ){
    			WheelItem item = mWheelBgView.getWheel().getItemAt(index);
    			item.setName(newValue);
    		}
		}
		return status;
	}
	
	
	private String finishedEditing(TextView view, EditText edit, TextStatus status){

		String oldValue = view.getText().toString();
		String newValue = edit.getText().toString();

		if ( status == TextStatus.VALID ){
			view.setText(newValue);
		}
		else newValue = oldValue;

		edit.setVisibility(GONE);
		view.setVisibility(VISIBLE);
		return newValue;
	}
	

	private void startEditing(TextView view, EditText edit, String startValue){
		view.setVisibility(GONE);
		view.setText(startValue);
		edit.setText(startValue);
		edit.setVisibility(VISIBLE);		
	}
	
	private TextStatus checkValidation(String newVal){
		TextStatus status = TextStatus.VALID;
		
		if ( (newVal==null) || newVal.trim().equals("") ){
			status = TextStatus.EMPTY;
		}
		
		if ( newVal.length() > MAX_NUM_OF_CHARACTERS ){
			status = TextStatus.EXCEED_MAX;
		}
		
		return status;
	}
	
	private void displayErrors(TextStatus status){
		switch (status){
			case VALID: // do nothing
			break;			 					
			case EMPTY:
				Toast.makeText(getContext(), R.string.message_text_empty, Toast.LENGTH_LONG).show();
				break;
			case EXCEED_MAX:
				Resources res = getContext().getResources();
				String message = res.getString(R.string.message_text_exceed_characters) + " " + MAX_NUM_OF_CHARACTERS + " "+res.getString(R.string.message_text_characters);
				Toast.makeText(getContext(), message , Toast.LENGTH_LONG).show();
				break;
		}		
	}
	
	
// Initialization methods
//==========================================================================================	
	
    // Initialize the control
	private void initViews(){
		Context context = getContext();
		mClickedItemIndex = -1;
		mIsEditable = false;
		mWheelBgView = new WheelBackgroundView(context);
	    addView(mWheelBgView);
	    mWheelDragView = new WheelDragView(context);
	    addView(mWheelDragView);
	    if ( mShowTextViews )
	    {
	    	initTextViews(context);
	    }
	}
	
	// initialize the text views	
	private void initTextViews(Context context){
		mWheelTitleView = new TextView(context);
		mWheelTitleView.setTextSize(TEXT_SIZE);
		mWheelTitleView.setTextColor(TEXT_COLOR);
		mWheelTitleView.setTypeface(mWheelTitleView.getTypeface(), Typeface.BOLD);
		mWheelTitleView.setOnClickListener(this);
		mWheelTitleView.setTag(ViewTag.TITLE);

		
		mWheelTitleEdit = new EditText(context);
		mWheelTitleEdit.setTextSize(EDIT_TEXT_SIZE);
		mWheelTitleEdit.setTextColor(TEXT_COLOR);
		mWheelTitleEdit.setImeActionLabel(context.getResources().getString(R.string.action_done), EditorInfo.IME_ACTION_DONE);
		mWheelTitleEdit.setInputType(EditorInfo.TYPE_CLASS_TEXT);
		mWheelTitleEdit.setImeOptions(EditorInfo.IME_ACTION_DONE);		
		mWheelTitleEdit.setOnEditorActionListener(this);
		mWheelTitleEdit.setVisibility(GONE);
		mWheelTitleEdit.setTag(ViewTag.TITLE);

    	mWheelItemView= new TextView(context);
    	mWheelItemView.setTextSize(TEXT_SIZE);
    	mWheelItemView.setTextColor(TEXT_COLOR);
    	mWheelItemView.setOnClickListener(this);
		mWheelItemView.setTag(ViewTag.ITEM);

    	mWheelItemEdit = new EditText(context);
    	mWheelItemEdit.setTextSize(EDIT_TEXT_SIZE);
    	mWheelItemEdit.setTextColor(TEXT_COLOR);
    	mWheelItemEdit.setImeActionLabel(context.getResources().getString(R.string.action_done), EditorInfo.IME_ACTION_DONE);
    	mWheelItemEdit.setInputType(EditorInfo.TYPE_CLASS_TEXT);
    	mWheelItemEdit.setImeOptions(EditorInfo.IME_ACTION_DONE);		
    	mWheelItemEdit.setOnEditorActionListener(this);
    	mWheelItemEdit.setVisibility(GONE);
    	mWheelItemEdit.setTag(ViewTag.ITEM);
    	
    	addView(mWheelTitleView);
		addView(mWheelTitleEdit);
		addView(mWheelItemView);
    	addView(mWheelItemEdit);    	    	
	}

	// initialize the content of the text views
	private void  initTextViewContent(Context context){
		mWheelItemView.setText("");
		mWheelItemEdit.setText("");
		String wheelTitle = mWheelDragView.getWheel().getTitle();
		mWheelTitleView.setText(wheelTitle);
		mWheelTitleView.setHint(R.string.edit_wheel_title);
		mWheelTitleEdit.setText(wheelTitle);
		mWheelDragView.setOnWheelItemClickedListener(new OnWheelItemClickedListener(){
			
			public void onWheelItemClicked(int itemIndex){

				if ( mIsEditable && mClickedItemIndex>=0 ){ 
					// if a previous item was being edited
					// stop editing it and display new item name
					stopEditing(mWheelItemEdit);                       
				}
	
				// now deal with the new clicked item
				mClickedItemIndex = itemIndex;
				WheelItem item = mWheelBgView.getWheel().getItemAt(mClickedItemIndex);	
				mWheelItemView.setText(item.getName());
				
				if ( mIsEditable ){
					stopEditing(mWheelTitleEdit);                   	
					startEditing(mWheelItemView, mWheelItemEdit, item.getName());
				}
				
				onSizeChanged(getWidth(), getHeight(), 0, 0);		
				invalidate();		
			}
		});
	}

    
}
