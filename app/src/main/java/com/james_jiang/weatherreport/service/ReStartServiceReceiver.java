package com.james_jiang.weatherreport.service;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.james_jiang.weatherreport.Common.MyApplication;

/**
 * Created by JC on 2016/8/29.
 */
public class ReStartServiceReceiver extends BroadcastReceiver {
    private final static String TAG = "ReStartServiceReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, intent.getAction());
        if (!isServiceRunning()) {
            Log.e(TAG, "ReStartServiceReceiver");
            Intent restart = new Intent(context, UpdateService.class);
            context.startService(restart);
        }
    }
    //检查服务是否已经运行
    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager)
                MyApplication.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.james_jiang.weatherreport.service.UpdateService"
                    .equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
