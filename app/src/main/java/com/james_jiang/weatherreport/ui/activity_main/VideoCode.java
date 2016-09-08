package com.james_jiang.weatherreport.ui.activity_main;

import com.james_jiang.weatherreport.AreasWeatherInfo.WeatherInfo.DailyForecast;
import com.james_jiang.weatherreport.AreasWeatherInfo.WeatherInfo.WeatherInfo;
import com.james_jiang.weatherreport.R;

import java.util.Date;

/**
 * Created by JC on 2016/8/11.
 */
final public class VideoCode {
    private final static String TAG = "VideoCode";
    private VideoCode(){
        throw new AssertionError();
    }
    public static int getWeatherCode(WeatherInfo weatherInfo){
        int weatherCode;
        DailyForecast dailyForecast = weatherInfo.getDaily_forecast().get(0);
        Date date = new Date();
        String[] sunrise = dailyForecast.getAstro().getSr().split(":");
        int sunRiseHour = Integer.parseInt(sunrise[0]);
        int sunRiseMinute = Integer.parseInt(sunrise[1]);
        String[] sunset = dailyForecast.getAstro().getSs().split(":");
        int sunSetHour = Integer.parseInt(sunset[0]);
        int sunSetMinute = Integer.parseInt(sunset[1]);
        if(((date.getHours() * 60 + date.getMinutes()) > (sunRiseHour * 60 + sunRiseMinute)) &&
                ((date.getHours() * 60 + date.getMinutes()) < (sunSetHour * 60 + sunSetMinute))){
            switch (weatherInfo.getNow().getCond().getTxt()){
                case "多云":
                    weatherCode = CLOUDY;
                    break;
                case "少云":
                case "晴间多云":
                    weatherCode = PARTLY_SUNNY;
                    break;
                case "阴":
                case "雾":
                case "浮尘":
                case "霾":
                    weatherCode = FOG;
                    break;
                case "晴":
                    weatherCode = SUNNY;
                    break;
                case "小雨":
                case "阵雨":
                case "中雨":
                case "大雨":
                case "冻雨":
                    weatherCode = RAIN;
                    break;
                case "雷阵雨伴有冰雹":
                case "雷阵雨":
                case "强雷阵雨":
                    weatherCode = THUNDER_STORM;
                    break;
                case "小雪":
                case "中雪":
                case "大雪":
                case "雨雪天气":
                case "雨夹雪":
                case "阵雪":
                    weatherCode = SNOW;
                    break;
                default:
                    weatherCode = SUNNY;
                    break;
            }
        }else {
            switch (weatherInfo.getNow().getCond().getTxt()){
                case "多云":
                    weatherCode = CLOUDY_N;
                    break;
                case "少云":
                case "晴间多云":
                    weatherCode = PARTLY_SUNNY_N;
                    break;
                case "阴":
                case "雾":
                case "浮尘":
                case "霾":
                    weatherCode = FOG_N;
                    break;
                case "晴":
                    weatherCode = SUNNY_N;
                    break;
                case "小雨":
                case "阵雨":
                case "中雨":
                case "大雨":
                case "冻雨":
                    weatherCode = RAIN_N;
                    break;
                case "雷阵雨伴有冰雹":
                case "雷阵雨":
                case "强雷阵雨":
                    weatherCode = THUNDER_STORM_N;
                    break;
                case "小雪":
                case "中雪":
                case "大雪":
                case "雨雪天气":
                case "雨夹雪":
                case "阵雪":
                    weatherCode = SNOW_N;
                    break;
                default:
                    weatherCode = SUNNY_N;
                    break;
            }
        }
        return weatherCode;
    }
    private final static int RAIN = R.raw.weather_2nd_bg_rain;
    private final static int RAIN_N = R.raw.weather_2nd_bg_rain_n;
    private final static int SUNNY = R.raw.weather_2nd_bg_sunny;
    private final static int SUNNY_N = R.raw.weather_2nd_bg_clear;
    private final static int CLOUDY = R.raw.weather_2nd_bg_cloudy;
    private final static int CLOUDY_N = R.raw.weather_2nd_bg_cloudy_n;
    private final static int PARTLY_SUNNY = R.raw.weather_2nd_bg_partly_sunny;
    private final static int PARTLY_SUNNY_N = R.raw.weather_2nd_bg_mostly_clear;
//    private final static int COLD = R.raw.weather_2nd_bg_cold;
//    private final static int HOT = R.raw.weather_2nd_bg_hot;
    private final static int FOG = R.raw.weather_2nd_bg_fog;
    private final static int FOG_N = R.raw.weather_2nd_bg_fog_n;
    private final static int SNOW = R.raw.weather_2nd_bg_snow;
    private final static int SNOW_N = R.raw.weather_2nd_bg_snow_n;
    private final static int THUNDER_STORM = R.raw.weather_2nd_bg_thunderstorms;
    private final static int THUNDER_STORM_N = R.raw.weather_2nd_bg_thunderstorms_n;
}
