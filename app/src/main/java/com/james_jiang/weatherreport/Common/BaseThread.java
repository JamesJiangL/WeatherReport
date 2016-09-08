package com.james_jiang.weatherreport.Common;

/**
 * Created by john on 2016/3/4.
 */
public class BaseThread extends Thread {
    protected boolean runFlag = true;
    public void stopThread(){
        this.runFlag = false;
    }
    @Override
    public void run(){
    }
}
