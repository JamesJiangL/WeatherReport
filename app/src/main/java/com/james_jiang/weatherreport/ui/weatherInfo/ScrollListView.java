package com.james_jiang.weatherreport.ui.weatherInfo;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * Created by JC on 2016/8/22.
 */
public class ScrollListView extends ListView {
    private final static String TAG = "ScrollListView";
    public ScrollListView(Context context) {
        super(context);
    }
    public ScrollListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public ScrollListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ScrollListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
//        Log.e(TAG, "dispatchTouchEvent");
        return super.dispatchTouchEvent(event);
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {      //截断Touch传递,默认返回false
//        Log.e(TAG, "onInterceptTouchEvent");
        return super.onInterceptTouchEvent(event);
    }
}
