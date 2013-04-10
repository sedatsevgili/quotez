/**
 * source: http://android-journey.blogspot.com/2010/01/android-gestures.html
 */
package com.tatlisoft.quotez.utils;

import com.tatlisoft.quotez.utils.SimpleGestureListener;

import android.app.Activity;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

public class SimpleGestureFilter extends SimpleOnGestureListener {

	 public final static int SWIPE_UP = 1; 
	 public final static int SWIPE_DOWN = 2; 
	 public final static int SWIPE_LEFT = 3; 
	 public final static int SWIPE_RIGHT = 4; 
	 
	 public final static int MODE_TRANSPARENT = 0; 
	 public final static int MODE_SOLID = 1; 
	 public final static int MODE_DYNAMIC = 2; 
	 
	 private final static int ACTION_FAKE = -13; //just an unlikely number 
	 private int _swipeMinDistance = 100; 
	 private int _swipeMaxDistance = 350; 
	 private int _swipeMinVelocity = 100; 
	 
	 private int _mode = MODE_DYNAMIC; 
	 private boolean _running = true; 
	 private boolean _tapIndicator = false; 
	 
	 private Activity _context; 
	 private GestureDetector _detector; 
	 private SimpleGestureListener _listener; 
	 
	 
	 public SimpleGestureFilter(Activity context,SimpleGestureListener sgl) { 
		 this._context = context; 
		 this._detector = new GestureDetector(context, this); 
		 this._listener = sgl; 
	 } 
	 
	 public void onTouchEvent(MotionEvent event){ 
		 if(!this._running) {
			 return;
		 }
		 
		 boolean result = this._detector.onTouchEvent(event); 
		 if(this._mode == MODE_SOLID) {
			 event.setAction(MotionEvent.ACTION_CANCEL); 
		 } else if (this._mode == MODE_DYNAMIC) { 
			 if(event.getAction() == ACTION_FAKE) { 
				 event.setAction(MotionEvent.ACTION_UP); 
			 } else if (result) { 
				 event.setAction(MotionEvent.ACTION_CANCEL); 
			 } else if(this._tapIndicator){ 
				 event.setAction(MotionEvent.ACTION_DOWN); 
				 this._tapIndicator = false; 
			 } 
		 } 
		 //else just do nothing, it's Transparent 
	 } 
	 
	 public void setMode(int m){ 
		 this._mode = m; 
	 } 
	 
	 public int getMode(){ 
		 return this._mode; 
	 } 
	 
	 public void setEnabled(boolean status){ 
		 this._running = status; 
	 } 
	 
	 public void setSwipeMaxDistance(int distance){ 
		 this._swipeMaxDistance = distance; 
	 } 
	 
	 public void setSwipeMinDistance(int distance){ 
		 this._swipeMinDistance = distance; 
	 } 
	 
	 public void setSwipeMinVelocity(int distance){ 
		 this._swipeMinVelocity = distance; 
	 } 
	 
	 public int getSwipeMaxDistance(){ 
		 return this._swipeMaxDistance; 
	 } 
	 
	 public int getSwipeMinDistance(){ 
		 return this._swipeMinDistance; 
	 } 
	 
	 public int getSwipeMinVelocity(){ 
		 return this._swipeMinVelocity; 
	 } 
	 
	 
	 @Override 
	 public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) { 
	 
		 final float xDistance = Math.abs(e1.getX() - e2.getX()); 
		 final float yDistance = Math.abs(e1.getY() - e2.getY()); 
		 
		 if(xDistance > this._swipeMaxDistance || yDistance > this._swipeMaxDistance) 
			 return false; 
		 
		 velocityX = Math.abs(velocityX); 
		 velocityY = Math.abs(velocityY); 
		 boolean result = false; 
		 
		 if(velocityX > this._swipeMinVelocity && xDistance > this._swipeMinDistance){ 
			 if(e1.getX() > e2.getX()) { // right to left 
				 this._listener.onSwipe(SWIPE_LEFT); 
			 } else { 
				 this._listener.onSwipe(SWIPE_RIGHT);
			 }
			 result = true; 
		 } else if(velocityY > this._swipeMinVelocity && yDistance > this._swipeMinDistance){ 
			 if(e1.getY() > e2.getY()) { // bottom to up 
				 this._listener.onSwipe(SWIPE_UP); 
			 } else { 
				 this._listener.onSwipe(SWIPE_DOWN);
			 }
			 result = true; 
		 } 
		 return result; 
	 } 
	 
	 @Override 
	 public boolean onSingleTapUp(MotionEvent e) { 
		 this._tapIndicator = true; 
		 return false; 
	 } 
	 
	 @Override 
	 public boolean onDoubleTap(MotionEvent arg0) { 
		 this._listener.onDoubleTap();
		 return true; 
	 } 
	 
	 @Override 
	 public boolean onDoubleTapEvent(MotionEvent arg0) { 
		 return true; 
	 } 
	 
	 @Override 
	 public boolean onSingleTapConfirmed(MotionEvent arg0) { 
		 if(this._mode == MODE_DYNAMIC){ // we owe an ACTION_UP, so we fake an 
			 arg0.setAction(ACTION_FAKE); //action which will be converted to an ACTION_UP later. 
			 this._context.dispatchTouchEvent(arg0); 
		 } 
		 return false; 
	 } 
	 
}
