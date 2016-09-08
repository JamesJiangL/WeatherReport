package com.james_jiang.weatherreport.service;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.text.format.Time;
import android.util.Log;
import android.widget.RemoteViews;

import com.james_jiang.weatherreport.AreasWeatherInfo.WeatherInfo.DailyForecast;
import com.james_jiang.weatherreport.AreasWeatherInfo.WeatherInfo.WeatherInfo;
import com.james_jiang.weatherreport.Common.MyApplication;
import com.james_jiang.weatherreport.R;
import com.james_jiang.weatherreport.Utils.File.FileOperateUtils;
import com.james_jiang.weatherreport.Utils.Http.HttpCallBackListener;
import com.james_jiang.weatherreport.Utils.Http.HttpUtil;
import com.james_jiang.weatherreport.Utils.Http.JsonUtil;
import com.james_jiang.weatherreport.Utils.Image.ImageDecodesUtil;
import com.james_jiang.weatherreport.Utils.ScreenUtil;
import com.james_jiang.weatherreport.Utils.SharedPreferencesUtil;
import com.james_jiang.weatherreport.Utils.ToastUtil;
import com.james_jiang.weatherreport.ui.splash.SplashActivity;
import com.james_jiang.weatherreport.ui.weatherInfo.WeatherCode;

import java.util.Date;

/**
 * Created by JC on 2016/8/14.
 */
public class UpdateService extends Service{
    private final static String TAG = "UpdateService";
    private RemoteViews remoteViews;    //notification自定义布局
    private NotificationManager manager;    //notification管理器
    private Notification notification;
    private SharedPreferencesUtil preferencesUtil;
    private UpdateNotification updateNotification;
    private LocalBroadcastManager localBroadcastManager;
//    private ReStartServiceReceiver reStartServiceReceiver;
    private Bitmap sunny_d;
    private Bitmap sunny_n;
    private Bitmap rain_d;
    private Bitmap rain_n;
    private Bitmap cloud_d;
    private Bitmap cloud_n;
    private Bitmap snow_d;
    private Bitmap snow_n;
    private Bitmap fog_d;
    private Bitmap fog_n;
    private Bitmap thunder_storm_d;
    private Bitmap thunder_storm_n;
    private Bitmap unknown_d;
    private Bitmap unknown_n;
    private int updateTimes;
    private int enterTimes;
    @Override
    public void onCreate(){
        super.onCreate();
        initService();
    }
    private void initService(){
        preferencesUtil = SharedPreferencesUtil.getInstance(this);
        enterTimes = 0;
        initNotification();
        initBroadCastReceiver();
    }
    //初始化广播
    private void initBroadCastReceiver(){
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(MyApplication.UPDATE_NOTIFICATION);
        updateNotification = new UpdateNotification();
        localBroadcastManager.registerReceiver(updateNotification, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Log.e(TAG, "onStartCommand");
        enterTimes++;
        //定时更新天气信息
        if(enterTimes > 1) {
            if(!preferencesUtil.MyRead("manual_update", false)) {
                updateWeather();
            }
        }
        //需要在配置文件中设置
        showNotification(getWeatherInfo());
        //定时任务
        timeOpening();
        return super.onStartCommand(intent, flags, startId);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void timeOpening(){
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int time = //1000 * 5;
         60 * 60 * 1000 * preferencesUtil.MyRead("updateTime", 1);   //默认1小时定时
        long triggerAtTime = SystemClock.elapsedRealtime() + time;
        Intent intent1 = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent1, 0);
        manager.setExact(AlarmManager.ELAPSED_REALTIME, triggerAtTime, pendingIntent);
    }
    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "onBind");
        return null;
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.e(TAG, "onDestroy");
        if(localBroadcastManager != null){
            localBroadcastManager.unregisterReceiver(updateNotification);
            localBroadcastManager = null;
            updateNotification = null;
        }
        recycleBitmap();
        System.gc();
        if(!isServiceRunning()) {
            Intent intent = new Intent(this, UpdateService.class);
            startService(intent);
        }
    }
    private void recycleBitmap(){
        if(sunny_d != null) {
            sunny_d.recycle();
        }
        if(sunny_n != null) {
            sunny_n.recycle();
        }
        if(rain_d != null) {
            rain_d.recycle();
        }
        if(rain_n != null) {
            rain_n.recycle();
        }
        if(cloud_d != null) {
            cloud_d.recycle();
        }
        if(cloud_n != null) {
            cloud_n.recycle();
        }
        if(snow_d != null) {
            snow_d.recycle();
        }
        if(snow_n != null) {
            snow_n.recycle();
        }
        if(fog_d != null) {
            fog_d.recycle();
        }
        if(fog_n != null) {
            fog_n.recycle();
        }
        if(thunder_storm_d != null) {
            thunder_storm_d.recycle();
        }
        if(thunder_storm_n != null) {
            thunder_storm_n.recycle();
        }
        if(unknown_d != null) {
            unknown_d.recycle();
        }
        if(unknown_n != null) {
            unknown_n.recycle();
        }
    }
    //定时刷新天气信息
    private void updateWeather(){
        updateTimes = 0;
        final String city = preferencesUtil.MyRead("city_list", ",");
        if(city.length() < 6)
            return;
        final String[] citys = city.split(",");
        for (String city1 : citys) {
            HttpUtil.weatherRequest(city1, new HttpCallBackListener() {
                @Override
                public void onFinish(String response) {
                    synchronized (this){updateTimes++;}     //必须同步，不然会出现不确定的自增运算
//                    Log.e(TAG, "updateTimes = " + updateTimes);
                    if(updateTimes == citys.length){
//                        Log.e(TAG, "UPDATE_WEATHER_PAGE");
                        updateTimes = 0;
                        Intent intent = new Intent(MyApplication.UPDATE_WEATHER_PAGE);
//                        intent.putExtra("cityId", citys[preferencesUtil.MyRead("current_page", 0)]);
                        localBroadcastManager.sendBroadcast(intent);
                        Intent intent1 = new Intent(MyApplication.UPDATE_WIDGET);
                        sendBroadcast(intent1);
                    }
                }
                @Override
                public void onError(Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, getString(R.string.failed_to_load_data));
                }
            });
        }
    }

    //得到当前WeatherInfo
    private WeatherInfo getWeatherInfo(){
//        long t1 = SystemClock.currentThreadTimeMillis();
        int index = preferencesUtil.MyRead("current_page", 0);
        String city = preferencesUtil.MyRead("city_list", ",");
        if(city.length() < 6)
            return null;
        String[] citys = city.split(",");
        //        long t2  = SystemClock.currentThreadTimeMillis();
//        System.out.println("时间差1：" + (t2 - t1));
        return JsonUtil.parseJsonWithFastJson(FileOperateUtils.readFile(citys[index]));
    }
    //使用Notification
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void initNotification(){
        Intent intent = new Intent(this, SplashActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        remoteViews = new RemoteViews(getPackageName(), R.layout.notification);
        sunny_d = ImageDecodesUtil.decodeSampledBitmapFromResource(getResources(),
                R.drawable.clear_d,
                (int)(ScreenUtil.getScreenWidth_px(this) / ScreenUtil.getScreenDensity(this)),
                64);

        sunny_n = ImageDecodesUtil.decodeSampledBitmapFromResource(getResources(),
                R.drawable.clear_n,
                (int)(ScreenUtil.getScreenWidth_px(this) / ScreenUtil.getScreenDensity(this)),
                64);

        rain_d = ImageDecodesUtil.decodeSampledBitmapFromResource(getResources(),
                R.drawable.rain_d,
                (int)(ScreenUtil.getScreenWidth_px(this) / ScreenUtil.getScreenDensity(this)),
                64);

        rain_n = ImageDecodesUtil.decodeSampledBitmapFromResource(getResources(),
                R.drawable.rain_n,
                (int)(ScreenUtil.getScreenWidth_px(this) / ScreenUtil.getScreenDensity(this)),
                64);

        cloud_d = ImageDecodesUtil.decodeSampledBitmapFromResource(getResources(),
                R.drawable.cloudy_d,
                (int)(ScreenUtil.getScreenWidth_px(this) / ScreenUtil.getScreenDensity(this)),
                64);

        cloud_n = ImageDecodesUtil.decodeSampledBitmapFromResource(getResources(),
                R.drawable.cloudy_n,
                (int)(ScreenUtil.getScreenWidth_px(this) / ScreenUtil.getScreenDensity(this)),
                64);

        snow_d = ImageDecodesUtil.decodeSampledBitmapFromResource(getResources(),
                R.drawable.snow_d,
                (int)(ScreenUtil.getScreenWidth_px(this) / ScreenUtil.getScreenDensity(this)),
                64);

        snow_n = ImageDecodesUtil.decodeSampledBitmapFromResource(getResources(),
                R.drawable.snow_n,
                (int)(ScreenUtil.getScreenWidth_px(this) / ScreenUtil.getScreenDensity(this)),
                64);

        fog_d = ImageDecodesUtil.decodeSampledBitmapFromResource(getResources(),
                R.drawable.foggy_d,
                (int)(ScreenUtil.getScreenWidth_px(this) / ScreenUtil.getScreenDensity(this)),
                64);

        fog_n = ImageDecodesUtil.decodeSampledBitmapFromResource(getResources(),
                R.drawable.foggy_n,
                (int)(ScreenUtil.getScreenWidth_px(this) / ScreenUtil.getScreenDensity(this)),
                64);

        thunder_storm_d = ImageDecodesUtil.decodeSampledBitmapFromResource(getResources(),
                R.drawable.storm_d,
                (int)(ScreenUtil.getScreenWidth_px(this) / ScreenUtil.getScreenDensity(this)),
                64);

        thunder_storm_n = ImageDecodesUtil.decodeSampledBitmapFromResource(getResources(),
                R.drawable.storm_n,
                (int)(ScreenUtil.getScreenWidth_px(this) / ScreenUtil.getScreenDensity(this)),
                64);

        unknown_d = ImageDecodesUtil.decodeSampledBitmapFromResource(getResources(),
                R.drawable.bg_morning,
                (int)(ScreenUtil.getScreenWidth_px(this) / ScreenUtil.getScreenDensity(this)),
                64);

        unknown_n = ImageDecodesUtil.decodeSampledBitmapFromResource(getResources(),
                R.drawable.bg_evening,
                (int)(ScreenUtil.getScreenWidth_px(this) / ScreenUtil.getScreenDensity(this)),
                64);

        notification = new Notification.Builder(this)
//                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.sunny))
                .setSmallIcon(R.drawable.sunny)
                .setOngoing(true)
                .setAutoCancel(false)
                .setContent(remoteViews)
                .setContentIntent(pendingIntent)
                .build();
//        startForeground(1, notification);
    }
    private void showNotification(WeatherInfo weatherInfo) {
        if(!preferencesUtil.MyRead("show_notification", false)) {
            manager.cancel(1);
            return;
        }
        if(weatherInfo == null) {
            ToastUtil.showToast(this, R.string.no_weather, 2000);
            return;
        }
        //自定义布局
        remoteViews.setTextViewText(R.id.city_name, weatherInfo.getBasic().getCity());
        remoteViews.setTextViewText(R.id.city_weather, weatherInfo.getNow().getCond().getTxt());
        remoteViews.setTextViewText(R.id.degree, weatherInfo.getNow().getTmp() + "°");
        Time time=new Time();
        time.setToNow();
        remoteViews.setImageViewBitmap(R.id.background, getWeatherBitmap(weatherInfo));
        remoteViews.setImageViewResource(R.id.img_noti,
                WeatherCode.getWeatherCode(weatherInfo.getNow().getCond().getTxt()));
        //创建Notification对象
        manager.notify(1, notification);
    }
    //得到notification背景
    private Bitmap getWeatherBitmap(WeatherInfo weatherInfo) {
        Bitmap bitmap;
        DailyForecast dailyForecast = weatherInfo.getDaily_forecast().get(0);
        Date date = new Date();
        String[] sunrise = dailyForecast.getAstro().getSr().split(":");
        int sunRiseHour = Integer.parseInt(sunrise[0]);
        int sunRiseMinute = Integer.parseInt(sunrise[1]);
        String[] sunset = dailyForecast.getAstro().getSs().split(":");
        int sunSetHour = Integer.parseInt(sunset[0]);
        int sunSetMinute = Integer.parseInt(sunset[1]);
        if (((date.getHours() * 60 + date.getMinutes()) > (sunRiseHour * 60 + sunRiseMinute)) &&
                ((date.getHours() * 60 + date.getMinutes()) < (sunSetHour * 60 + sunSetMinute))) {
            switch (weatherInfo.getNow().getCond().getTxt()) {
                case "多云":
                case "少云":
                case "晴间多云":
                    bitmap = cloud_d;
                    break;
                case "阴":
                case "雾":
                case "浮尘":
                case "霾":
                    bitmap = fog_d;
                    break;
                case "晴":
                    bitmap = sunny_d;
                    break;
                case "小雨":
                case "阵雨":
                case "中雨":
                case "大雨":
                case "冻雨":
                    bitmap = rain_d;
                    break;
                case "雷阵雨伴有冰雹":
                case "雷阵雨":
                case "强雷阵雨":
                    bitmap = thunder_storm_d;
                    break;
                case "小雪":
                case "中雪":
                case "大雪":
                case "雨雪天气":
                case "雨夹雪":
                case "阵雪":
                    bitmap = snow_d;
                    break;
                default:
                    bitmap = unknown_d;
                    break;
            }
        }else {
            switch (weatherInfo.getNow().getCond().getTxt()) {
                case "多云":
                case "少云":
                case "晴间多云":
                    bitmap = cloud_n;
                    break;
                case "阴":
                case "雾":
                case "浮尘":
                case "霾":
                    bitmap = fog_n;
                    break;
                case "晴":
                    bitmap = sunny_n;
                    break;
                case "小雨":
                case "阵雨":
                case "中雨":
                case "大雨":
                case "冻雨":
                    bitmap = rain_n;
                    break;
                case "雷阵雨伴有冰雹":
                case "雷阵雨":
                case "强雷阵雨":
                    bitmap = thunder_storm_n;
                    break;
                case "小雪":
                case "中雪":
                case "大雪":
                case "雨雪天气":
                case "雨夹雪":
                case "阵雪":
                    bitmap = snow_n;
                    break;
                default:
                    bitmap = unknown_n;
                    break;
            }
        }
        return bitmap;
    }
    class UpdateNotification extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            showNotification(getWeatherInfo());
        }
    }
    //检查服务是否已经运行
    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service :
                manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.james_jiang.weatherreport.service.UpdateService"
                    .equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
