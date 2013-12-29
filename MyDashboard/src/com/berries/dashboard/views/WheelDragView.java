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

import com.berries.dashboard.model.Wheel;
import com.berries.dashboard.model.WheelItem;
import com.berries.dashboard.event.*;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.Color;


/**
 * Displays a round wheel, separated to a number of items 
 * Each item is drawn with the given color and value
 * By default user can drag and set the values for each item
 * Use setDraggable(false) if you want to disable dragging
 * If you set an OnWheelItemClickedListener and/or an OnWheelItemValueChangedListener
 * the view will notify the listeners each time an item is clicked and/or each time an item value is changed
 */
public class WheelDragView extends View  {

	private static final int TOTAL_CIRCLES = 22;
    private static final long ANIM_DURATION_MSEC = 1000;
    private static final int TEXT_SIZE = 19; // TODO move this to resources
    
	private Path  mTextPath;
	private RectF mBounds;
	private RectF mPiePieceBounds;
	private Paint mPaint;
	private Wheel mWheel;
	private int mSelectedItemIndex= -1; 
    private GestureDetector mDetector;
    private boolean mIsDragging = false;
    private boolean mDraggable = true;
    private float mDiskWidth;
    private OnWheelItemClickedListener mItemClickedListener = null;
    private OnWheelItemValueChangedListener mValueChangedListener = null;

    
	public WheelDragView(Context context) {
		super(context);
		mTextPath = new Path();
		mBounds = new RectF();
		mPiePieceBounds = new RectF();
		mPaint  = new Paint();
		mPaint.setColor(Color.DKGRAY);
		mPaint.setStyle(Style.STROKE);            
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setStrokeWidth(1);
		mPaint.setTextSize(TEXT_SIZE);
		
        // Create a gesture detector to handle onTouch messages
        mDetector = new GestureDetector(this.getContext(), new DragGestureListener());
        // Turn off long press-- if long press is enabled, you can't scroll for a bit, pause, then scroll some more 
        // because the pause is interpreted as a long press
        mDetector.setIsLongpressEnabled(false); 
	}
	
	/** @param wheel the wheel to display  */
	public void setWheel(Wheel wheel){
		mWheel = wheel;		
	}	
	/** @param Wheel the updated wheel */
	public Wheel getWheel(){
		return mWheel;
	}
	
	/** @param draggable true if we want the user to be able to drag the items and set their value */
	public void setDraggable(boolean draggable){
		mDraggable = draggable;
	}

	/** @param l a listener to be notified when an item on the wheel has been clicked */
	public void setOnWheelItemClickedListener(OnWheelItemClickedListener l){
		mItemClickedListener = l;
	}	
	/** @param l a listener to be notified when the value of an item has been changed */
	public void setOnWheelItemValueChangedListener(OnWheelItemValueChangedListener l){
		mValueChangedListener = l;
	}
	

    /**
     * Make the current wheel animate from it's current values to the values given in targetWheel
     * @param targetWheel
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void animateTo(Wheel targetWheel){
    	AnimatorSet animatorSet = new AnimatorSet();
    	
    	ObjectAnimator lastAnimator = createWheelItemAnimator(targetWheel, 0);    	
    	for ( int i=1; i<mWheel.getNumOfItems(); i++)
    	{
    		ObjectAnimator animator = createWheelItemAnimator(targetWheel, i);
    		animatorSet.play(lastAnimator).with(animator);
    		lastAnimator = animator;
   		
    	}

    	WheelAnimatorListener animListener = new WheelAnimatorListener();
    	lastAnimator.addUpdateListener(animListener);
    	lastAnimator.addListener(animListener);
    	accelerate();
    	animatorSet.start();    	
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private ObjectAnimator createWheelItemAnimator(Wheel targetWheel, int index){
		WheelItem startWI = mWheel.getItemAt(index);
		WheelItem endWI = targetWheel.getItemAt(index);    		
		ObjectAnimator animator = ObjectAnimator.ofInt(startWI, "value", startWI.getValue(), endWI.getValue() );
		animator.setDuration(ANIM_DURATION_MSEC);
		return animator;    	
    }
    
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);        
        // Draw the background borders        
        // draw the 9 first circle outlines with regular stroke 
        mPaint.setStyle(Style.STROKE);
        mPaint.setColor(Color.DKGRAY);
        mPaint.setStrokeWidth(1);        
        float centerX = mBounds.centerX();
        float centerY = mBounds.centerY();
        mDiskWidth = Math.min(mBounds.width(),mBounds.height())/TOTAL_CIRCLES;

        for (int itemIndex = 0; itemIndex <mWheel.getNumOfItems(); itemIndex++ ) {
            WheelItem item = mWheel.getItemAt(itemIndex);
            int startAngle = mWheel.getStartAngleAt(itemIndex);
            int endAngle = mWheel.getEndAngleAt(itemIndex);
            int angleDiff = endAngle - startAngle;
        	 
            //Draw the colored highlight for this piece in the pie
            int color = item.getColor();
            int alpha = 200;
            if ( itemIndex == mSelectedItemIndex ){
            	alpha = 120;
            }   
            color = Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
            mPaint.setColor(color);
            mPaint.setStyle(Style.FILL);
            int width = (int)(item.getValue()*mDiskWidth);
            mPiePieceBounds.set(centerX-width, centerY-width, centerX+width, centerY+width);
            canvas.drawArc(mPiePieceBounds, startAngle, angleDiff, true, mPaint);

            //draw the piece arc border 
            mPaint.setColor(Color.rgb(64, 0, 128));               
            mPaint.setStyle(Style.STROKE);
          	mPaint.setStrokeWidth(6);
          	canvas.drawArc(mPiePieceBounds,startAngle,angleDiff, false, mPaint);

          	// draw the item value number 
	        mTextPath.reset();
	        mPaint.setColor(Color.BLACK);
	        mPaint.setStyle(Style.FILL_AND_STROKE);    
	        mPaint.setStrokeWidth(1);
	        width = (int)(4.5*mDiskWidth);
	        mPiePieceBounds.set(centerX-width, centerY-width, centerX+width, centerY+width);            
	        long angleOffset = (long) (0.3*angleDiff);
	       	mTextPath.addArc(mPiePieceBounds, startAngle+angleOffset, angleDiff-angleOffset);
	        canvas.drawTextOnPath(String.valueOf(item.getValue()), mTextPath, 0, 0, mPaint);            
	        mTextPath.reset();

        } 

    }
    

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mBounds = new RectF(0, 0, w, h);
        mDiskWidth = Math.min(w,h)/TOTAL_CIRCLES;         
    }
    
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {

    	if ( !this.mDraggable ){
    		// just select the item, don't do any dragging
    		selectItem(event.getX(), event.getY());
    		return false;    		
    	}
    		
    	// Let the GestureDetector interpret this event  	    	
	    boolean result = mDetector.onTouchEvent(event);

	    if (!result) {
	    	// GestureDetector didn't want this event
	    	// if the event action ACTION_UP then the user has finished dragging
	        if (event.getAction() == MotionEvent.ACTION_UP) {
	            // User is done dragging
	        	if ( mSelectedItemIndex >= 0 && mIsDragging){
		        	dragValueTo(event.getX(), event.getY());
	        	}
	        	
	            stopDragging();
	            invalidate();
	            result = true;
	        }
	    } 
	    return result;
	} 
   

    private void dragValueTo(float eventX, float eventY){
    	float x = eventX - mBounds.centerX();
    	float y = mBounds.centerY() -  eventY;		        
    	
		WheelItem wheelItem = mWheel.getItemAt(mSelectedItemIndex); 
    	int oldValue = wheelItem.getValue();
    	double diff =  Math.sqrt(x * x + y * y) / mDiskWidth - oldValue;
    	double absDiff = Math.abs(diff);
    	
    	if ( absDiff > 0.5 ){
    		int itemValue = oldValue + (int)(Math.signum(diff)*Math.ceil(absDiff));
    		itemValue = Math.min(itemValue, 10);
    		itemValue = Math.max(itemValue, 1);		        	
    		wheelItem.setValue(itemValue);
    		if ( oldValue != itemValue && mValueChangedListener != null){
    			mValueChangedListener.onWheelItemValueChanged(wheelItem);
    		}
    	}
    	
    }
    
    private void startDragging(MotionEvent e){   	
 	    mIsDragging = true;
 	    accelerate();
 	    selectItem(e.getX(), e.getY());   	
    }

    private void stopDragging(){
    	mIsDragging = false;
    	mSelectedItemIndex = -1;
    	invalidate();
    	decelerate();
    }
      
  
    
    // Extends GestureDetector.SimpleOnGestureListener to provide custom gesture processing.
    private class DragGestureListener extends GestureDetector.SimpleOnGestureListener {
    	
        @Override
        public boolean onDown(MotionEvent e) {        	
        	startDragging(e);
        	return true;
        }
        
    	@Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        	       	
            if (mSelectedItemIndex >= 0 && mSelectedItemIndex < mWheel.getNumOfItems()) {            	
            	dragValueTo(e2.getX(), e2.getY());
        		invalidate();
        		return true;
        	}
            return false;
        }

    }
    

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private class WheelAnimatorListener implements AnimatorListener, ValueAnimator.AnimatorUpdateListener {
        // implements AnimatorListener/AnimatorUpdateListener to handle animation updates and end/cancel
    	
		@Override
		public void onAnimationStart(Animator animation) {
		}

		@Override
		public void onAnimationEnd(Animator animation) {
			decelerate();
		}

		@Override
		public void onAnimationCancel(Animator animation) {
			decelerate();
		}

		@Override
		public void onAnimationRepeat(Animator animation) {
		}

		@Override
		public void onAnimationUpdate(ValueAnimator animation) {
			invalidate();
		}
    	
    }
    
   
    // calculate the index of the item clicked, save it as mSelectedItemIndex + fire onWheelItemClicked event
    private void selectItem(float eventX, float eventY){
 	    double angle = calcAngle(eventX, eventY);
    	mSelectedItemIndex = calcSelectedIndex(angle);
    	if (this.mItemClickedListener != null  && mSelectedItemIndex>=0 ){
    		mItemClickedListener.onWheelItemClicked(mSelectedItemIndex);
    	}    	
    	invalidate(); // we need to redraw so that the selected item alpha changes
    }
    
    // given an eventX, eventY position calculate the angle in degrees of the line from the center of the wheel to the given position
    private double calcAngle(float eventX, float eventY){
    	float x = eventX - mBounds.centerX();
    	float y = mBounds.centerY() -  eventY;
    	double angle = Math.toDegrees(Math.atan2(Math.abs(y),x));
    	if ( y > 0 ){
    		angle = 360 - angle;
    	}
    	return angle;
    }
    
    // finds and returns the selected (clicked) wheel item index
    private int calcSelectedIndex(double angle){

    	for ( int i=0; i<mWheel.getNumOfItems(); i++ ){
    		if ( mWheel.getStartAngleAt(i) < angle && angle <= mWheel.getEndAngleAt(i)  ){
    			return i;
    		}   			
    	}
    	return -1;    	
    }
    
    
    // Enable hardware acceleration (consumes memory)
    private void accelerate() {
       setLayerToHW(this);
    }    
    // Disable hardware acceleration (releases memory)
    private void decelerate() {
       setLayerToSW(this);
    }
    
    @TargetApi(Build.VERSION_CODES.HONEYCOMB) 
    private void setLayerToSW(View v) {
        if (!v.isInEditMode() && Build.VERSION.SDK_INT >= 11) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }
    
    @TargetApi(Build.VERSION_CODES.HONEYCOMB) 
    private void setLayerToHW(View v) {
        if (!v.isInEditMode() && Build.VERSION.SDK_INT >= 11) {
            setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
    }
    
   
}
