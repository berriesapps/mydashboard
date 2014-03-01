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

import com.berries.dashboard.R;
import com.berries.dashboard.model.Wheel;
import com.berries.dashboard.model.WheelItem;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.View;
import android.graphics.Color;

/**
 * The WheelBackgroundView, draws all the background circles and text that remain constant regardless of the user Gestures
 */
public class WheelBackgroundView extends View  {

   
	private Wheel mWheel;	
	private Path  mTextPath;
	private RectF mBounds;
	private RectF mPiePieceBounds;
	private Paint mPaint;
	private float mTextMaxWidth;
	private float mCenterX;
	private float mCenterY;
	private boolean mUseOtherCenter;
	private int mBgColor;
	private int mOuterBgColor;
	private static final int TOTAL_CIRCLES = 22;

    
	public WheelBackgroundView(Context context) {
		super(context);
		mUseOtherCenter = false;
		mTextPath = new Path();
		mPiePieceBounds = new RectF();
		mPaint  = new Paint();
		mPaint.setColor(Color.DKGRAY);
		mPaint.setStyle(Style.STROKE);            
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setStrokeWidth(2);
		mPaint.setTextSize(19); 
		mBgColor = context.getResources().getColor(R.color.WheelBackgroundColor);
		mOuterBgColor = context.getResources().getColor(R.color.WheelOuterBackgroundColor);		
	}
	
	public void setWheel(Wheel wheel){
		mWheel = wheel;
		invalidate();
	}
	
	public Wheel getWheel(){
		return mWheel;
	}
	
	public void setCircleCenter(float centerX, float centerY){
		mUseOtherCenter = true;
		mCenterX = centerX;
		mCenterY = centerY;
	}
    
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float centerX, centerY;
        if ( mUseOtherCenter ){
        	centerX = mCenterX;
        	centerY = mCenterY;
        }
        else {
        	centerX = mBounds.centerX();
        	centerY = mBounds.centerY();
        }
        float diskWidth = Math.min(mBounds.width(),mBounds.height())/TOTAL_CIRCLES;
        
        // Draw the background borders        
        // draw the 9 first circle outlines with regular stroke 
        mPaint.setStyle(Style.STROKE);
        mPaint.setColor(Color.DKGRAY);
        mPaint.setStrokeWidth(1);        
        for (int i=2; i<=8; i+=2)
        	canvas.drawCircle(centerX, centerY, i*diskWidth, mPaint);
        
        // draw the 10th circle outlines with double stroke, black color
        mPaint.setStrokeWidth(3);
        mPaint.setColor(Color.BLACK);
        canvas.drawCircle(centerX, centerY, 10*diskWidth, mPaint);
        
        mPaint.setStrokeWidth(0.5f);
        mPaint.setColor(Color.LTGRAY);
        canvas.drawCircle(centerX, centerY, 12*diskWidth, mPaint);
        canvas.drawCircle(centerX, centerY, 15*diskWidth, mPaint);
        canvas.drawCircle(centerX, centerY, 19*diskWidth, mPaint);
        canvas.drawCircle(centerX, centerY, 25*diskWidth, mPaint);        
        canvas.drawCircle(centerX, centerY, 32*diskWidth, mPaint);        
        
        // fill the outer circle with a light background
        mPaint.setColor(mBgColor);  
        mPaint.setStyle(Style.FILL);
        canvas.drawCircle(centerX, centerY, 10*diskWidth, mPaint);
        mPaint.setColor(mOuterBgColor);
        canvas.drawCircle(centerX, centerY, 32*diskWidth, mPaint);        

        // Draw the Items within the wheel  
        int numOfItems = mWheel==null?0:mWheel.getNumOfItems();
        for (int itemIndex = 0; itemIndex < numOfItems; itemIndex++ ) {
            WheelItem it = mWheel.getItemAt(itemIndex);
            int startAngle = mWheel.getStartAngleAt(itemIndex);
            int endAngle = mWheel.getEndAngleAt(itemIndex);
            int width;
            
            // draw the text describing this item on the outer circle
            mPaint.setStrokeWidth(2);
            mPaint.setColor(Color.BLACK);
            mPaint.setStyle(Style.FILL);                       
            width = (int)(10.2*diskWidth);
            mPiePieceBounds.set(centerX-width, centerY-width, centerX+width,centerY+width);            
            mTextPath.addArc(mPiePieceBounds, startAngle+2, endAngle - startAngle-2);            
            String itemName = it.getName();
            int textEndIndex = mPaint.breakText(itemName, true, mTextMaxWidth, null);
            if ( textEndIndex < it.getName().length() ){
            	itemName = it.getName().substring(0, textEndIndex);
            }
            canvas.drawTextOnPath(itemName, mTextPath, 5, 0, mPaint);
            mTextPath.reset();

            // Draw the borders of the piece
            mPaint.setStrokeWidth(0.5f);
            mPaint.setColor(Color.LTGRAY);
            mPaint.setStyle(Style.STROKE);                                               
            width = (int)(32*diskWidth);
            mPiePieceBounds.set(centerX-width, centerY-width, centerX+width, centerY+width);
            canvas.drawArc(mPiePieceBounds, startAngle, endAngle-startAngle, true, mPaint);            
            
        } 

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mBounds = new RectF(0, 0, w, h);
        calcMaxTextWidth();
    }
    
    
    private void calcMaxTextWidth(){
    	if ( mWheel != null ){
    		if ( mWheel.getNumOfItems()>0 ){
	            float diskWidth = Math.min(mBounds.width(),mBounds.height())/TOTAL_CIRCLES;    		
	            float textRadius = (float)(10.2 * diskWidth);
	            float circlePerimeter = 2 * (float)Math.PI * textRadius; 
	            float offsetDist = circlePerimeter * 4 / 360 ;
	    		mTextMaxWidth =   circlePerimeter / mWheel.getNumOfItems()  - offsetDist;
    		}
    	}
    }
}
