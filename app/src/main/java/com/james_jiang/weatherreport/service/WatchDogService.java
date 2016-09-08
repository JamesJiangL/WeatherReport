package com.james_jiang.weatherreport.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.james_jiang.weatherreport.Common.MyApplication;

/**
 * Created by JC on 2016/8/31.
 */
public class WatchDogService extends IntentService {
    private final static String TAG = "WatchDogService";
    public WatchDogService(){
        super(TAG);
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        if(intent == null){
//            Log.e(TAG, "intent == null");
            return;
        }
        String type = intent.getStringExtra("widget_click_type");
        Intent broadcastIntent = null;
        switch (type){
            case MyApplication.WIDGET_LEFT:
                broadcastIntent = new Intent(MyApplication.WIDGET_LEFT);
                break;
            case MyApplication.WIDGET_RIGHT:
                broadcastIntent = new Intent(MyApplication.WIDGET_RIGHT);
                break;
            case MyApplication.REFRESH_WIDGET:
                broadcastIntent = new Intent(MyApplication.REFRESH_WIDGET);
                break;
        }
        if(broadcastIntent != null) {
            sendBroadcast(broadcastIntent);
        }
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.e(TAG, "onDestroy");
    }
}
