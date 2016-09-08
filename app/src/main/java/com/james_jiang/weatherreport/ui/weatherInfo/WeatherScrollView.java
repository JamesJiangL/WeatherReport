package com.james_jiang.weatherreport.ui.weatherInfo;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by JC on 2016/8/24.
 */
public class WeatherScrollView extends ScrollView {
    private final static String TAG = "WeatherScrollView";
    private ScrollViewListener listener = null;
    private GestureDetector mGestureDetector;

    public WeatherScrollView(Context context) {
        super(context);
        init();
    }

    public WeatherScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WeatherScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public WeatherScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void setScrollViewListener(ScrollViewListener listener){
        this.listener = listener;
    }
    @Override
    protected void onScrollChanged(int x, int y, int oldX, int oldY){
        super.onScrollChanged(x, y, oldX, oldY);
        if(listener != null){
            listener.onScrollChanged(this, x, y, oldX, oldY);
        }
    }
    private void init() {
        mGestureDetector = new GestureDetector(getContext(),
                new YScrollDetector());
        setFadingEdgeLength(0);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
//        Log.e(TAG, "dispatchTouchEvent");
        return super.dispatchTouchEvent(event);
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {      //截断Touch传递,默认返回false
//        Log.e(TAG, "onInterceptTouchEvent");
//        if(event.getAction() == MotionEvent.ACTION_DOWN)
//            return true;
        return super.onInterceptTouchEvent(event)
                && mGestureDetector.onTouchEvent(event);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event){
//        Log.e(TAG, "onTouchEvent");
        return super.onTouchEvent(event);
    }

    private class YScrollDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            if (Math.abs(distanceY) >= Math.abs(distanceX)) {
                return true;
            }
            return false;
        }
    }
}
