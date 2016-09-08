package com.james_jiang.weatherreport.Utils.Http;

/**
 * Created by JC on 2016/8/7.
 */
public interface HttpCallBackListener {
    void onFinish(String response);
    void onError(Exception e);
}
