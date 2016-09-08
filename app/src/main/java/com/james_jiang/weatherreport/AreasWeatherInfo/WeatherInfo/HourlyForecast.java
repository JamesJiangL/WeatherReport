package com.james_jiang.weatherreport.AreasWeatherInfo.WeatherInfo;

import java.io.Serializable;

/**
 * Created by JC on 2016/8/13.
 */
public class HourlyForecast implements Serializable{
    private static final long serialVersionUID = -435005543551580611L;
    private String date;
    private transient int hum;
    private transient int pop;
    private transient int pres;
    private int tmp;
    private transient Wind wind;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getTmp() {
        return tmp;
    }

    public void setTmp(int tmp) {
        this.tmp = tmp;
    }

    public class Wind implements Serializable{
        private static final long serialVersionUID = -7398421645025654630L;
        private transient int deg;
        private transient String dir;
        private transient String sc;
        private transient int spd;
    }
}
