package com.james_jiang.weatherreport.ui.refresh_top;

/**
 * Created by JC on 2016/8/27.
 */
public interface PullToRefreshListener {
    //刷新时会去回调此方法，在方法内编写具体的刷新逻辑。
    // 此方法是在子线程中调用的，可以不必另开线程来进行耗时操作
    void onRefresh();
}
