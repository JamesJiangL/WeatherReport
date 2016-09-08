package com.james_jiang.weatherreport.ui.weatherInfo;

import com.james_jiang.weatherreport.R;

/**
 * Created by JC on 2016/8/10.
 * get from 和风天气  http://www.heweather.com/documents/condition-code
 */
final public class WeatherCode {
    private WeatherCode(){
        throw new AssertionError();
    }
    public static int getWeatherCode(String weather){
        int weatherCode;
        switch (weather){
            case "晴":
                weatherCode = SUNNY;
                break;
            case "多云":
                weatherCode = CLOUDY;
                break;
            case "少云":
                weatherCode = FEW_CLOUDS;
                break;
            case "晴间多云":
                weatherCode = PARTLY_CLOUDY;
                break;
            case "阴":
                weatherCode = OVERCAST;
                break;
            case "有风":
                weatherCode = WINDY;
                break;
            case "清风":
                weatherCode = FRESH_BREEZE;
                break;
            case "大风":
                weatherCode = GALE;
                break;
            case "飓风":
                weatherCode = HURRICANE;
                break;
            case "小雨":
                weatherCode = LIGHT_RAIN;
                break;
            case "中雨":
                weatherCode = MODERATE_RAIN;
                break;
            case "大雨":
                weatherCode = HEAVY_RAIN;
                break;
            case "阵雨":
                weatherCode = SHOWER_RAIN;
                break;
            case "雷阵雨伴有冰雹":
                weatherCode = HAIL;
                break;
            case "冻雨":
                weatherCode = FREEZING_RAIN;
                break;
            case "浮尘":
                weatherCode = DUST;
                break;
            case "小雪":
                weatherCode = LIGHT_SNOW;
                break;
            case "中雪":
                weatherCode = MODERATE_SNOW;
                break;
            case "大雪":
                weatherCode = HEAVY_SNOW;
                break;
            case "雨雪天气":
                weatherCode = RAIN_AND_SNOW;
                break;
            case "雨夹雪":
                weatherCode = SLEET;
                break;
            case "霾":
                weatherCode = HAZE;
                break;
            case "雾":
                weatherCode = FOGGY;
                break;
            case "阵雪":
                weatherCode = SNOW_FLURRY;
                break;
            case "雷阵雨":
                weatherCode = THUNDER_SHOWER;
                break;
            case "强雷阵雨":
                weatherCode = HEAVY_THUNDER_STORM;
                break;
            default:
                weatherCode = UNKNOWN;
                break;
        }
        return weatherCode;
    }
    private final static int SUNNY = R.drawable.sunny;         //100;        //晴
    private final static int CLOUDY = R.drawable.cloudy;     //101;       //多云
    private final static int FEW_CLOUDS = R.drawable.few_clouds;        //102;   //少云
    private final static int PARTLY_CLOUDY = R.drawable.partly_cloudy;       //103;    //晴转多云
    private final static int OVERCAST = R.drawable.over_cast;        //104;     //阴
    private final static int WINDY = R.drawable.windy;                   //200;        //有风
    public final static int CALM = 201;         //平静
    public final static int LIGHT_BREEZE = 202; //微风
    public final static int GENTLE_BREEZE = 203;    //和风
    private final static int FRESH_BREEZE = R.drawable.fresh_breeze;         //204;     //清风
    public final static int STRONG_BREEZE = 205;    //强风
    public final static int HIGH_WIND = 206;        //疾风
    private final static int GALE = R.drawable.tornado;                 //207;             //大风
    public final static int STRONG_GALE = 208;      //烈风
    public final static int STORM = 209;            //风暴
    public final static int VIOLENT_STORM = 210;    //狂风暴
    private final static int HURRICANE = R.drawable.hurricane;            //211;        //飓风
    public final static int TROPICAL_STORM = 212;   //热带风暴
    private final static int SHOWER_RAIN = R.drawable.shower_rain;               //300;      //阵雨
    public final static int HEAVY_SHOWER_RAIN = 301;    //强阵雨
    private final static int THUNDER_SHOWER = R.drawable.thunder_shower;             //302;       //雷阵雨
    private final static int HEAVY_THUNDER_STORM = R.drawable.heavy_thunder_shower;      //303;  //强雷阵雨
    private final static int HAIL = R.drawable.hail;             //304;             //雷阵雨伴有冰雹
    private final static int LIGHT_RAIN = R.drawable.light_rain;             //305;       //小雨
    private final static int MODERATE_RAIN = R.drawable.moderate_rain;           //306;   //中雨
    private final static int HEAVY_RAIN = R.drawable.heavy_rain;            //307;       //大雨
    public final static int EXTREME_RAIN = 308;     //极端降雨
    public final static int DRIZZLE_RAIN = 309;     //毛毛雨
    public final static int STORM_RAIN = 310;       //暴雨
    public final static int HEAVY_STORM_RAIN = 311;     //大暴雨
    public final static int SEVERE_STORM_RAIN = 312;    //特大暴雨
    private final static int FREEZING_RAIN = R.drawable.freezing_rain;           //313;        //冻雨
    private final static int LIGHT_SNOW = R.drawable.light_snow;             //400;           //小雪
    private final static int MODERATE_SNOW = R.drawable.moderate_snow;           //401;        //中雪
    private final static int HEAVY_SNOW = R.drawable.heavy_snow;                 //402;           //大雪
    public final static int SNOW_STORM = 403;           //暴雪
    private final static int SLEET = R.drawable.sleet;                  //404;                //雨夹雪
    private final static int RAIN_AND_SNOW = R.drawable.rain_and_snow;           //405;        //雨雪天气
    public final static int SHOWER_SNOW = 406;          //阵雨夹雪
    private final static int SNOW_FLURRY = R.drawable.flurry;                    //407;          //阵雪
    public final static int MIST = 500;                 //薄雾
    private final static int FOGGY = R.drawable.fog;                 //501;                //雾
    private final static int HAZE = R.drawable.haze;             //502;                 //霾
    public final static int SAND = 503;                 //扬沙
    private final static int DUST = R.drawable.dust;         //504;                 //浮尘
    public final static int VOLCANIC_ASH = 506;         //火山灰
    public final static int DUST_STORM = 507;           //沙尘暴
    public final static int SANDSTORM = 508;            //强沙尘暴
    public final static int HOT = 900;                  //热
    public final static int COLD = 901;                 //冷
    private final static int UNKNOWN = R.drawable.unknown;                   //999;              //未知
}
