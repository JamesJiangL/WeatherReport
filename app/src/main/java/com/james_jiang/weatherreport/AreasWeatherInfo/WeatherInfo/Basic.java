package com.james_jiang.weatherreport.AreasWeatherInfo.WeatherInfo;

import java.io.Serializable;

/**
 * Created by JC on 2016/8/19.
 */
public class Basic implements Serializable{
    private static final long serialVersionUID = 1989758936785305079L;
    private String city;
    private String id;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
