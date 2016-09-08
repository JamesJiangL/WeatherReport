package com.james_jiang.weatherreport.ui.weatherInfo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.james_jiang.weatherreport.AreasWeatherInfo.WeatherInfo.AQI;
import com.james_jiang.weatherreport.AreasWeatherInfo.WeatherInfo.DailyForecast;
import com.james_jiang.weatherreport.AreasWeatherInfo.WeatherInfo.WeatherInfo;
import com.james_jiang.weatherreport.Common.MyApplication;
import com.james_jiang.weatherreport.R;
import com.james_jiang.weatherreport.Utils.DataUtil;
import com.james_jiang.weatherreport.Utils.File.FileOperateUtils;
import com.james_jiang.weatherreport.Utils.Http.HttpCallBackListener;
import com.james_jiang.weatherreport.Utils.Http.HttpUtil;
import com.james_jiang.weatherreport.Utils.Http.JsonUtil;
import com.james_jiang.weatherreport.Utils.ScreenUtil;
import com.james_jiang.weatherreport.Utils.SharedPreferencesUtil;
import com.james_jiang.weatherreport.Utils.ToastUtil;
import com.james_jiang.weatherreport.ui.refresh_top.PullToRefreshListener;
import com.james_jiang.weatherreport.ui.refresh_top.RefreshLayout;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by JC on 2016/8/12.
 */
public class WeatherInfoView implements
        View.OnClickListener, PullToRefreshListener, ScrollViewListener{
    private final static String TAG = "WeatherInfoView";
    private final static int UPDATE_SUCCESS = 100;
    private final static int UPDATE_FAILURE = 101;
    private Context context;
    private LayoutInflater layoutInflater;
//    private GestureDetector gestureDetector;
    private String cityId;
    private WeatherInfo weatherInfo;
    private View weatherInfoView;
    private WeatherScrollView weatherScrollView;

    private ImageView add;
    private ImageView more;
    private ImageView use_location;
    private ImageView location;
    private TextView cityName;
    private TextView todayDate;
    private ImageView weatherImg;
    private TextView currentTmp;
    private TextView todayMaxTmp;
    private TextView todayMinTmp;
    private TextView weatherString;
    //风车
    private ImageView bigBlade;
    private ImageView smallBlade;
    private TextView windSuggestion;
    private int windClick;
    private SensorManager sensorManager;
    private RelativeLayout bladeLayout;
    //日出日落
    private ImageView sunRiseSetLine;
    private TextView sunSuggestion;

    private LocalBroadcastManager localBroadcastManager;
    private UpdateReceiver updateReceiver;
    private SharedPreferencesUtil preferencesUtil;
    //下拉刷新
    private RefreshLayout refreshLayout;
    private ConnectivityManager connectivityManager;
    private int updateTimes;
    public WeatherInfoView(Context context, String cityId){
        this.context = context;
        this.cityId = cityId;
        initView();
    }
    private void initView(){
//        Log.e(TAG, "initView: " + cityId);
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        weatherInfoView = layoutInflater.inflate(R.layout.weather_info, null);
        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        preferencesUtil = SharedPreferencesUtil.getInstance(context);
//        gestureDetector = new GestureDetector(this);
        weatherInfo = JsonUtil.parseJsonWithFastJson(FileOperateUtils.readFile(cityId));
        updateTimes = 0;
        initShowControls();
        initUpdateReceiver();
        loadWeatherInfo();
        initFutureWeatherList();
        initDetailWeather();
    }
    //初始化显示控件
    private void initShowControls(){
        refreshLayout = (RefreshLayout) weatherInfoView.findViewById(R.id.refresh_header);
        weatherScrollView = (WeatherScrollView)
                weatherInfoView.findViewById(R.id.weather_scrollView);
        add = (ImageView) weatherInfoView.findViewById(R.id.add);
        more = (ImageView) weatherInfoView.findViewById(R.id.more);
        use_location = (ImageView) weatherInfoView.findViewById(R.id.use_location);
        if(preferencesUtil.MyRead("use_location", false)){
            use_location.setVisibility(View.VISIBLE);
            use_location.setOnClickListener(this);
        }else {
            use_location.setVisibility(View.GONE);
        }

        location = (ImageView) weatherInfoView.findViewById(R.id.location);
        cityName = (TextView) weatherInfoView.findViewById(R.id.city_Name);
        todayDate = (TextView) weatherInfoView.findViewById(R.id.today_date);
        weatherImg = (ImageView) weatherInfoView.findViewById(R.id.today_weather_pic);
        currentTmp = (TextView) weatherInfoView.findViewById(R.id.current_tmp);
        todayMaxTmp = (TextView) weatherInfoView.findViewById(R.id.today_max_tmp);
        todayMinTmp = (TextView) weatherInfoView.findViewById(R.id.today_min_tmp);
        weatherString = (TextView) weatherInfoView.findViewById(R.id.today_weather_string);
        //风车
        bigBlade  = (ImageView) weatherInfoView.findViewById(R.id.big_blade);
        smallBlade = (ImageView) weatherInfoView.findViewById(R.id.small_blade);
        windSuggestion = (TextView) weatherInfoView.findViewById(R.id.wind_suggestion);
        bladeLayout = (RelativeLayout) weatherInfoView.findViewById(R.id.windmill_layout);
        windClick = 0;
        //日出日落
        sunRiseSetLine = (ImageView) weatherInfoView.findViewById(R.id.sun_set_rise_line);
        sunSuggestion = (TextView) weatherInfoView.findViewById(R.id.sun_suggestion);
        setShowControlsListener();
    }
    private void setShowControlsListener(){
        //设置下拉刷新监听器
        refreshLayout.setOnRefreshListener(this, 0);
        weatherScrollView.setScrollViewListener(this);
//        weatherScrollView.setOnTouchListener(this);
        add.setOnClickListener(this);
        more.setOnClickListener(this);
        if(preferencesUtil.MyRead("use_location", false)){
            use_location.setOnClickListener(this);
        }else {
            use_location.setVisibility(View.GONE);
        }
        //风车
        bigBlade.setOnClickListener(this);
        smallBlade.setOnClickListener(this);
        windSuggestion.setOnClickListener(this);
        bladeLayout.setOnClickListener(this);
        //日出日落
        sunRiseSetLine.setOnClickListener(this);
        sunSuggestion.setOnClickListener(this);
    }
    //初始化广播
    private void initUpdateReceiver(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(MyApplication.UPDATE_ANIMATION);
        filter.addAction(MyApplication.UPDATE_WEATHER_PAGE);
        filter.addAction(MyApplication.UNREGISTER_RECEIVER);
        filter.addAction(MyApplication.USE_LOCATION);
        updateReceiver = new UpdateReceiver();
        localBroadcastManager.registerReceiver(updateReceiver, filter);
    }
    //从 WeatherInfo 对象中加载天气信息
    private void loadWeatherInfo(){
        if(cityId.equals(preferencesUtil.MyRead("location", "")))
            location.setImageResource(R.drawable.current_location);
        cityName.setText(weatherInfo.getBasic().getCity());
        weatherString.setText(weatherInfo.getNow().getCond().getTxt());
        setDate();
        currentTmp.setText(weatherInfo.getNow().getTmp() + "°");
        todayMaxTmp.setText(String.valueOf(weatherInfo.getDaily_forecast().get(0).getTmp().getMax()));
        todayMinTmp.setText(String.valueOf(weatherInfo.getDaily_forecast().get(0).getTmp().getMin()));
        weatherImg.setImageResource(WeatherCode.getWeatherCode(weatherInfo.getNow().getCond().getTxt()));
    }
    //设置时间      时间是必须设置的
    private void setDate(){
        Date date = new Date();
        todayDate.setText(DataUtil.getWeekDate(date));
    }

    //*********************************************未来天气**************************************//
    //初始化listView
    private void initFutureWeatherList(){
        final RelativeLayout relativeLayout = (RelativeLayout)
                weatherInfoView.findViewById(R.id.future_weather_layout);
        ScrollListView listView = (ScrollListView) weatherInfoView.findViewById(R.id.future_weather);
        List<DailyForecast> dailyForecasts = weatherInfo.getDaily_forecast();
        FutureWeatherAdapter adapter = new FutureWeatherAdapter(context,
                R.layout.future_weather_list_item, dailyForecasts);
        listView.setAdapter(adapter);
//        setListViewHeightBasedOnChildren(listView);
        //移动控件至屏幕下方
        final TextView linearLayout = (TextView)
                weatherInfoView.findViewById(R.id.today_weather_string);
        ViewTreeObserver observer = linearLayout.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                linearLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                int bottom = linearLayout.getBottom();
                //计算出高度之后再移动
                LinearLayout.LayoutParams params =
                        (LinearLayout.LayoutParams) relativeLayout.getLayoutParams();
                int top;
//                if(ScreenUtil.isFlyme()){         //去掉了smartBar
//                    top = ScreenUtil.getScreenHeight_px(context) - bottom - 120;     //smartBar高度
//                }else{
                    top = ScreenUtil.getScreenHeight_px(context) - bottom
                                     - ScreenUtil.getStatusHeight(context);       //在界面跳转的时候，没有状态栏
//                }
                params.setMargins(0, top, 0, 0);
                relativeLayout.setLayoutParams(params);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        relativeLayout.setVisibility(View.VISIBLE);
                    }
                }, 10);
            }
        });
    }
    //重绘控件高度
    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }
    //*********************************************未来天气**************************************//

    //*********************************************天气详情**************************************//
    private void initDetailWeather(){
        //设置ListView
        List<DetailWeather> detailWeathers = new ArrayList<>();
        DetailWeather fl = new DetailWeather(context.getString(R.string.fl),
                weatherInfo.getNow().getFl() + "°");
        detailWeathers.add(fl);
        DetailWeather hum = new DetailWeather(context.getString(R.string.hum),
                weatherInfo.getNow().getHum() + "%");
        detailWeathers.add(hum);
        DetailWeather vis = new DetailWeather(context.getString(R.string.vis),
                weatherInfo.getNow().getVis() + "km");
        detailWeathers.add(vis);
        AQI aq = weatherInfo.getAqi();
        DetailWeather aqi;
        if(aq == null){
            aqi = new DetailWeather(context.getString(R.string.aqi), "0");
        }else {
            aqi = new DetailWeather(context.getString(R.string.aqi),
                    String.valueOf(aq.getCity().getAqi()));
        }
        detailWeathers.add(aqi);
        DetailWeather uv = new DetailWeather(context.getString(R.string.uv),
                weatherInfo.getSuggestion().getUv().getBrf());
        detailWeathers.add(uv);

        ScrollListView listView = (ScrollListView) weatherInfoView.findViewById(R.id.detail_weather);
        DetailWeatherAdapter adapter = new DetailWeatherAdapter(context,
                R.layout.future_weather_list_item, detailWeathers);
        listView.setAdapter(adapter);

        //设置前面的照片
        ImageView imageView = (ImageView) weatherInfoView.findViewById(R.id.detail_weather_img);
        imageView.setImageResource(
                WeatherCode.getWeatherCode(weatherInfo.getNow().getCond().getTxt()));
    }
    //*********************************************天气详情**************************************//

    //*********************************************风速气压**************************************//
    private void initWindAndPressure(){
        if(windClick == 0) {
            windSuggestion.setText("");
        }
        //风速值
        TextView windSpeed = (TextView) weatherInfoView.findViewById(R.id.wind_speed_value);
        int speed = weatherInfo.getNow().getWind().getSpd();
        windSpeed.setText(speed + "km/h");
        //风向
        TextView windDirection = (TextView) weatherInfoView.findViewById(R.id.wind_direction);
        windDirection.setText(weatherInfo.getNow().getWind().getDir());
        //风车
        Animation bigBladeAnim = AnimationUtils.loadAnimation(context, R.anim.windmill);
        bigBladeAnim.setDuration(24000 / speed);              //最小80     time = 24000 / speed
        bigBlade.startAnimation(bigBladeAnim);
        Animation smallBladeAnim = AnimationUtils.loadAnimation(context, R.anim.windmill);
        smallBladeAnim.setDuration(24000 / speed);
        smallBlade.startAnimation(smallBladeAnim);
        //气压线
        TextView pressureValue = (TextView) weatherInfoView.findViewById(R.id.pressure_value);
        pressureValue.setText(String.valueOf(weatherInfo.getNow().getPres()));
        RelativeLayout pressure = (RelativeLayout) weatherInfoView.findViewById(R.id.pressure_line_layout);
        Animation pressureAnim = AnimationUtils.loadAnimation(context, R.anim.pressure);
        pressure.startAnimation(pressureAnim);
    }
    private void setWindSuggestion(int count){
        switch (count){
            case 1:
                windSuggestion.setText(R.string.wind_suggestion_1);
                break;
            case 2:
                windSuggestion.setText(R.string.wind_suggestion_2);
                break;
            case 3:
                windSuggestion.setText(R.string.wind_suggestion_3);
                break;
        }
    }
    private SensorEventListener listener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float xValue = Math.abs(event.values[0]);
            float yValue = Math.abs(event.values[1]);
            float zValue = Math.abs(event.values[2]);
            if(windClick >= 3 && (xValue > 15.0f || yValue > 15.0f || zValue > 15.0f)){
                Log.e(TAG, "listener: " + windClick);
                windClick = 0;
                bigBlade.clearAnimation();
                bigBlade.setVisibility(View.GONE);
                smallBlade.clearAnimation();
                smallBlade.setVisibility(View.GONE);
                windSuggestion.setText(R.string.wind_suggestion_4);
                Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(new long[] {500, 200}, -1);
            }
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };
    //*********************************************风速气压**************************************//

    //*********************************************日出日落**************************************//
    private void initSunSetRise(){
//        Log.e(TAG, "initSunSetRise");
        //显示日出日落时间
        String sunrise = weatherInfo.getDaily_forecast().get(0).getAstro().getSr();
        String sunset = weatherInfo.getDaily_forecast().get(0).getAstro().getSs();
        TextView sunRise = (TextView) weatherInfoView.findViewById(R.id.sun_rise_time);
        sunRise.setText(sunrise);
        TextView sunSet = (TextView) weatherInfoView.findViewById(R.id.sun_set_time);
        sunSet.setText(sunset);
        //日出日落动画
        float angle;
        int duration;
        Date date = new Date();
        String[] rise = sunrise.split(":");
        String[] set = sunset.split(":");
        int currentHour = date.getHours();
        int currentMinute = date.getMinutes();
        int riseHour = Integer.parseInt(rise[0]);
        int riseMinute = Integer.parseInt(rise[1]);
        int setHour = Integer.parseInt(set[0]);
        int setMinute = Integer.parseInt(set[1]);
        int totalTime = (setHour * 60 + setMinute) - (riseHour * 60 + riseMinute);
        if(currentHour < riseHour) {
            angle = 0.0f;
            duration = 0;
        } else if(currentHour > setHour) {
            angle = 180.0f;
            duration = 9000;
        } else {
            int elapsedTime = (currentHour * 60 + currentMinute) - (riseHour * 60 + riseMinute);
            angle = (elapsedTime * 180.0f) / totalTime;
            duration = (int) angle * 50;
        }
        ImageView sun = (ImageView) weatherInfoView.findViewById(R.id.small_sun);
        RotateAnimation animation = new RotateAnimation(0, angle,
                (float) (8.4 + 80) * ScreenUtil.getScreenDensity(context),
                (float) 8.4 * ScreenUtil.getScreenDensity(context));    //适配MX3
        animation.setFillAfter(true);
        animation.setDuration(duration);            //1度 对应 50ms
        sun.startAnimation(animation);
    }
    private void sunSuggestion(Date date){
        if(date.getHours() >= 23 || date.getHours() <= 6){
            sunSuggestion.setText(R.string.sun_suggestion_1);
            sunSuggestion.setTextColor(context.getResources().getColor(R.color.pink));
        }else if(date.getHours() >= 12 && date.getHours() <= 13){
            sunSuggestion.setText(R.string.sun_suggestion_2);
            sunSuggestion.setTextColor(context.getResources().getColor(R.color.light_blue));
        }else if(date.getHours() >= 18 && date.getHours() <= 22){
            sunSuggestion.setText(R.string.sun_suggestion_3);
            sunSuggestion.setTextColor(context.getResources().getColor(R.color.light_blue));
        }else {
            sunSuggestion.setText(R.string.sun_suggestion_4);
            sunSuggestion.setTextColor(context.getResources().getColor(R.color.sun_rise));
        }
    }
    //*********************************************日出日落**************************************//

        @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add:
                Intent addIntent = new Intent(MyApplication.ADD_BUTTON);
                localBroadcastManager.sendBroadcast(addIntent);
                break;
            case R.id.more:
                Intent moreIntent = new Intent(MyApplication.MORE_BUTTON);
                localBroadcastManager.sendBroadcast(moreIntent);
                break;
            case R.id.use_location:
                Intent locationIntent = new Intent(MyApplication.LOCATION);
                localBroadcastManager.sendBroadcast(locationIntent);
                break;
            case R.id.sun_set_rise_line:
                sunSuggestion(new Date());
                break;
            case R.id.sun_suggestion:
                sunSuggestion.setText("");
                break;
            case R.id.big_blade:
            case R.id.small_blade:
            case R.id.windmill_layout:
                if(bigBlade.getVisibility() == View.VISIBLE) {
                    windClick++;
                    setWindSuggestion(windClick);
                }else {
                    bigBlade.setVisibility(View.VISIBLE);
                    initWindAndPressure();
                }
                break;
            case R.id.wind_suggestion:
                windSuggestion.setText("");
                break;
        }
    }


    /*              */
    @Override
    public void onScrollChanged(WeatherScrollView scrollView, int x, int y, int oldX, int oldY) {
//        Log.e(TAG, "x = " + x + ", y = " + y + ", oldX = " + oldX + ", oldY = " + oldY);
    }

    //*********************************************下拉刷新**************************************//
    @Override
    public void onRefresh() {
        if(!isNetWorkAvailable()){
            Message message = new Message();
            message.what = UPDATE_FAILURE;
            handler.sendMessage(message);
            return;
        }
        String city = preferencesUtil.MyRead("city_list",",");
        if(city.length() < 6)
            return;
        final String[] citys = city.split(",");
        for (String city1 : citys) {
            HttpUtil.weatherRequest(city1, new HttpCallBackListener() {
                @Override
                public void onFinish(String response) {
                    synchronized (this){updateTimes++;}
                    if(updateTimes == citys.length) {
                        updateTimes = 0;
                        Message message = new Message();
                        message.what = UPDATE_SUCCESS;
                        handler.sendMessage(message);
                    }
                }

                @Override
                public void onError(Exception e) {
                    Log.e(TAG, "onError");
                    e.printStackTrace();
                    Message message = new Message();
                    message.what = UPDATE_FAILURE;
                    handler.sendMessage(message);
                }
            });
        }
    }
    //更新UI
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_SUCCESS:
//                    weatherInfo = JsonUtil.parseJsonWithFastJson(FileOperateUtils.readFile(cityId));
//                    loadWeatherInfo();
//                    initFutureWeatherList();
//                    initDetailWeather();
//                    initSunSetRise();
                    Intent intent = new Intent(MyApplication.UPDATE_WEATHER_PAGE);
                    localBroadcastManager.sendBroadcast(intent);
                    refreshLayout.finishRefreshing(true);
                    break;
                case UPDATE_FAILURE:
                    if(!isNetWorkAvailable()){
                        ToastUtil.showToast(context, R.string.network_is_not_available, 2000);
                    }
                    refreshLayout.finishRefreshing(false);
                    break;
            }
        }
    };
    //*********************************************下拉刷新**************************************//

    //*********************************************触摸事件**************************************//
    /*
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.e(TAG, "y = " + weatherScrollView.getScrollY());
        return gestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        Log.e(TAG, "onDown");
        return false;
    }
    @Override
    public void onShowPress(MotionEvent e) {
        Log.e(TAG, "onShowPress");
    }
    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.e(TAG, "onSingleTapUp");
        return false;
    }
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Log.e(TAG, "onScroll");
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        Log.e(TAG, "onLongPress");
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.e(TAG, "onFling");
        return false;
    }
    */
    //*********************************************触摸事件**************************************//

    //更新下方天气的广播
    class UpdateReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case MyApplication.UPDATE_ANIMATION:
//                    Log.e(TAG, "cityId = " + cityId);
                    if(cityId.equals(preferencesUtil.MyRead("location", ""))) {
                        location.setImageResource(R.drawable.current_location);
//                        Log.e(TAG, "location = " + preferencesUtil.MyRead("location", ""));
                    }else {
                        location.setImageResource(0);
                    }
                    String id = intent.getStringExtra("cityId");
                    if(id.equals(cityId)) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent updateVideo = new Intent(MyApplication.UPDATE_VIDEO);
                                updateVideo.putExtra("weather", weatherInfo);
                                localBroadcastManager.sendBroadcast(updateVideo);
                                initWindAndPressure();
                                initSunSetRise();
                            }
                        }, 100);
                    }
                    break;
                case MyApplication.UPDATE_WEATHER_PAGE:
//                    String city = intent.getStringExtra("cityId");
//                    if(city.equals(cityId)) {
                        weatherInfo = JsonUtil.parseJsonWithFastJson(FileOperateUtils.readFile(cityId));
                        loadWeatherInfo();
                        initFutureWeatherList();
                        initDetailWeather();
                        initWindAndPressure();
                        initSunSetRise();
//                    }
                    break;
                case MyApplication.UNREGISTER_RECEIVER:
//                    Log.e(TAG, "UNREGISTER_RECEIVER");
                    unregisterReceiver();
                    clearObject();
                    System.gc();
                    break;
                case MyApplication.USE_LOCATION:
                    if(preferencesUtil.MyRead("use_location", false)){
                        use_location.setVisibility(View.VISIBLE);
                        use_location.setOnClickListener(WeatherInfoView.this);
                    }else {
                        use_location.setVisibility(View.GONE);
                    }
                    break;
            }
        }
    }
    //初始化图表
    private void initLineChart(){
//        lineChartView = (LineChartView) weatherInfoView.findViewById(R.id.line_chart);
//        LineSet data;
//        lineChartView.reset();
//        int nSets = 1;
//        int nPoints = 7;
//        for(int i = 0; i < nSets; i++){
//            data = new LineSet();
//            for(int j = 0; j < nPoints; j++){
//                data.addPoint(new Point(mLabels[j], datas[j]));
//                data.setDotsColor(context.getResources().getColor(R.color.saffron))
//                        .setDotsRadius(7.0f)
//                        .setSmooth(true)
//                        .setColor(context.getResources().getColor(R.color.sienna));
//            }
//            lineChartView.addData(data);
//        }
//        lineChartView.setAxisColor(context.getResources().getColor(R.color.light_blue));
//        lineChartView.show();
    }
    //得到View
    public View getWeatherInfoView(){
        return this.weatherInfoView;
    }
    //注销广播
    private void unregisterReceiver(){   //不注销广播接收器，会重复接收信息，进一次增加一次
        if(localBroadcastManager != null){
            localBroadcastManager.unregisterReceiver(updateReceiver);
            localBroadcastManager = null;
        }
    }
    //清空对象
    private void clearObject(){
        refreshLayout = null;

    }
    //判断网络是否可用
    private boolean isNetWorkAvailable(){
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isAvailable())
            return true;
        else
            return false;
    }
    @Override
    protected void finalize(){
        Log.e(TAG, "finalize");
        try {
            super.finalize();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }finally {
            unregisterReceiver();
        }
    }
}
