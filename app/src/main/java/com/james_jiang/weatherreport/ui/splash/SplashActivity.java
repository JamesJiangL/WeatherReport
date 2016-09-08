package com.james_jiang.weatherreport.ui.splash;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.james_jiang.weatherreport.Common.BaseActivity;
import com.james_jiang.weatherreport.Common.MyApplication;
import com.james_jiang.weatherreport.R;
import com.james_jiang.weatherreport.Utils.Image.ImageDecodesUtil;
import com.james_jiang.weatherreport.Utils.Image.RecycleResources;
import com.james_jiang.weatherreport.Utils.ScreenUtil;
import com.james_jiang.weatherreport.Utils.SharedPreferencesUtil;
import com.james_jiang.weatherreport.ui.activity_main.MainActivity;
import com.james_jiang.weatherreport.ui.weatherInfo.ViewPagerAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends BaseActivity
    implements ViewPager.OnPageChangeListener{
    private final static String TAG = "SplashActivity";
    private SharedPreferencesUtil preferencesUtil;
    private ViewPager viewPager;
    private ViewPagerAdapter pagerAdapter;
    private List<View> viewList;
    private ImageView weather;
    private TextView weatherText;
    private TextView version;
    private ProgressBar progressBar;
    private ImageView dot1;
    private ImageView dot2;
    private ImageView dot3;
    private ImageView dot4;
    private Button startActivity;

    private int[] picture = {R.drawable.bg_morning, R.drawable.bg_evening,
                    R.drawable.clear_d, R.drawable.clear_n};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDates();
        if(isTheFirstTime()){
            requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        }
        setContentView(R.layout.splash_activity);
        //透明状态栏
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        //隐藏SmartBar
        if(ScreenUtil.isFlyme()) {
            getWindow().getDecorView()
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
        initView();
    }
    //创建一些必要的数据
    private void initDates(){
        preferencesUtil = SharedPreferencesUtil.getInstance(this);      //可以放这里
        createCacheFolder();
        if(!preferencesUtil.MyRead("writeDbOK", false)) {
            copyCityListToDB();
        }
    }
    //初始化界面
    private void initView(){
        viewPager = (ViewPager) findViewById(R.id.introduction);
        startActivity = (Button) findViewById(R.id.start_activity);
        dot1 = (ImageView) findViewById(R.id.dot_1);
        dot2 = (ImageView) findViewById(R.id.dot_2);
        dot3 = (ImageView) findViewById(R.id.dot_3);
        dot4 = (ImageView) findViewById(R.id.dot_4);
        weather = (ImageView) findViewById(R.id.img_icon);
        weatherText = (TextView) findViewById(R.id.app_name);
        version = (TextView) findViewById(R.id.version);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        if(isTheFirstTime()){
            initViewPager();
            showViewPager();
        }else {
            showSplash();
            skipSplash(2000);
        }
    }
    //初始化ViewPager
    private void initViewPager(){
        viewList = new ArrayList<>();
        initViewPagerList();
        pagerAdapter = new ViewPagerAdapter(viewList);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(this);
    }
    //初始化ViewPager里的图片
    private void initViewPagerList(){
        LayoutInflater inflater = getLayoutInflater();
        for(int i = 0; i < 4; i++){
            View view = inflater.inflate(R.layout.introduction, null);
            ImageView img= (ImageView) view.findViewById(R.id.introduction_img);
            Bitmap bitmap = ImageDecodesUtil.decodeSampledBitmapFromResource(getResources(),
                    picture[i], 240, 427);
            img.setImageBitmap(bitmap);
            viewList.add(view);
        }
    }
    //控件显示
    private void showViewPager(){
        viewPager.setVisibility(View.VISIBLE);
        startActivity.setVisibility(View.GONE);
        dot1.setVisibility(View.VISIBLE);
        dot1.setEnabled(true);
        dot2.setVisibility(View.VISIBLE);
        dot2.setEnabled(false);
        dot3.setVisibility(View.VISIBLE);
        dot3.setEnabled(false);
        dot4.setVisibility(View.VISIBLE);
        dot4.setEnabled(false);
        weather.setVisibility(View.GONE);
        weatherText.setVisibility(View.GONE);
        version.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }
    private void showSplash(){
        viewPager.setVisibility(View.GONE);
        startActivity.setVisibility(View.GONE);
        dot1.setVisibility(View.GONE);
        dot2.setVisibility(View.GONE);
        dot3.setVisibility(View.GONE);
        dot4.setVisibility(View.GONE);
        weather.setVisibility(View.VISIBLE);
        weather.setImageResource(R.drawable.weather_splash);
        weatherText.setVisibility(View.VISIBLE);
        version.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }
    //显示下方小圆点
    private void showDot(int index){
        dot1.setEnabled(0 == index);
        dot2.setEnabled(1 == index);
        dot3.setEnabled(2 == index);
        dot4.setEnabled(3 == index);
    }
    //判断是否是第一次安装进入程序
    private boolean isTheFirstTime(){
        return preferencesUtil.MyRead("isFirstTime", true);
    }
    //创建缓存文件夹
    private void createCacheFolder(){
        File file = new File(MyApplication.WCACHE);
        if(!file.exists()) {
            file.mkdirs();
        }
    }
    //将城市数据库拷贝到默认位置
    private void copyCityListToDB(){        //可以使用带资源的try/catch语句简化代码
        File file = new File("/data/data/com.james_jiang.weatherreport/databases");
        if (!file.exists()){
            file.mkdirs();
        }
        InputStream is = null;
        OutputStream os = null;
        try {
            is = getResources().getAssets().open("weather_report.db");
            if(is == null)
                System.out.println("******************************null*************************");
            os = new FileOutputStream(
                    "/data/data/com.james_jiang.weatherreport/databases/weather_report.db");
            byte buffer[] = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0 , length);
            }
            preferencesUtil.MySave("writeDbOK", true);
        } catch (IOException e) {
            e.printStackTrace();
            preferencesUtil.MySave("writeDbOK", false);
        }finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //跳出界面
    private void skipSplash(int delayTime){
        //splash延时时间
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                Intent intent = new Intent(SplashActivity.this, VideoTestActivity.class);
//                startActivity(intent);
                MainActivity.startActivity(SplashActivity.this);
                finish();
                overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
            }
        }, delayTime);
    }


    //********************************OnPageChangeListener****************************//
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//        Log.e(TAG, "position:" + position + ", positionOffset:"
//                + positionOffset + ", positionOffsetPixels:" + positionOffsetPixels);
    }

    @Override
    public void onPageSelected(int position) {
//        Log.e(TAG, "position:" + position);
        showDot(position);
        if(position == 3){
            startActivity.setVisibility(View.VISIBLE);
            startActivity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    preferencesUtil.MySave("isFirstTime", false);
//                    Intent intent = new Intent(SplashActivity.this, TestActivity.class);
//                    startActivity(intent);
                    MainActivity.startActivity(SplashActivity.this);
                    finish();
                    overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
                }
            });
        }else {
            startActivity.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
//        Log.e(TAG, "state:" + state);
    }
    //********************************OnPageChangeListener****************************//

    @Override
    public void onDestroy(){
        super.onDestroy();
        if (viewPager != null){
            viewPager.removeAllViews();
            viewPager = null;
        }
        if(weather != null){
            RecycleResources.releaseImageViewWhenDestroy(weather);
        }
        System.gc();
    }
}
