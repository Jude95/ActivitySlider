简要说明
-
仿制的知乎的Activity滑动关闭效果。是对Activity顶级视图直接进行移动。

让activity跟随手指滑动并关闭。 

使用方法
-
先实例化一个ActivitySlider对象
>
    actSlider = new ActivitySlider(this);

然后重写dispatchTouchEvent（）方法

>
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

设置activity的主题为


	    <!-- transparent theme. -->
	    <style name="TransparentTheme" parent="AppTheme">
	       	<item name="android:windowBackground">@android:color/transparent</item>
	　　		<item name="android:windowIsTranslucent">true</item>
	　　		<item name="android:windowAnimationStyle">@android:style/Animation.Translucent</item>
	    </style>

就好了