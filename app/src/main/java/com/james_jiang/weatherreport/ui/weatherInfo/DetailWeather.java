package com.james_jiang.weatherreport.ui.weatherInfo;

/**
 * Created by JC on 2016/8/22.
 */
public class DetailWeather {
    private String title;
    private String describe;

    public DetailWeather(String title, String describe){
        this.title = title;
        this.describe = describe;
    }
    public String getTitle() {
        return title;
    }

    public String getDescribe() {
        return describe;
    }
}
