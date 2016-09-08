package com.james_jiang.weatherreport.Common;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Environment;

import com.baidu.apistore.sdk.ApiStoreSDK;

import java.lang.reflect.Field;

/**
 * Created by JC on 2016/8/8.
 */
public class MyApplication extends Application {
    private static Context context;
    public final static String WCACHE = Environment.getExternalStorageDirectory() +
            "/Android/data/com.james_jiang.weatherreport/wCache";       //天气文件缓存位置
    public final static String UPDATE_NOTIFICATION =
            "com.james_jiang.weatherreport.UPDATE_NOTIFICATION";        //更新通知栏的广播
    public final static String UPDATE_WEATHER_PAGE =
            "com.james_jiang.weatherreport.UPDATE_WEATHER_PAGE";        //更新界面的广播
    public final static String UPDATE_ANIMATION =
            "com.james_jiang.weatherreport.UPDATE_ANIMATION";           //更新界面动画
    public final static String UPDATE_VIDEO =
            "com.james_jiang.weatherreport.UPDATE_VIDEO";
    public final static String UNREGISTER_RECEIVER =
            "com.james_jiang.weatherreport.UNREGISTER_RECEIVER";
    public final static String ADD_BUTTON =
            "com.james_jiang.weatherreport.ADD_BUTTON";
    public final static String MORE_BUTTON =
            "com.james_jiang.weatherreport.MORE_BUTTON";
    public final static String USE_LOCATION =
            "com.james_jiang.weatherreport.USE_LOCATION";
    public final static String LOCATION =
            "com.james_jiang.weatherreport.LOCATION";
    public final static String UPDATE_WIDGET =
            "com.james_jiang.weatherreport.UPDATE_WIDGET";
    public final static String WIDGET_LEFT =
            "com.james_jiang.weatherreport.WIDGET_LEFT";
    public final static String WIDGET_RIGHT =
            "com.james_jiang.weatherreport.WIDGET_RIGHT";
    public final static String UPDATE_WIDGET_TIME =
            "com.james_jiang.weatherreport.UPDATE_WIDGET_TIME";
    public final static String REFRESH_WIDGET =
            "com.james_jiang.weatherreport.REFRESH_WIDGET";
    private static Typeface typeface;

    @Override
    public void onCreate(){
        super.onCreate();
        context = getApplicationContext();
        typeface = Typeface.createFromAsset(getAssets(), "roboto_light.ttf");
        setFont(typeface);
        ApiStoreSDK.init(this, "8c92d612e03cf4cb3c9ccaffac4830b5");
    }
    public static Context getContext(){
        return context;
    }
    private void setFont(Typeface typeface){
        try {
            Field field = Typeface.class.getDeclaredField("SERIF");
            field.setAccessible(true);
            field.set(null, typeface);
        }
        catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
