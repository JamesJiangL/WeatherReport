package com.james_jiang.weatherreport.AreasWeatherInfo;

/**
 * Created by JC on 2016/8/10.
 */
public class AreasWeatherInfo {
    private AreasAQI aqi;
    private AreasDailyForecast[] daily_forecast;
    private AreasHourlyForecast hourlyForecast;
    private AreasNow areasNow;
    private AreasSuggestions suggestions;

//    public AreasWeatherInfo(AreasAQI aqi, AreasDailyForecast[] daily_forecast,
//                     AreasHourlyForecast hourlyForecast, AreasNow areasNow,
//                     AreasSuggestions suggestions){
//        this.aqi = aqi;
//        this.daily_forecast = daily_forecast;
//        this.hourlyForecast = hourlyForecast;
//        this.areasNow = areasNow;
//        this.suggestions = suggestions;
//    }

    public AreasAQI getAqi() {
        return aqi;
    }

    public void setAqi(AreasAQI aqi) {
        this.aqi = aqi;
    }

    public AreasDailyForecast[] getDaily_forecast() {
        return daily_forecast;
    }

    public void setDaily_forecast(AreasDailyForecast[] daily_forecast) {
        this.daily_forecast = daily_forecast;
    }

    public AreasHourlyForecast getHourlyForecast() {
        return hourlyForecast;
    }

    public void setHourlyForecast(AreasHourlyForecast hourlyForecast) {
        this.hourlyForecast = hourlyForecast;
    }

    public AreasNow getAreasNow() {
        return areasNow;
    }

    public void setAreasNow(AreasNow areasNow) {
        this.areasNow = areasNow;
    }

    public AreasSuggestions getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(AreasSuggestions suggestions) {
        this.suggestions = suggestions;
    }
}
