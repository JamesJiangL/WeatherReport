package com.james_jiang.weatherreport.ui.widgets;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
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
import com.james_jiang.weatherreport.Utils.SharedPreferencesUtil;
import com.james_jiang.weatherreport.service.WatchDogService;
import com.james_jiang.weatherreport.ui.splash.SplashActivity;
import com.james_jiang.weatherreport.ui.weatherInfo.WeatherCode;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.Thread.sleep;

/**
 * Created by JC on 2016/8/29.
 * 尺寸 N格 70 x N - 30
 */
public class WeatherWidgets extends AppWidgetProvider {
    private final static String TAG = "WeatherWidgets";

    private SharedPreferencesUtil preferencesUtil;
    private int currentPage;
    private WeatherInfo weatherInfo;
    private final static int REFRESH_WIDGET = 1000;
    private final static int REFRESH_IMG = 1001;
    private Context context;
    private LocalBroadcastManager localBroadcastManager;
    private boolean isUpdate;
    private int[] refreshImg = {R.drawable.refresh_27x27_1, R.drawable.refresh_27x27_2,
            R.drawable.refresh_27x27_3, R.drawable.refresh_27x27_4, R.drawable.refresh_27x27_5,
            R.drawable.refresh_27x27_6, R.drawable.refresh_27x27_7, R.drawable.refresh_27x27_8,
            R.drawable.refresh_27x27_9, R.drawable.refresh_27x27_10, R.drawable.refresh_27x27_11,
            R.drawable.refresh_27x27_12, R.drawable.refresh_27x27_13, R.drawable.refresh_27x27_14,
            R.drawable.refresh_27x27_15, R.drawable.refresh_27x27_16, R.drawable.refresh_27x27_17,
            R.drawable.refresh_27x27_18,};
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        localBroadcastManager = LocalBroadcastManager.getInstance(context);
//        Log.e(TAG, "onUpdate, context = " + context.toString());
        //定时更新时间
        timeOpening(context);
        //获取天气和位置
        getWeatherInfo(context);
        initWidget(context, appWidgetManager, appWidgetIds, weatherInfo);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        Log.e(TAG, "onDeleted, length = " + appWidgetIds.length);
        for(int i = 0; i < appWidgetIds.length; i++){
            Log.e(TAG, "appWidgetIds = " + appWidgetIds[i]);
        }
    }

    @Override
    public void onEnabled(Context context) {        //仅在第一次使用widget时调用
        preferencesUtil = SharedPreferencesUtil.getInstance(context);
        super.onEnabled(context);
//        Log.e(TAG, "onEnabled, context = " + context.toString());
    }

    @Override
    public void onDisabled(Context context) {       //仅在删除所有widget之后调用
        super.onDisabled(context);
//        Log.e(TAG, "onDisabled");
        if(localBroadcastManager != null);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        this.context = context;
//        Log.e(TAG, "onReceive.intent = " + intent.getAction());
        if(preferencesUtil == null){
//            Log.e(TAG, "preferencesUtil == null");
            preferencesUtil = SharedPreferencesUtil.getInstance(context);
        }
        if(localBroadcastManager == null){
            localBroadcastManager = LocalBroadcastManager.getInstance(context);
        }
        String city = preferencesUtil.MyRead("city_list", ",");
        if(city.length() < 6) {
            deleteAll();
            return;
        }
        String[] citys = city.split(",");
        if(citys.length == 0) {
            deleteAll();
            return;
        }
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.app_widget);
        switch (intent.getAction()){
            case MyApplication.UPDATE_WIDGET_TIME:
                timeOpening(context);
                Date date = new Date(System.currentTimeMillis());
                DateFormat format = new SimpleDateFormat("HH:mm");
                remoteViews.setTextViewText(R.id.current_time, format.format(date));
                break;
            case MyApplication.WIDGET_LEFT:
//                Log.e(TAG, "WIDGET_LEFT");
                currentPage = preferencesUtil.MyRead("current_page", 0);
                if(citys.length == 1){
                    return;
                }
                if(currentPage == 0){
                    currentPage = citys.length - 1;
                }else {
                    currentPage = currentPage - 1;
                }
                preferencesUtil.MySave("current_page", currentPage);
                weatherInfo = JsonUtil.parseJsonWithFastJson(
                        FileOperateUtils.readFile(citys[currentPage]));
                updateWidget(weatherInfo, remoteViews);
                updateNotification();
                break;
            case MyApplication.WIDGET_RIGHT:
//                Log.e(TAG, "WIDGET_RIGHT");
                currentPage = preferencesUtil.MyRead("current_page", 0);
                if(citys.length == 1){
                    return;
                }
                if(currentPage == citys.length - 1){
                    currentPage = 0;
                }else {
                    currentPage = currentPage + 1;
                }
                preferencesUtil.MySave("current_page", currentPage);
                weatherInfo = JsonUtil.parseJsonWithFastJson(
                        FileOperateUtils.readFile(citys[currentPage]));
                updateWidget(weatherInfo, remoteViews);
                updateNotification();
                break;
            case MyApplication.UPDATE_WIDGET:
            case Intent.ACTION_BOOT_COMPLETED:
//                Log.e(TAG, "UPDATE_WIDGET");
                currentPage = preferencesUtil.MyRead("current_page", 0);
                weatherInfo = JsonUtil.parseJsonWithFastJson(
                        FileOperateUtils.readFile(citys[currentPage]));
                updateWidget(weatherInfo, remoteViews);
                break;
            case MyApplication.REFRESH_WIDGET:
//                Log.e(TAG, "REFRESH_WIDGET");
                isUpdate = false;
                refreshWidgetImage();
                currentPage = preferencesUtil.MyRead("current_page", 0);
                String cityId = citys[currentPage];
                HttpUtil.weatherRequest(cityId, new HttpCallBackListener() {
                    @Override
                    public void onFinish(String response) {
//                        Log.e(TAG, "response");
                        weatherInfo = JsonUtil.parseJsonWithFastJson(response);
                        isUpdate = true;
                        refreshWidgetData(weatherInfo);
                    }
                    @Override
                    public void onError(Exception e) {
                        isUpdate = true;
                        e.printStackTrace();
                    }
                });
                break;
        }
        // 获得appwidget管理实例，用于管理appwidget以便进行更新操作
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        // 相当于获得所有本程序创建的appwidget
        ComponentName componentName = new ComponentName(context,WeatherWidgets.class);
        // 更新appwidget
        appWidgetManager.updateAppWidget(componentName, remoteViews);
    }
    private int getWeatherImg(WeatherInfo weatherInfo){
        int imgResource;
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
            Log.e(TAG, weatherInfo.getNow().getCond().getTxt());
            switch (weatherInfo.getNow().getCond().getTxt()) {
                case "多云":
                case "少云":
                case "晴间多云":
                    imgResource = R.drawable.cloudy_d;
                    break;
                case "阴":
                case "雾":
                case "浮尘":
                case "霾":
                    imgResource = R.drawable.foggy_d;
                    break;
                case "晴":
                    imgResource = R.drawable.clear_d;
                    break;
                case "小雨":
                case "阵雨":
                case "中雨":
                case "大雨":
                case "冻雨":
                    imgResource = R.drawable.rain_d;
                    break;
                case "雷阵雨伴有冰雹":
                case "雷阵雨":
                case "强雷阵雨":
                    imgResource = R.drawable.storm_d;
                    break;
                case "小雪":
                case "中雪":
                case "大雪":
                case "雨雪天气":
                case "雨夹雪":
                case "阵雪":
                    imgResource = R.drawable.snow_d;
                    break;
                default:
                    imgResource = R.drawable.bg_morning;
                    break;
            }
        }else {
            switch (weatherInfo.getNow().getCond().getTxt()) {
                case "多云":
                case "少云":
                case "晴间多云":
                    imgResource = R.drawable.cloudy_n;
                    break;
                case "阴":
                case "雾":
                case "浮尘":
                case "霾":
                    imgResource = R.drawable.foggy_n;
                    break;
                case "晴":
                    imgResource = R.drawable.clear_n;
                    break;
                case "小雨":
                case "阵雨":
                case "中雨":
                case "大雨":
                case "冻雨":
                    imgResource = R.drawable.rain_n;
                    break;
                case "雷阵雨伴有冰雹":
                case "雷阵雨":
                case "强雷阵雨":
                    imgResource = R.drawable.storm_n;
                    break;
                case "小雪":
                case "中雪":
                case "大雪":
                case "雨雪天气":
                case "雨夹雪":
                case "阵雪":
                    imgResource = R.drawable.snow_n;
                    break;
                default:
                    imgResource = R.drawable.bg_evening;
                    break;
            }
        }
        return imgResource;
    }
    //获取当前页面和天气
    private void getWeatherInfo(Context context){

        if(preferencesUtil == null){
//            Log.e(TAG, "preferencesUtil == null");
            preferencesUtil = SharedPreferencesUtil.getInstance(context);
        }
        currentPage = preferencesUtil.MyRead("current_page", 0);
        String city = preferencesUtil.MyRead("city_list", ",");
        if(city.length() < 6)
            return;
        String[] citys = city.split(",");
        String cityId = citys[currentPage];
        Log.e(TAG, "cityId = " + cityId);
        weatherInfo = JsonUtil.parseJsonWithFastJson(FileOperateUtils.readFile(cityId));
    }
    //初始化widget
    private void initWidget(Context context, AppWidgetManager appWidgetManager,
                            int[] appWidgetIds, WeatherInfo weatherInfo){
        //widget布局
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.app_widget);
        setLeftPending(context, remoteViews);
        setRightPending(context, remoteViews);
        setRefreshPending(context, remoteViews);
        setMainPending(context, remoteViews);
        //背景
        if(weatherInfo == null) {
//            Log.e(TAG, "weatherInfo == null");
            remoteViews.setImageViewResource(R.id.widget_img, R.drawable.bg_morning);
            appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
            return;
        }
        remoteViews.setImageViewResource(R.id.widget_img, getWeatherImg(weatherInfo));
        //城市名
        remoteViews.setTextViewText(R.id.city_name, weatherInfo.getBasic().getCity());
        if(weatherInfo.getBasic().getId().equals(preferencesUtil.MyRead("location", "")))
            remoteViews.setImageViewResource(R.id.location, R.drawable.current_location);
        else
            remoteViews.setImageViewResource(R.id.location, 0);
        //时间
        Date date = new Date(System.currentTimeMillis());
        DateFormat format = new SimpleDateFormat("HH:mm");
        remoteViews.setTextViewText(R.id.current_time, format.format(date));
        //天气
        remoteViews.setImageViewResource(R.id.current_weather_img,
                WeatherCode.getWeatherCode(weatherInfo.getNow().getCond().getTxt()));
        remoteViews.setTextViewText(R.id.current_weather_text,
                weatherInfo.getNow().getCond().getTxt());
        //温度
        remoteViews.setTextViewText(R.id.current_tmp,
                weatherInfo.getNow().getTmp() + "°");
        remoteViews.setTextViewText(R.id.today_max_tmp,
                String.valueOf(weatherInfo.getDaily_forecast().get(0).getTmp().getMax()));
        remoteViews.setTextViewText(R.id.today_min_tmp,
                String.valueOf(weatherInfo.getDaily_forecast().get(0).getTmp().getMin()));
        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
    }
    //设置响应
    private void setLeftPending(Context context, RemoteViews remoteViews){
        Intent leftIntent = new Intent(context, WatchDogService.class);
        leftIntent.putExtra("widget_click_type", MyApplication.WIDGET_LEFT);
        PendingIntent leftPendingIntent = PendingIntent.getService(context, 3,
                leftIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.left_widget, leftPendingIntent);
    }
    private void setRightPending(Context context, RemoteViews remoteViews){
        Intent rightIntent = new Intent(context, WatchDogService.class);
        rightIntent.putExtra("widget_click_type", MyApplication.WIDGET_RIGHT);
        PendingIntent rightPendingIntent = PendingIntent.getService(context, 2,
                rightIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.right_widget, rightPendingIntent);
    }
    private void setRefreshPending(Context context, RemoteViews remoteViews){
        Intent refreshIntent = new Intent(context, WatchDogService.class);
        refreshIntent.putExtra("widget_click_type",MyApplication.REFRESH_WIDGET);
        PendingIntent refreshPendingIntent = PendingIntent.getService(context, 1,
                refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.refresh_widget, refreshPendingIntent);
    }
    private void setMainPending(Context context, RemoteViews remoteViews){
        //设置响应
        Intent mainIntent = new Intent(context, SplashActivity.class);
        PendingIntent mainPendingIntent = PendingIntent.getActivity(context, 0,     //需要区分
                mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.widget_img, mainPendingIntent);
    }
    //更新时间的定时器
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void timeOpening(Context context){
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        int time = 20 * 1000;           //为了同步时间，20s更新一次
        long triggerAtTime = SystemClock.elapsedRealtime() + time;
        Intent intent = new Intent(MyApplication.UPDATE_WIDGET_TIME);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        manager.setExact(AlarmManager.ELAPSED_REALTIME, triggerAtTime, pendingIntent);
    }
    //向左向右更新数据
    private void updateWidget(WeatherInfo weatherInfo, RemoteViews remoteViews){
        //背景
        if(weatherInfo == null) {
//            Log.e(TAG, "weatherInfo == null");
            remoteViews.setImageViewResource(R.id.widget_img, R.drawable.bg_morning);
            return;
        }
        remoteViews.setImageViewResource(R.id.widget_img, getWeatherImg(weatherInfo));
        //城市名
        remoteViews.setTextViewText(R.id.city_name, weatherInfo.getBasic().getCity());
        if(weatherInfo.getBasic().getId().equals(preferencesUtil.MyRead("location", "")))
            remoteViews.setImageViewResource(R.id.location, R.drawable.current_location);
        else
            remoteViews.setImageViewResource(R.id.location, 0);
        //时间
        Date date = new Date(System.currentTimeMillis());
        DateFormat format = new SimpleDateFormat("HH:mm");
        remoteViews.setTextViewText(R.id.current_time, format.format(date));
        //天气
        remoteViews.setImageViewResource(R.id.current_weather_img,
                WeatherCode.getWeatherCode(weatherInfo.getNow().getCond().getTxt()));
        remoteViews.setTextViewText(R.id.current_weather_text,
                weatherInfo.getNow().getCond().getTxt());
        //温度
        remoteViews.setTextViewText(R.id.current_tmp,
                weatherInfo.getNow().getTmp() + "°");
        remoteViews.setTextViewText(R.id.today_max_tmp,
                String.valueOf(weatherInfo.getDaily_forecast().get(0).getTmp().getMax()));
        remoteViews.setTextViewText(R.id.today_min_tmp,
                String.valueOf(weatherInfo.getDaily_forecast().get(0).getTmp().getMin()));
    }
    //更新Notification
    private void updateNotification(){
        Intent intent = new Intent(MyApplication.UPDATE_NOTIFICATION);
        localBroadcastManager.sendBroadcast(intent);
    }
    //刷新widget
    private void refreshWidget(WeatherInfo weatherInfo){
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.app_widget);
        //背景
        if(weatherInfo == null) {
            Log.e(TAG, "weatherInfo == null");
            remoteViews.setImageViewResource(R.id.widget_img, R.drawable.bg_morning);
            return;
        }
        remoteViews.setImageViewResource(R.id.widget_img, getWeatherImg(weatherInfo));
        //城市名
        remoteViews.setTextViewText(R.id.city_name, weatherInfo.getBasic().getCity());
        if(weatherInfo.getBasic().getId().equals(preferencesUtil.MyRead("location", "")))
            remoteViews.setImageViewResource(R.id.location, R.drawable.current_location);
        else
            remoteViews.setImageViewResource(R.id.location, 0);
        //时间
        Date date = new Date(System.currentTimeMillis());
        DateFormat format = new SimpleDateFormat("HH:mm");
        remoteViews.setTextViewText(R.id.current_time, format.format(date));
        //天气
        remoteViews.setImageViewResource(R.id.current_weather_img,
                WeatherCode.getWeatherCode(weatherInfo.getNow().getCond().getTxt()));
        remoteViews.setTextViewText(R.id.current_weather_text,
                weatherInfo.getNow().getCond().getTxt());
        //温度
        remoteViews.setTextViewText(R.id.current_tmp,
                weatherInfo.getNow().getTmp() + "°");
        remoteViews.setTextViewText(R.id.today_max_tmp,
                String.valueOf(weatherInfo.getDaily_forecast().get(0).getTmp().getMax()));
        remoteViews.setTextViewText(R.id.today_min_tmp,
                String.valueOf(weatherInfo.getDaily_forecast().get(0).getTmp().getMin()));

        remoteViews.setImageViewResource(R.id.refresh_widget, R.drawable.refresh_widget);

        // 获得appwidget管理实例，用于管理appwidget以便进行更新操作
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        // 相当于获得所有本程序创建的appwidget
        ComponentName componentName = new ComponentName(context,WeatherWidgets.class);
        // 更新appwidget
        appWidgetManager.updateAppWidget(componentName, remoteViews);
    }
    //清空之后的状态
    private void deleteAll(){
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.app_widget);
        remoteViews.setImageViewResource(R.id.widget_img, R.drawable.bg_morning);
        remoteViews.setTextViewText(R.id.city_name, "");
        remoteViews.setImageViewResource(R.id.location, 0);
        remoteViews.setTextViewText(R.id.current_time, "");
        remoteViews.setImageViewResource(R.id.left_widget, 0);
        remoteViews.setImageViewResource(R.id.right_widget, 0);
        remoteViews.setImageViewResource(R.id.current_weather_img, 0);
        remoteViews.setTextViewText(R.id.current_weather_text, "");
        remoteViews.setTextViewText(R.id.current_tmp, "");
        remoteViews.setTextViewText(R.id.today_max_tmp, "");
        remoteViews.setTextViewText(R.id.today_min_tmp, "");
        remoteViews.setImageViewResource(R.id.refresh_widget, 0);
        // 获得appwidget管理实例，用于管理appwidget以便进行更新操作
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        // 相当于获得所有本程序创建的appwidget
        ComponentName componentName = new ComponentName(context,WeatherWidgets.class);
        // 更新appwidget
        appWidgetManager.updateAppWidget(componentName, remoteViews);
    }
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH_WIDGET:
                    WeatherInfo weatherInfo = (WeatherInfo) msg.obj;
                    refreshWidget(weatherInfo);
                    break;
                case REFRESH_IMG:
                    int index = 0;
                    while (!isUpdate){
                        refreshImg(refreshImg[index]);
                        index++;
                        if(index >= 18){
                            index = 0;
                        }
                    }
                    try {
                        sleep(150);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };
    //更新刷新图片
    private void refreshImg(int resourceId){
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.app_widget);
        remoteViews.setImageViewResource(R.id.refresh_widget, resourceId);
        // 获得appwidget管理实例，用于管理appwidget以便进行更新操作
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        // 相当于获得所有本程序创建的appwidget
        ComponentName componentName = new ComponentName(context,WeatherWidgets.class);
        // 更新appwidget
        appWidgetManager.updateAppWidget(componentName, remoteViews);
    }
    private void refreshWidgetData(WeatherInfo weatherInfo){
        Message message = new Message();
        message.what = REFRESH_WIDGET;
        message.obj = weatherInfo;
        handler.sendMessage(message);
    }
    private void refreshWidgetImage(){
        Message message = new Message();
        message.what = REFRESH_IMG;
        handler.sendMessage(message);
    }
}
