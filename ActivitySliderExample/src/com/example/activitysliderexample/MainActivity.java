package com.example.activitysliderexample;

import com.jude.lib.ActivitySlider;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;


/**
 * 
 * @author jude
 * @date 2014/10/11
 * if you want your Activity can follow gestures sliding,you must do the following:
 * 
 * 1.you must alter the dispatchTouchEvent() to send the MotionEvent to ActivitySlider,
 * it can moves your activity
 * 
 * 2.you must change your Activity's style to Transparent like:
 *  <!-- transparent theme. -->
    <style name="TransparentTheme" parent="AppTheme">
       	<item name="android:windowBackground">@android:color/transparent</item>
　　		<item name="android:windowIsTranslucent">true</item>
　　		<item name="android:windowAnimationStyle">@android:style/Animation.Translucent</item>
    </style>
 * 
 * 
 * 3.you should chenge your root layout's background because it must be transparent.
 * 
 */

public class MainActivity extends Activity {
	//
	private ActivitySlider actSlider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //put the activity int ActivitySlider;
        actSlider = new ActivitySlider(this);
    }

    
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		//use onTouch(ev) to send the MotionEvent to ActivitySlider
		if(actSlider==null||!actSlider.onTouch(ev)){
			//onTouch return false mean the MotionEvent should be hand on.
			return super.dispatchTouchEvent(ev);
		}else{
			//onTouch return true mean the MotionEvent should be intercepted.
			return true;
		}	
	}
}
