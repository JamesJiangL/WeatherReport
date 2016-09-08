package com.james_jiang.weatherreport.AreasWeatherInfo.WeatherInfo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by JC on 2016/8/13.
 * 真TMD 的坑，动不动就变数据了，免费的果然没有好东西
 */
public class WeatherInfo implements Serializable{
    private static final long serialVersionUID = -2043308901237792091L;
    private AQI aqi;
    private Basic basic;
    private List<DailyForecast> daily_forecast;
    private List<HourlyForecast> hourly_forecast;
    private Now now;
    private String status;
    private Suggestion suggestion;
    public AQI getAqi() {
        return aqi;
    }

    public void setAqi(AQI aqi) {
        this.aqi = aqi;
    }

    public Basic getBasic() {
        return basic;
    }

    public void setBasic(Basic basic) {
        this.basic = basic;
    }

    public List<DailyForecast> getDaily_forecast() {
        return daily_forecast;
    }

    public void setDaily_forecast(List<DailyForecast> daily_forecast) {
        this.daily_forecast = daily_forecast;
    }

    public Now getNow() {
        return now;
    }

    public void setNow(Now now) {
        this.now = now;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Suggestion getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(Suggestion suggestion) {
        this.suggestion = suggestion;
    }

    public List<HourlyForecast> getHourly_forecast() {
        return hourly_forecast;
    }

    public void setHourly_forecast(List<HourlyForecast> hourly_forecast) {
        this.hourly_forecast = hourly_forecast;
    }
}
