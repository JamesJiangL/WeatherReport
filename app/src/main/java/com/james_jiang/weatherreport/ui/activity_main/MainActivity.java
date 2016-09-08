package com.james_jiang.weatherreport.ui.activity_main;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.ToggleButton;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.james_jiang.weatherreport.AreasWeatherInfo.WeatherInfo.WeatherInfo;
import com.james_jiang.weatherreport.Common.BaseActivity;
import com.james_jiang.weatherreport.Common.BaseThread;
import com.james_jiang.weatherreport.Common.FullScreenVideoView;
import com.james_jiang.weatherreport.Common.MyApplication;
import com.james_jiang.weatherreport.DataBase.AreasDatabase;
import com.james_jiang.weatherreport.R;
import com.james_jiang.weatherreport.Utils.File.FileOperateUtils;
import com.james_jiang.weatherreport.Utils.Http.HttpCallBackListener;
import com.james_jiang.weatherreport.Utils.Http.HttpUtil;
import com.james_jiang.weatherreport.Utils.Http.JsonUtil;
import com.james_jiang.weatherreport.Utils.ScreenUtil;
import com.james_jiang.weatherreport.Utils.SharedPreferencesUtil;
import com.james_jiang.weatherreport.Utils.ToastUtil;
import com.james_jiang.weatherreport.service.UpdateService;
import com.james_jiang.weatherreport.ui.left_menu.LeftMenu;
import com.james_jiang.weatherreport.ui.left_menu.LeftMenuListViewAdapter;
import com.james_jiang.weatherreport.ui.search.SearchBar;
import com.james_jiang.weatherreport.ui.weatherInfo.FixedSpeedScroller;
import com.james_jiang.weatherreport.ui.weatherInfo.ScrollListView;
import com.james_jiang.weatherreport.ui.weatherInfo.ViewPagerAdapter;
import com.james_jiang.weatherreport.ui.weatherInfo.WeatherInfoView;
import com.james_jiang.weatherreport.ui.weatherInfo.WeatherScrollView;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends BaseActivity
        implements AMapLocationListener,ViewPager.OnPageChangeListener,
        View.OnClickListener{
    private final static String TAG = "MainActivity";
    //启动活动
    public static void startActivity(Context context){
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }
    //联网
    private ConnectivityManager connectivityManager;
    private boolean isNetworkAvailable;
    //定位
    private String addressName;
//    private String oldAddressName;
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;
    private boolean isFirstLocationFailed = true;       //不知道为什么会有两次返回值
    //数据库
    private AreasDatabase areasDatabase;
    //播放背景音乐
    private String rawPath;
    private FullScreenVideoView videoView;
    private VideoPlayThread videoPlayThread;
    //两个button
    private ImageView add;
    private ImageView more;
    private ImageView location;
    //左右滑动的城市天气
    private ViewPager viewPager;
    private ViewPagerAdapter pagerAdapter;
    private List<View> viewList;
    private int currentPager;
    //城市列表
    private List<String> cityList;
    private SharedPreferencesUtil preferencesUtil;
    //RequestCode
    private final static int ADD_CITY = 1;
    //Message
    private final static int UPDATE_VIDEO = 10000;
    private final static int ADD_CITY_PAGE = 10001;
    private final static int REMOVE_CITY_PAGE = 10002;
    private final static int UPDATE_NOTIFICATION = 10003;
    private final static int UPDATE_ANIMATION = 10004;
    private final static int FAILED_TO_LOAD_DATA = 10005;
    private final static int UPDATE_WIDGET = 10006;
    //广播
    private LocalBroadcastManager localBroadcastManager;
    private UpdateVideoReceiver updateVideoReceiver;
    private NetworkChangeReceiver networkChangeReceiver;

    //侧滑菜单
    private SlidingMenu menu;
    private ScrollListView leftMenuCityListView;
    private LeftMenuListViewAdapter leftMenuListViewAdapter;
    private List<LeftMenu> leftMenus;
    private Dialog dialog;
//    private boolean IS_QCOM;            //是否是高通的
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //隐藏SmartBar
        if(ScreenUtil.isFlyme()) {
            getWindow().getDecorView()
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
//        IS_QCOM = Build.HARDWARE.equals("qcom");
//        IS_QCOM = false;
        initView();
    }
    private void initView() {
        areasDatabase = AreasDatabase.getInstance(this);
        preferencesUtil = SharedPreferencesUtil.getInstance(this);
        connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        isNetworkAvailable = isNetWorkAvailable();
        rawPath = "android.resource://" + getPackageName() + "/";
        cityList = new ArrayList<>();
        currentPager = 0;
        //*******************************测试用*********************************//
//        preferencesUtil.MySave("city_list",",");
//        preferencesUtil.MySave("city_list", "CN101200105,CN101190101,CN101020100,CN101200803,CN101240101,");
//        preferencesUtil.MySave("current_page", 0);
//        preferencesUtil.MySave("location", "CN101200803");
        //*******************************测试用*********************************//
        initUpdateVideoReceiver();
        initNetworkChangeReceiver();
        initControls();
        initAMapLocation();
        initViewPager();
        startService();
        initLeftMenu();
    }
    //获取存储的城市列表
    private List<String> getCityList(){
        String city = preferencesUtil.MyRead("city_list", ",");
//        Log.e(TAG, "city = " + city);
        if(city.length() < 6)       //小于一个city名称的长度时
            return cityList;
        List<String> list = new ArrayList<>();
        String[] split = city.split(",");
        int length = split.length;
//        if(length < 1)      //判断条件不准确，当city = ""时，长度为1
//            return null;
        list.addAll(Arrays.asList(split).subList(0, length));
        return list;
    }
    //初始化加载城市天气
    private void initCityWeatherList(){
//        if((cityList = getCityList()) == null) {
//            cityList = new ArrayList<>();
//            return;
//        }
        cityList = getCityList();
        if(cityList.size() == 0){
            videoPlayer(R.raw.weather_2nd_bg_sunny);
            add.setOnClickListener(this);
            more.setOnClickListener(this);
            if(preferencesUtil.MyRead("use_location", false)) {
                Log.e(TAG, "use_location");
                location.setVisibility(View.VISIBLE);
                location.setOnClickListener(this);
            }else {
                location.setVisibility(View.GONE);
            }
        }else {
            add.setVisibility(View.GONE);
            more.setVisibility(View.GONE);
            location.setVisibility(View.GONE);
            viewList.clear();               //先清空
            for(int i = 0; i < cityList.size(); i++){
                WeatherInfoView view = new WeatherInfoView(this, cityList.get(i));
                viewList.add(view.getWeatherInfoView());
            }
        }
    }
    //初始化ViewPager
    private void initViewPager(){
        viewList = new ArrayList<>();
        initCityWeatherList();
        viewPager = (ViewPager) findViewById(R.id.weather_info_view_pager);
        viewPager.setOffscreenPageLimit(1);
        pagerAdapter = new ViewPagerAdapter(viewList);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(this);
        if(cityList != null && cityList.size() > 0) {
            int index = preferencesUtil.MyRead("current_page", 0);
            viewPager.setCurrentItem(index);
        }
    }
    //设置ViewPager滑动速度
    private void setViewPagerSpeed(){
        try {
            Field field = ViewPager.class.getDeclaredField("mScroller");
            field.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(viewPager.getContext(),
                    new AccelerateInterpolator());
            scroller.setmDuration(100);
            field.set(viewPager, scroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //初始化界面控件
    private void initControls(){
        videoView = (FullScreenVideoView) findViewById(R.id.weather_player);
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return true;                //设置为true可以截断错误的传递
            }
        });
        add = (ImageView) findViewById(R.id.add);
        more = (ImageView) findViewById(R.id.more);
        location = (ImageView) findViewById(R.id.location);
    }

    //****************************************左滑菜单********************************************//
    //左滑菜单
    private void initLeftMenu(){
        menu = new SlidingMenu(this);
        //设置菜单显示模式
        menu.setMode(SlidingMenu.LEFT);
        //设置滑动菜单阴影
        menu.setShadowWidthRes(R.dimen.shadow_width);
        menu.setShadowDrawable(R.drawable.shadow);
        // 设置滑动菜单视图拉开后离边框距离
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        // 设置渐入渐出效果的值
        menu.setFadeEnabled(false);
        menu.setFadeDegree(0.35f);
        //使SlidingMenu附加在Activity上
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        //设置菜单的布局
        menu.setMenu(R.layout.left_menu);
        //设置缩放比例
//        menu.setBehindScrollScale(0.5f);

        initLeftMenuControls();
    }
    //左滑菜单控件初始化
    private void initLeftMenuControls(){
        initLeftMenuCityListView();
        useLocation();
        useNotification();
        updateDuration();
    }
    //左滑菜单城市列表
    private void initLeftMenuCityListView(){
        leftMenuCityListView = (ScrollListView) menu.findViewById(R.id.location_list);
        leftMenus = new ArrayList<>();
        String currentLoc = preferencesUtil.MyRead("location", "0");
        for(int i = 0; i < cityList.size(); i++){
            LeftMenu leftMenu = new LeftMenu();
            if(currentLoc.equals(cityList.get(i)))
                leftMenu.setLocation(true);
            else
                leftMenu.setLocation(false);
            leftMenu.setCityName(areasDatabase.getCityName(cityList.get(i)));
            leftMenus.add(leftMenu);
        }
        leftMenuListViewAdapter = new
                LeftMenuListViewAdapter(this, R.layout.left_menu_item, leftMenus);
        leftMenuCityListView.setAdapter(leftMenuListViewAdapter);
        leftMenuCityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                //删除地名
                RelativeLayout item = (RelativeLayout) leftMenuCityListView.getChildAt(position);
                ImageView delete = (ImageView) item.findViewById(R.id.delete);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        leftMenus.remove(position);
                        leftMenuListViewAdapter.updateList(leftMenus);
                        leftMenuListViewAdapter.notifyDataSetChanged();
                        removeCityPage(position);
                        return;
                    }
                });
                //进入
                menu.toggle();
                viewPager.setCurrentItem(position);
//                TextView city = (TextView) item.findViewById(R.id.city_name);
//                city.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        menu.toggle();
//                        viewPager.setCurrentItem(position);
//                    }
//                });
//                ImageView location = (ImageView) item.findViewById(R.id.location_img);
//                location.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        menu.toggle();
//                        viewPager.setCurrentItem(position);
//                    }
//                });
            }
        });
    }
    //更新左滑菜单城市信息
    private void updateLeftCityList(List<String> cityList){
        leftMenus.clear();
        String currentLoc = preferencesUtil.MyRead("location", "0");
        for(int i = 0; i < cityList.size(); i++){
            LeftMenu leftMenu = new LeftMenu();
            if(currentLoc.equals(cityList.get(i)))
                leftMenu.setLocation(true);
            else
                leftMenu.setLocation(false);
            leftMenu.setCityName(areasDatabase.getCityName(cityList.get(i)));
            leftMenus.add(leftMenu);
        }
        leftMenuListViewAdapter.updateList(leftMenus);
        leftMenuListViewAdapter.notifyDataSetChanged();
    }
    //设置
    //使用定位
    private void useLocation(){
        final ToggleButton useLocation = (ToggleButton) menu.findViewById(R.id.use_location);
        useLocation.setChecked(preferencesUtil.MyRead("use_location", false));
        useLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                preferencesUtil.MySave("use_location", isChecked);
                if(isChecked)
                    useLocation.setBackgroundResource(R.drawable.slip_on);
                else
                    useLocation.setBackgroundResource(R.drawable.slip_off);
                if(cityList.size() == 0){
                    if(isChecked){
                        location.setVisibility(View.VISIBLE);
                        location.setOnClickListener(MainActivity.this);
                    }else {
                        location.setVisibility(View.GONE);
                    }
                    return;
                }
                Intent intent = new Intent(MyApplication.USE_LOCATION);
                localBroadcastManager.sendBroadcast(intent);
            }
        });
    }
    //使用通知栏
    private void useNotification(){
        final ToggleButton useNotification = (ToggleButton) menu.findViewById(R.id.use_notification);
        useNotification.setChecked(preferencesUtil.MyRead("show_notification", false));
        useNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    useNotification.setBackgroundResource(R.drawable.slip_on);
                else
                    useNotification.setBackgroundResource(R.drawable.slip_off);

                preferencesUtil.MySave("show_notification", isChecked);
                Intent intent = new Intent(MyApplication.UPDATE_NOTIFICATION);
                localBroadcastManager.sendBroadcast(intent);
            }
        });
    }
    //更新时间
    private void updateDuration(){
        Spinner spinner = (Spinner) menu.findViewById(R.id.refresh_duration);
        spinner.setSelection(preferencesUtil.MyRead("spinner_choice", 0));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.e(TAG, "position = " + position);
                switch (position){
                    case 0:
                        preferencesUtil.MySave("manual_update", true);
                        preferencesUtil.MySave("updateTime", 24);
                        break;
                    case 1:
                        preferencesUtil.MySave("manual_update", false);
                        preferencesUtil.MySave("updateTime", 1);
                        break;
                    case 2:
                        preferencesUtil.MySave("manual_update", true);
                        preferencesUtil.MySave("updateTime", 2);
                        break;
                    case 3:
                        preferencesUtil.MySave("manual_update", false);
                        preferencesUtil.MySave("updateTime", 3);
                        break;
                    case 4:
                        preferencesUtil.MySave("manual_update", true);
                        preferencesUtil.MySave("updateTime", 5);
                        break;
                    case 5:
                        preferencesUtil.MySave("manual_update", false);
                        preferencesUtil.MySave("updateTime", 6);
                        break;
                }
                preferencesUtil.MySave("spinner_choice", position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    //版本号
    private void showEdition(){
        Log.e(TAG, "showEdition");
        final RelativeLayout edition = (RelativeLayout) menu.findViewById(R.id.version_layout);
        edition.setVisibility(View.INVISIBLE);
        ViewTreeObserver observer = edition.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                edition.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                int top = edition.getTop() + ScreenUtil.getStatusHeight(MainActivity.this);
                int height = edition.getHeight();
                int H = ScreenUtil.getScreenHeight_px(MainActivity.this);
                Log.e(TAG, "top = " + top + ", height = " + height);
                if((top + height) >= H)
                    return;

                top = H - height - top + 10;
                LinearLayout.LayoutParams params =
                        (LinearLayout.LayoutParams) edition.getLayoutParams();
                params.setMargins(0, top, 0, 10);
                edition.setLayoutParams(params);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        edition.setVisibility(View.VISIBLE);
                    }
                }, 10);
            }
        });
    }
    //****************************************左滑菜单********************************************//
    //开启服务
    private void startService(){
        if(!isServiceRunning()) {
            Intent intent = new Intent(this, UpdateService.class);
            startService(intent);
        }
    }
    //检查服务是否已经运行
    private boolean isServiceRunning(){
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.james_jiang.weatherreport.service.UpdateService"
                    .equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    //添加按钮
    private void addButton(){
        Log.e(TAG, "add");
        Intent intent = new Intent(this, SearchBar.class);
        startActivityForResult(intent, ADD_CITY);
//        Intent intentAdd = new Intent(this, WatchDogService.class);
//        intentAdd.putExtra("widget_click_type", "add");
//        startService(intentAdd);
    }
    private void moreButton(){
        Log.e(TAG, "more");
        menu.toggle();
//        Intent intentMore = new Intent(this, WatchDogService.class);
//        intentMore.putExtra("widget_click_type", "more");
//        startService(intentMore);
    }
    //****************************************高德API********************************************//
    private void initAMapLocation(){
        locationClient = new AMapLocationClient(this.getApplicationContext());
        locationOption = new AMapLocationClientOption();
        // 设置定位监听
        locationClient.setLocationListener(this);
        initAMapOption();
    }
    private void initAMapOption(){
        // 设置定位模式为低功耗模式
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        //设置为连续定位
        locationOption.setOnceLocation(true);
        // 设置是否需要显示地址信息
        locationOption.setNeedAddress(true);
        // 设置是否开启缓存
        locationOption.setLocationCacheEnable(true);
    }
    private void startLocation(){
        //设置连续定位时间 6小时刷新一次
        locationOption.setInterval(1000 * 60 * 60 * 6);
        // 设置定位参数
        locationClient.setLocationOption(locationOption);
        //启动定位
        locationClient.startLocation();
        dialog = LoadingDialog.createLoadingDialog(this, getString(R.string.locating_hold));
        dialog.show();
    }
    private void stopLocation(){
        if(locationClient != null)
            locationClient.stopLocation();
    }
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if(aMapLocation == null)
            return;
        if(aMapLocation.getErrorCode() == 0){   //表示定位成功
            addressName = aMapLocation.getDistrict();
            String areas_id = areasDatabase.getAreasId(addressName);
            if(areas_id == null){
                String city = aMapLocation.getCity();
                areas_id = areasDatabase.getAreasId(city);
            }
            preferencesUtil.MySave("location", areas_id);       //定位成功就写入
            if(cityList.contains(areas_id)){
                int index = cityList.indexOf(areas_id);
                viewPager.setCurrentItem(index);
                dialog.dismiss();
                return;
            }
            //得到了地点，并且和上一个不同，开始查询数据库
//            if(!oldAddressName.equals(addressName)) {
                requestLocationWeatherInfo(areas_id);
//                oldAddressName = addressName;
                Log.e(TAG, "address: " + addressName);
//            }
        }else {     //定位失败
            if(isFirstLocationFailed) {
                isFirstLocationFailed = false;
                Log.e(TAG, "定位失败" + aMapLocation.getErrorCode());
                ToastUtil.showToast(this, "定位失败！", 3000);
                dialog.dismiss();
            }
        }
    }
    //请求服务器传回天气信息并解析
    private void requestLocationWeatherInfo(final String areas_id){
        HttpUtil.weatherRequest(areas_id, new HttpCallBackListener() {
            @Override
            public void onFinish(String response) {
                Log.e(TAG, "areasWeatherInfo: ");
                //成功得到数据之后就开始解析
                cityList.add(areas_id);     //定位成功之后需要添加进去
                addCityToPreferences(cityList);
                addCityPage(areas_id);
                dialog.dismiss();
            }
            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                failedToLoadData();
            }
        });
    }
    //****************************************高德API********************************************//
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode){
            case ADD_CITY:
                if(resultCode == RESULT_OK){
                    final String cityId = data.getStringExtra("cityId");
                    if(cityList.contains(cityId)){
                        int index = cityList.indexOf(cityId);
                        viewPager.setCurrentItem(index);
                        return;
                    }
                    //打开progressDialog
                    dialog = LoadingDialog.createLoadingDialog(this, getString(R.string.data_loading));
                    dialog.show();
                    HttpUtil.weatherRequest(cityId, new HttpCallBackListener() {
                        @Override
                        public void onFinish(String response) {
                            cityList.add(cityId);       //添加数据到配置文件
                            addCityToPreferences(cityList);
                            dialog.dismiss();
                            addCityPage(cityId);
                        }
                        @Override
                        public void onError(Exception e) {
                            e.printStackTrace();
                            failedToLoadData();
                        }
                    });
                }
                break;
        }
    }
    //****************************************主界面控件处理*******************************************//
    //播放背景
    private void videoPlayer(int src){
//        if(!IS_QCOM) {
            videoView.setVideoURI(Uri.parse(rawPath + src));
//        videoView.setVideoPath("/mnt/sdcard/weather_2nd_bg_clear.mp4");
            if (videoPlayThread == null) {
                videoPlayThread = new VideoPlayThread();
                videoPlayThread.start();
            }
//        }
    }
    //播放gif图
    private void gifPlayer(String path, String data){
//        String path = "file:///android_asset/" + "thunder_shower_snow.gif";
//        String data = "<HTML><Div align=\"center\"  margin=\"0px\">" +
//                "<IMG src=\""+path+"\" margin=\"0px\"/></Div>";
//        gifPlayer(path, data);
//        weatherImg.setVisibility(View.INVISIBLE);
//        weatherImg.loadDataWithBaseURL(path, data, "text/html", "utf-8", null);
//        weatherImg.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
//        weatherImg.setBackgroundColor(0);
//        weatherImg.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add:
                addButton();
                break;
            case R.id.more:
                moreButton();
                break;
            case R.id.location:
                startLocation();
                break;
        }
    }
    class VideoPlayThread extends BaseThread{
        @Override
        public void run(){
            while (runFlag){
                try {
                    synchronized (this) {
                        if (!videoView.isPlaying()) {
                            videoView.start();
                        }
                    }
                    sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    //****************************************主界面控件处理*******************************************//

    //****************************************PagerListener*******************************************//
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//        Log.e(TAG, "position = " + position +
//        " positionOffset = " + positionOffset +
//        " positionOffsetPixels = " + positionOffsetPixels);
        if(cityList.size() > 0 && position == 0 &&
                positionOffset == 0.0f && positionOffsetPixels == 0.0f){
            String cityId = cityList.get(currentPager);
            Log.e(TAG, "onPageScrolled : cityId = " + cityId);
            updateAnimation(cityId);
            menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
//            updateWidget();           //每次进入会自动更新widget?
        }
    }
    @Override
    public void onPageSelected(int position) {
//        Log.e(TAG, "onPageSelected: " + position);
        currentPager = position;
        String cityId = cityList.get(currentPager);
        updateAnimation(cityId);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                preferencesUtil.MySave("current_page", currentPager);   //储存当前滑动位置
                updateNotification();
                updateWidget();
                int size = viewList.size();
                if(currentPager == 0) {
                    WeatherScrollView scrollView = (WeatherScrollView) viewList.get(currentPager + 1)
                            .findViewById(R.id.weather_scrollView);
                    scrollView.scrollTo(0, 0);
                    menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

                }else if(currentPager == size - 1){
                    WeatherScrollView scrollView = (WeatherScrollView) viewList.get(currentPager - 1)
                            .findViewById(R.id.weather_scrollView);
                    scrollView.scrollTo(0, 0);

                    menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
                }else {
                    WeatherScrollView scrollView1 = (WeatherScrollView) viewList.get(currentPager - 1)
                            .findViewById(R.id.weather_scrollView);
                    scrollView1.scrollTo(0, 0);
                    WeatherScrollView scrollView2 = (WeatherScrollView) viewList.get(currentPager + 1)
                            .findViewById(R.id.weather_scrollView);
                    scrollView2.scrollTo(0, 0);

                    menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
                }
            }
        }, 200);
    }
    @Override
    public void onPageScrollStateChanged(int state) {
//        Log.e(TAG, "state = " + state);
    }

    //****************************************PagerListener*******************************************//

    //****************************************Handler*******************************************//
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case ADD_CITY_PAGE:
                    if(add.getVisibility() != View.GONE) {
                        add.setVisibility(View.GONE);
                        more.setVisibility(View.GONE);
                        location.setVisibility(View.GONE);
                    }
                    String cityId = (String) msg.obj;
                    WeatherInfoView view = new WeatherInfoView(MainActivity.this, cityId);
                    pagerAdapter.addPage(view.getWeatherInfoView());
                    pagerAdapter.notifyDataSetChanged();
                    viewPager.setCurrentItem(viewList.size() - 1);
                    updateLeftCityList(cityList);
                    updateNotification();
                    updateWidget();
                    break;
                case REMOVE_CITY_PAGE:
                    int index = (int) msg.obj;
                    delete(index);
                    updateWidget();
                    break;
                case UPDATE_VIDEO:
                    WeatherInfo weather = (WeatherInfo) msg.obj;
                    videoPlayer(VideoCode.getWeatherCode(weather));
                    break;
                case UPDATE_NOTIFICATION:
                    Intent notiIntent = new Intent(MyApplication.UPDATE_NOTIFICATION);      //更新通知栏
                    localBroadcastManager.sendBroadcast(notiIntent);
                    break;
                case UPDATE_ANIMATION:
                    Log.e(TAG, "UPDATE_ANIMATION");
                    Intent animIntent = new Intent(MyApplication.UPDATE_ANIMATION);
                    animIntent.putExtra("cityId", (String) msg.obj);
                    localBroadcastManager.sendBroadcast(animIntent);
                    break;
                case FAILED_TO_LOAD_DATA:
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            ToastUtil.showToast(MainActivity.this, R.string.failed_to_load_data, 2000);
                        }
                    }, 4 * 1000);
                    break;
                case UPDATE_WIDGET:
                    Log.e(TAG, "UPDATE_WIDGET");
                    Intent widgetIntent = new Intent(MyApplication.UPDATE_WIDGET);
                    sendBroadcast(widgetIntent);
                    break;
            }
        }
    };
    //更新背景
    private void updateVideo(WeatherInfo weather){
        Message message = new Message();
        message.what = UPDATE_VIDEO;
        message.obj = weather;
        handler.sendMessage(message);
    }
    //新增页面
    private void addCityPage(String cityId){
        Message message = new Message();
        message.what = ADD_CITY_PAGE;
        message.obj = cityId;
        handler.sendMessage(message);
    }
    private void removeCityPage(int index){
        Message message = new Message();
        message.what = REMOVE_CITY_PAGE;
        message.obj = index;
        handler.sendMessage(message);
    }
    //添加城市列表
    private void addCityToPreferences(List<String> cityList){
        StringBuilder builder = new StringBuilder();
        int size = cityList.size();
        for (int i = 0; i < size; i++){
            builder.append(cityList.get(i));
            builder.append(",");
        }
        preferencesUtil.MySave("city_list", builder.toString());
    }
    //删除页面
    private void delete(int index){
//        Log.e(TAG, "index = " + index);
        int size = viewList.size();
        if(size == 0) {    //如果已经完全删除
            videoPlayer(R.raw.weather_2nd_bg_sunny);
            add.setVisibility(View.VISIBLE);
            more.setVisibility(View.VISIBLE);
            if(preferencesUtil.MyRead("use_location", false)) {
                location.setVisibility(View.VISIBLE);
                location.setOnClickListener(this);
            }
            add.setOnClickListener(this);       //还必须重新设置监听器？
            more.setOnClickListener(this);
            return;
        }
        if(index == size - 1){
            if(size == 1){
                index = 0;
            }else {
                index = index - 1;
            }
        }
//        Log.e(TAG, "index 1 = " + index);
        currentPager = index;
        preferencesUtil.MySave("current_page", index);   //储存当前滑动位置
        cityList.remove(index);
        addCityToPreferences(cityList); //更新配置文件列表
        pagerAdapter.removePage(viewPager, index);
//        viewList.remove(index);       //和pagerAdapter是同一个对象
        pagerAdapter.notifyDataSetChanged();    //更新界面
        viewPager.setCurrentItem(index);
        if(cityList.size() == 0){
            videoPlayer(R.raw.weather_2nd_bg_sunny);
            add.setVisibility(View.VISIBLE);
            more.setVisibility(View.VISIBLE);
            add.setOnClickListener(this);       //还必须重新设置监听器？
            more.setOnClickListener(this);
            if(preferencesUtil.MyRead("use_location", false)) {
                location.setVisibility(View.VISIBLE);
                location.setOnClickListener(this);
            }
        }
    }
    //更新通知栏
    private void updateNotification(){
        Message message = new Message();
        message.what = UPDATE_NOTIFICATION;
        handler.sendMessage(message);
    }
    //更新widget
    private void updateWidget(){
        Message message = new Message();
        message.what = UPDATE_WIDGET;
        handler.sendMessage(message);
    }
    //更新天气动画
    private void updateAnimation(String cityId){
        Message message = new Message();
        message.what = UPDATE_ANIMATION;
        message.obj = cityId;
        handler.sendMessage(message);
    }
    private void failedToLoadData(){
        Message message = new Message();
        message.what = FAILED_TO_LOAD_DATA;
        handler.sendMessage(message);
    }
    //****************************************Handler*******************************************//
    //初始化广播
    private void initUpdateVideoReceiver(){
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(MyApplication.UPDATE_VIDEO);
        filter.addAction(MyApplication.ADD_BUTTON);
        filter.addAction(MyApplication.MORE_BUTTON);
        filter.addAction(MyApplication.LOCATION);
        updateVideoReceiver = new UpdateVideoReceiver();
        localBroadcastManager.registerReceiver(updateVideoReceiver, filter);
    }
    //更新页面广播， 本地广播
    class UpdateVideoReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case MyApplication.UPDATE_VIDEO:
                    WeatherInfo weather = (WeatherInfo) intent.getSerializableExtra("weather");
                    updateVideo(weather);
                    break;
//                case MyApplication.UPDATE_WEATHER_PAGE:
//                    initCityWeatherList();
//                    pagerAdapter.updatePage(viewPager, viewList);
//                    pagerAdapter.notifyDataSetChanged();
//                    viewPager.setCurrentItem(currentPager);
//                    updateAnimation(cityList.get(currentPager));    //只更新当前页面
//                    break;
                case MyApplication.ADD_BUTTON:
                    addButton();
                    break;
                case MyApplication.MORE_BUTTON:
                    moreButton();
                    break;
                case MyApplication.LOCATION:
                    startLocation();
                    break;
            }
        }
    }
    //初始化广播
    private void initNetworkChangeReceiver(){
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver, filter);
    }
    class NetworkChangeReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, "NetworkChangeReceiver");
            switch (intent.getAction()){
                case "android.net.conn.CONNECTIVITY_CHANGE":
                    isNetworkAvailable = isNetWorkAvailable();
                    break;
            }
        }
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
    public void onResume(){
        super.onResume();
        Log.e(TAG, "onResume");
        if(videoView != null){
            videoView.resume();
        }
    }
    @Override
    public void onPause(){
        super.onPause();
        Log.e(TAG, "onPause");
        if(videoView != null){
            videoView.pause();
        }
    }
    @Override
    public void onStop(){
        super.onStop();
        Log.e(TAG, "onStop");
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.e(TAG, "onDestroy");
        Intent intent = new Intent(MyApplication.UNREGISTER_RECEIVER);
        localBroadcastManager.sendBroadcast(intent);        //使用广播去关闭广播，挺怪异的

        if(viewPager != null){
            viewPager.removeAllViews();
            viewList = null;
            viewPager = null;
            pagerAdapter = null;
            cityList = null;
        }
        if (null != locationClient) {
            //如果AMapLocationClient是在当前Activity实例化的，
            // 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
            stopLocation();
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
        if(videoPlayThread != null){
            videoPlayThread.stopThread();
            videoPlayThread = null;
        }
        if(videoView != null){
            videoView.suspend();
            videoView = null;
        }
        if(localBroadcastManager != null){
            localBroadcastManager.unregisterReceiver(updateVideoReceiver);
            localBroadcastManager = null;
            updateVideoReceiver = null;
        }
        unregisterReceiver(networkChangeReceiver);
        System.gc();
    }

    //***************************************暂时不用****************************************//
    //获取存储的天气
    private List<WeatherInfo> getWeatherInfoList(List<String> cityList){
        if(cityList == null)
            return null;
        List<WeatherInfo> weatherInfos = new ArrayList<>();
        int size = cityList.size();
        for(int i = 0; i < size; i++){
            String data = FileOperateUtils.readFile(cityList.get(i));
            WeatherInfo weatherInfo = JsonUtil.parseJsonWithFastJson(data);
            weatherInfos.add(weatherInfo);
        }
        return weatherInfos;
    }
    //***************************************暂时不用****************************************//
}
