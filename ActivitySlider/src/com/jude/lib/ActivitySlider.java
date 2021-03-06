package com.jude.lib;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Activity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

/**
 * 
 * @author Jude
 * @date 2014年10月11日
 * 
 * activity滑动时间处理类
 */
public class ActivitySlider{
	private Activity act;
	private View rootView;
	private VelocityTracker verTracker; 
	
	private int duration = 500;
	private Interpolator interpolator = new DecelerateInterpolator();
	
	
	/**
	 * 当滑动速度超过此值得时关闭activity
	 */
	public int VELOCITY_CLOSE = 1000;
	/**
	 * 小于此值则认为反方向滑动
	 */
	private final static int VELOCITY_BACK= -100;
	
	/**
	 * activity关闭时的动画速度与手指滑动速度度的比例[0~1]
	 */
	public float VELOCITY_RATIO_ANIM= 0.5f;
	/**
	 * 当滑动超过屏幕多少比例的时关闭activity
	 */
	public float OFFSET_RATIO_CLOSE= 0.4f;
	/**
	 * 在以此为边长的矩形的区域判断手势方向
	 */
	private final static int JUDGEEDGE= 12;
	
	private int state;
	private int width;
	private int beginTouchX;
	private int beginTouchY;
	private float deltaX;
	private float deltaY;
	private int startX;
	
	/**
	 * 从按下到弹起的一套动作是否有效
	 */
	private boolean isActionAvailable=false;
	/**
	 * 状态：空闲  滑动   按下    动画    
	 */
	public final static int STATE_FREE=0;
	public final static int STATE_SLIDING=1;
	public final static int STATE_TOUCHING=2;
	public final static int STATE_ANIMING=3;

	public ActivitySlider(Activity act){
		this.act=act;
		this.width=getScreenWidth();
		rootView=act.getWindow().getDecorView();
	}
	
	/**
	 * 在Activity的dispatchTouchEvent中调用
	 * @param ev
	 * @return
	 */
	public boolean onTouch(MotionEvent ev){
		switch(ev.getAction()){
		case MotionEvent.ACTION_DOWN:
			if(state==STATE_FREE){
				verTracker= VelocityTracker.obtain(); 

				beginTouchX=(int) ev.getRawX();
				beginTouchY=(int) ev.getRawY();

				state=STATE_TOUCHING;
				isActionAvailable=true;
				return false;
			}else{
				isActionAvailable=false;
				return true;
			}
			
		case MotionEvent.ACTION_MOVE:

			if(!isActionAvailable){
				return true;
			}
			verTracker.addMovement(ev);
			deltaX=Math.abs(ev.getRawX()-beginTouchX);
			deltaY=Math.abs(ev.getRawY()-beginTouchY);
			if(ev.getRawX()-beginTouchX<0){//左移忽略
				beginTouchX=(int) ev.getRawX();
				return false;
			}
			switch(state){
			case STATE_TOUCHING:
				if(deltaX < JUDGEEDGE && deltaY < JUDGEEDGE){//小矩形中不处理
					return false;
				}else{//首次离开小矩形，判断是横向移动还是纵向移动
					if(deltaY/deltaX<1){//横向移动,跟随滑动activity，并锁定状态
						rootView.scrollTo(beginTouchX-(int) ev.getRawX(), 0);
						state=STATE_SLIDING;
						ev.setAction(MotionEvent.ACTION_CANCEL);//第一次横向移动，取消子view的按下状态
						return false;
					}else{//纵向移动，数移放行此动作，并锁定状态
						state=STATE_FREE;
						return false;
					}
				}
			case STATE_SLIDING:
				rootView.scrollTo(beginTouchX-(int) ev.getRawX(), 0);
				return true;
			case STATE_FREE:
				return false;
			}
			break;
			
		case MotionEvent.ACTION_UP:
			if(!isActionAvailable){
				return true;
			}
			verTracker.computeCurrentVelocity(1000);
			if(state==STATE_SLIDING){
				startX=(int) (beginTouchX-ev.getRawX());
				state=STATE_ANIMING;
				
				if(startX>0){//左移胡略
					state=STATE_FREE;
					rootView.scrollTo(0, 0);
				}
				if(verTracker.getXVelocity()<VELOCITY_BACK){//速度为负数，向左滑的处理
					AnimToBack((int)(verTracker.getXVelocity()*VELOCITY_RATIO_ANIM));
						
				}else if(verTracker.getXVelocity()>VELOCITY_CLOSE){//速度大于界限速度
					if(-startX>JUDGEEDGE){
						AnimToClose((int)(verTracker.getXVelocity()*VELOCITY_RATIO_ANIM));
					}else{
						AnimToBack();
					}
				}else {//速度[-100,界定速度]			
					if(-startX>(width*OFFSET_RATIO_CLOSE)){//滑动位置>界定屏幕
						Log.i("test", "位移大于屏幕");
						AnimToClose();
					}else{//滑动位置[0,界定屏幕]
						AnimToBack();
					}
				}
			}else{
				state=STATE_FREE;
			}
			verTracker.recycle();
			verTracker=null;
			break;
		default:
			//未识别手势
		}
		return false;
	}
	
	private ValueAnimator creatAnimator(int startX,int endX,int duration,final Runnable endRunnable){
		ValueAnimator anim = ValueAnimator.ofInt(startX,endX);
		anim.setDuration(Math.abs(duration));
		anim.setInterpolator(interpolator);
		anim.addUpdateListener(new AnimatorUpdateListener() {
			
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				rootView.scrollTo((Integer)animation.getAnimatedValue(), 0);
			}
		});
		anim.addListener(new AnimatorListener() {
			
			@Override
			public void onAnimationStart(Animator animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animator animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animator animation) {
				endRunnable.run();
			}
			
			@Override
			public void onAnimationCancel(Animator animation) {
				// TODO Auto-generated method stub
				
			}
		});
		return anim;
	}
	
	
	private void AnimToClose(){
		creatAnimator(startX, -width, duration, new Runnable(){

			@Override
			public void run() {	
				act.finish();
			}

		}).start();
	}
	
	private void AnimToClose(int velocity){
		creatAnimator(startX, -width, (width+startX)*1000/velocity, new Runnable(){

			@Override
			public void run() {	
				act.finish();
			}

		}).start();
	}
	
	private void AnimToBack(){
		creatAnimator(startX, 0, duration, new Runnable(){

			@Override
			public void run() {	
				state=STATE_FREE;
			}

		}).start();
	}
	
	private void AnimToBack(int velocity){
		creatAnimator(startX, 0, -startX*1000/velocity, new Runnable(){

			@Override
			public void run() {	
				state=STATE_FREE;
			}

		}).start();

	}
	
	/**
	 * 获取状态
	 * @return
	 */
	public int getState() {
		return state;
	}
	/**
	 * 获取动画持续时间
	 */
	public int getDuration() {
		return duration;
	}
	
	/**
	 * 设置动画持续时间，默认1000ms
	 */
	public void setDuration(int duration) {
		this.duration = duration==0?1000:duration;
	}
	
	/**
	 * 设置速度选择器，默认先加后减移动
	 * @return
	 */
	public void setInterpolator(Interpolator interpolator) {
		this.interpolator = interpolator;
	}

	   
    /**
     * 取屏幕宽度
     * @param ctx
     * @return
     */
    public  int getScreenWidth(){
    	DisplayMetrics dm = act.getResources().getDisplayMetrics();
    	return dm.widthPixels;
    }
    
    /**
     * 取屏幕高度
     * @param ctx
     * @return
     */
    public  int getScreenHeight(){
    	DisplayMetrics dm = act.getResources().getDisplayMetrics();
    	return dm.heightPixels;
    }
	

}
