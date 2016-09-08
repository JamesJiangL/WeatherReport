package com.james_jiang.weatherreport.Common;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * Created by JC on 2016/8/10.
 */
public class FullScreenVideoView extends VideoView {
    public FullScreenVideoView(Context context) {
        super(context);
    }
    public FullScreenVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public FullScreenVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    @Override
    public void onMeasure(int width, int height){
        int w = getDefaultSize(0, width);
        int h = getDefaultSize(0, height);
        setMeasuredDimension(w, h);
    }
}
