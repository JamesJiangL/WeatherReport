package com.james_jiang.weatherreport.ui.refresh_top;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.james_jiang.weatherreport.R;
import com.james_jiang.weatherreport.Utils.SharedPreferencesUtil;
import com.james_jiang.weatherreport.ui.weatherInfo.WeatherScrollView;

import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.Thread.sleep;

/**
 * Created by JC on 2016/8/27.
 */
public class RefreshLayout extends LinearLayout implements View.OnTouchListener{
    //    private final static int PULL_TO_REFRESH = 0;       //下拉刷新
//    private final static int RELEASE_TO_REFRESH = 1;    //释放立即刷新
//    private final static int REFRESHING = 2;                //正在刷新
//    private final static int REFRESH_FINISHED = 3;          //刷新完成/失败
    private final static String TAG = "RefreshLayout";
    private enum State {PULL_TO_REFRESH, RELEASE_TO_REFRESH, REFRESHING, REFRESH_FINISHED}
    private final static int SCROLL_SPEED = -10;       //回滚速度
    private SharedPreferencesUtil preferencesUtil;
    private final static String UPDATE_TIME = "header_update_time_";
    private View header;                //下拉头
    private WeatherScrollView refreshView;           //需要刷新的视图
    private ImageView arrow;            //下拉箭头
    private ProgressBar progressBar;        //下拉进度条
    private TextView description;           //下拉刷新
    private TextView updateTime;              //刷新时间
    private MarginLayoutParams headerLayoutParams;      //下拉头布局参数
    private int headerHeight;                     //下拉头高度
    private State currentState;     //刷新状态
    private State lastState;
    private float yDown;        //手指按下的y坐标
    private int touchSlop;      ////滑动时，手指移动大于这个值，才触发
    private boolean loadOnce;   //是否已经加载过布局
    private boolean ableToRefresh;  //是否可以刷新

    private PullToRefreshListener listener;     //下拉刷新的回调接口
    private int id;     //为了防止不同界面的下拉刷新在上次更新时间上互相有冲突，使用id来做区分

    public RefreshLayout(Context context) {
        super(context);
    }
    public RefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initFiled();
        initView(context);
    }
    public RefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RefreshLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    //初始化变量
    private void initFiled(){
        currentState = State.PULL_TO_REFRESH;
        lastState = State.PULL_TO_REFRESH;
        loadOnce = false;
        ableToRefresh = false;
        id = -1;
    }
    //初始化布局
    private void initView(Context context){
        preferencesUtil = SharedPreferencesUtil.getInstance(context);
        header = LayoutInflater.from(context).inflate(R.layout.refresh_top, null, true);
        arrow = (ImageView) header.findViewById(R.id.arrow);
        progressBar = (ProgressBar) header.findViewById(R.id.progress_bar);
        description = (TextView) header.findViewById(R.id.description);
        updateTime = (TextView) header.findViewById(R.id.update_time);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
//        setUpdateTime();
        setOrientation(VERTICAL);
        addView(header, 0);
    }
    //进行一些关键性的初始化操作，比如：将下拉头向上偏移进行隐藏，给需要刷新的视图注册touch事件
    @Override
    protected void onLayout(boolean change, int l, int t, int r, int b){
        super.onLayout(change, l, t, r, b);
        if(change && !loadOnce){
            headerHeight = -header.getHeight();
            headerLayoutParams = (MarginLayoutParams) header.getLayoutParams();
            headerLayoutParams.topMargin = headerHeight;
            refreshView = (WeatherScrollView) getChildAt(1);
            refreshView.setOnTouchListener(this);
            loadOnce = true;
        }
    }

    //当需要刷新的布局被触摸调用时的处理逻辑
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        setAbleToRefresh();
        if(ableToRefresh){
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    yDown = event.getRawY();
                    setUpdateTime();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float yMove = event.getRawY();
                    float distance = yMove - yDown;
                    //如果手指是下滑状态，并且下拉头是完全隐藏的，就屏蔽下拉事件
                    if(distance < 0.0f && headerLayoutParams.topMargin <= headerHeight){
                        return false;
                    }
                    if(distance < touchSlop){
                        return false;
                    }
                    if(currentState != State.REFRESHING){
                        if(headerLayoutParams.topMargin > 0){
                            currentState = State.RELEASE_TO_REFRESH;
                        }else {
                            currentState = State.PULL_TO_REFRESH;
                        }
                        headerLayoutParams.topMargin = (int) (distance / 2.0f) + headerHeight;
                        header.setLayoutParams(headerLayoutParams);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                default:
                    if(currentState == State.RELEASE_TO_REFRESH){
                        //松手时刻调用刷新任务
                        new RefreshTask().execute();
                    }else if(currentState == State.PULL_TO_REFRESH){
                        // 松手时如果是下拉状态，就去调用隐藏下拉头的任务
                        new HideHeaderTask().execute();
                    }
                    break;
            }
            // 时刻记得更新下拉头中的信息
            if(currentState == State.PULL_TO_REFRESH ||
                    currentState == State.RELEASE_TO_REFRESH){
                updateHeaderView();
                // 当前正处于下拉或释放状态，要让待刷新视图失去焦点
                refreshView.setPressed(false);
                refreshView.setFocusable(false);
                refreshView.setFocusableInTouchMode(false);
                lastState = currentState;
                return true;
            }
        }
        return false;
    }
    private void setAbleToRefresh(){
        if(refreshView.getScrollY() == 0)
            ableToRefresh = true;
        else
            ableToRefresh = false;
    }
    //下拉刷新控件注册回调监听器
    public void setOnRefreshListener(PullToRefreshListener listener, int id){
        this.listener = listener;
        this.id = id;
    }


    //更新下拉头信息
    private void updateHeaderView(){
        if(lastState != currentState){
            switch (currentState){
                case PULL_TO_REFRESH:
                    description.setText(getResources().getString(R.string.pull_to_refresh));
                    arrow.setVisibility(VISIBLE);
                    progressBar.setVisibility(GONE);
                    rotateArrow();
                    break;
                case RELEASE_TO_REFRESH:
                    description.setText(getResources().getString(R.string.release_to_refresh));
                    arrow.setVisibility(VISIBLE);
                    progressBar.setVisibility(GONE);
                    rotateArrow();
                    break;
                case REFRESHING:
                    description.setText(getResources().getString(R.string.refreshing));
                    arrow.clearAnimation();
                    arrow.setVisibility(GONE);
                    progressBar.setVisibility(VISIBLE);
                    break;
            }
            setUpdateTime();
        }
    }
    //旋转箭头
    private void rotateArrow(){
        float pivotX = arrow.getWidth() / 2.0f;
        float pivotY = arrow.getHeight() / 2.0f;
        float fromDegree = 0.0f;
        float toDegree = 0.0f;
        if(currentState == State.PULL_TO_REFRESH){
            fromDegree = 180.0f;
            toDegree = 360.0f;
        }else if(currentState == State.RELEASE_TO_REFRESH){
            fromDegree = 0.0f;
            toDegree = 180.0f;
        }
        RotateAnimation animation = new RotateAnimation(fromDegree, toDegree, pivotX, pivotY);
        animation.setDuration(100);
        animation.setFillAfter(true);
        arrow.startAnimation(animation);
    }
    //下拉头更新时间的描述
    private void setUpdateTime(){
        String time = preferencesUtil.MyRead(UPDATE_TIME + id, null);
        if(time == null){
            time = getResources().getString(R.string.update_no_yet);
        }
        updateTime.setText(getResources().getString(R.string.update_time) + time);
    }
    //下拉刷新完成
    public void finishRefreshing(boolean isSuccess){
        currentState = State.REFRESH_FINISHED;
        if(isSuccess) {
            description.setText(getResources().getString(R.string.success_to_refresh));
            SimpleDateFormat format = new SimpleDateFormat("MM/dd HH:mm");
            Date date = new Date(System.currentTimeMillis());
            String time = format.format(date);
            preferencesUtil.MySave(UPDATE_TIME + id, time);
        }else {
            description.setText(getResources().getString(R.string.failed_to_refresh));
        }
        new HideHeaderTask().execute();
    }
    //正在刷新的任务，在此任务中会去回调注册进来的下拉刷新监听器
    class RefreshTask extends AsyncTask<Void, Integer, Void>{
        @Override
        protected Void doInBackground(Void... params) {
            int topMargin = headerLayoutParams.topMargin;
            while (true){
                topMargin = topMargin + SCROLL_SPEED;
                if(topMargin <= 0){
                    topMargin = 0;
                    break;
                }
                publishProgress(topMargin);
                try {
                    sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            currentState = State.REFRESHING;
            publishProgress(0);
            if(listener != null){
                listener.onRefresh();
            }
            return null;
        }
        @Override
        protected void onProgressUpdate(Integer... topMargin){
            updateHeaderView();
            headerLayoutParams.topMargin = topMargin[0];
            header.setLayoutParams(headerLayoutParams);
        }
    }
    //隐藏下拉头的任务，当未进行下拉刷新或下拉刷新完成后，此任务将会使下拉头重新隐藏
    class HideHeaderTask extends AsyncTask<Void, Integer, Integer>{
        @Override
        protected Integer doInBackground(Void... params) {
            int topMargin = headerLayoutParams.topMargin;
            while (true){
                topMargin = topMargin + SCROLL_SPEED / 2;
                if(topMargin <= headerHeight){
                    topMargin = headerHeight;
                    break;
                }
                publishProgress(topMargin);
                try {
                    sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return topMargin;
        }

        @Override
        protected void onProgressUpdate(Integer... topMargin) {
            headerLayoutParams.topMargin = topMargin[0];
            header.setLayoutParams(headerLayoutParams);
        }

        @Override
        protected void onPostExecute(Integer topMargin) {
            headerLayoutParams.topMargin = topMargin;
            header.setLayoutParams(headerLayoutParams);
            currentState = State.REFRESH_FINISHED;
        }
    }
}
