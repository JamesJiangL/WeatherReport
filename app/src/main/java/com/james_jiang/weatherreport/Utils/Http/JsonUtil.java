package com.james_jiang.weatherreport.Utils.Http;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.james_jiang.weatherreport.AreasWeatherInfo.AreasAQI;
import com.james_jiang.weatherreport.AreasWeatherInfo.AreasDailyForecast;
import com.james_jiang.weatherreport.AreasWeatherInfo.AreasHourlyForecast;
import com.james_jiang.weatherreport.AreasWeatherInfo.AreasNow;
import com.james_jiang.weatherreport.AreasWeatherInfo.AreasSuggestions;
import com.james_jiang.weatherreport.AreasWeatherInfo.AreasWeatherInfo;
import com.james_jiang.weatherreport.AreasWeatherInfo.WeatherInfo.WeatherInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;


/**
 * Created by JC on 2016/8/13.
 */
public class JsonUtil {
    private final static String TAG = "JsonUtil";
    //使用JSONObject解析json
    public static AreasWeatherInfo parseJsonWithJSONObject(String jsonData){
        try{
            JSONObject jsonObject = new JSONObject(jsonData);
            AreasWeatherInfo areasWeatherInfo = new AreasWeatherInfo();
            if(jsonObject.has("HeWeather data service 3.0")) {
//                Log.e(TAG, "包含" + jsonData);
                JSONArray jsonArray = jsonObject.getJSONArray("HeWeather data service 3.0");
                jsonObject = jsonArray.getJSONObject(0);
                if(jsonObject.has("aqi")) {     //解析aqi空气质量
                    JSONObject aqi = jsonObject.getJSONObject("aqi");
                    JSONObject city = aqi.getJSONObject("city");
                    int aq = 0;
                    int co = 0;
                    int no2 = 0;
                    int o3 = 0;
                    int so2 = 0;
                    int pm10 = 0;
                    int pm25 = 0;
                    String qlty = "";
                    if(city.has("aqi")) {
                        aq = city.getInt("aqi");
                    }
                    if(city.has("co")) {
                        co = city.getInt("co");
                    }
                    if(city.has("no2")) {
                        no2 = city.getInt("no2");
                    }
                    if(city.has("o3")) {
                        o3 = city.getInt("o3");
                    }
                    if(city.has("so2")) {
                        so2 = city.getInt("so2");
                    }
                    if(city.has("pm10")) {
                        pm10 = city.getInt("pm10");
                    }
                    if(city.has("pm25")) {
                        pm25 = city.getInt("pm25");
                    }
                    if(city.has("qlty")) {
                        qlty = city.getString("qlty");
                    }
                    AreasAQI areasAQI = new AreasAQI(aq, co, no2, o3, so2, pm10, pm25, qlty);
                    areasWeatherInfo.setAqi(areasAQI);
                }
                if(jsonObject.has("daily_forecast")){       //解析未来七天的天气
//                    Log.e(TAG, "包含" + "daily_forecast");
                    JSONArray dailyArray = jsonObject.getJSONArray("daily_forecast");
                    int size = dailyArray.length();
                    AreasDailyForecast[] dailyForecasts = new AreasDailyForecast[size];
                    for(int i = 0; i < size; i++){
                        JSONObject dailyObject = dailyArray.getJSONObject(i);   //得到每一天的数据
                        String date = "";
                        int hum = 0;
                        float pcpn = 0.0f;
                        int pop = 0;
                        int pres = 0;
                        int vis = 0;
                        String sunrise = "";
                        String sunset = "";
                        String cond = "";
                        String conn = "";
                        int max = 0;
                        int min = 0;
                        String dir = "";
                        String sc = "";
                        int spd = 0;
                        //先解析单个
                        if(dailyObject.has("date")){
                            date = dailyObject.getString("date");
//                            System.out.println("date: " + date);
                        }
                        if(dailyObject.has("hum")){
                            hum = dailyObject.getInt("hum");
//                            System.out.println("hum: " + hum);
                        }
                        if(dailyObject.has("pcpn")){
                            pcpn = dailyObject.getInt("pcpn");
//                            System.out.println("pcpn: " + pcpn);
                        }
                        if(dailyObject.has("pop")){
                            pop = dailyObject.getInt("pop");
//                            System.out.println("pop: " + pop);
                        }
                        if(dailyObject.has("pres")){
                            pres = dailyObject.getInt("pres");
//                            System.out.println("pres: " + pres);
                        }
                        if(dailyObject.has("vis")){
                            vis = dailyObject.getInt("vis");
//                            System.out.println("vis: " + vis);
                        }
                        if(dailyObject.has("astro")){       //解析日出日落时间
                            JSONObject sunInfo = dailyObject.getJSONObject("astro");
                            sunrise = sunInfo.getString("sr");
                            sunset = sunInfo.getString("ss");
//                            System.out.println("sunrise: " + sunrise + ", sunset: " + sunset);
                        }
                        if(dailyObject.has("cond")){
                            JSONObject condObject = dailyObject.getJSONObject("cond");
                            cond = condObject.getString("txt_d");
                            conn = condObject.getString("txt_n");
//                            System.out.println("cond: " + cond + ", conn: " + conn);
                        }
                        if(dailyObject.has("tmp")){
                            JSONObject tmpObject = dailyObject.getJSONObject("tmp");
                            max = tmpObject.getInt("max");
                            min = tmpObject.getInt("min");
//                            System.out.println("max: " + max + ", min: " + min);
                        }
                        if(dailyObject.has("wind")){
                            JSONObject windObject = dailyObject.getJSONObject("wind");
                            dir = windObject.getString("dir");
                            sc = windObject.getString("sc");
                            spd = windObject.getInt("spd");
//                            System.out.println("dir: " + dir + ", sc: " + sc + ", spd: " + spd);
                        }
                        dailyForecasts[i] = new AreasDailyForecast(date, sunrise,
                                sunset, cond, conn, max, min);
                    }
                    areasWeatherInfo.setDaily_forecast(dailyForecasts);
                }
                if(jsonObject.has("hourly_forecast")){          //小时预报
//                    Log.e(TAG, "包含" + "hourly_forecast");
                    JSONArray hourlyArray = jsonObject.getJSONArray("hourly_forecast");
                    if(hourlyArray.length() > 0) {
                        JSONObject hourlyObject = hourlyArray.getJSONObject(0);
                        int hum = 0;
                        int pop = 0;
                        int pres = 0;
                        int tmp = 0;
                        String dir = "";
                        String sc = "";
                        int spd = 0;
                        if (hourlyObject.has("hum")) {
                            hum = hourlyObject.getInt("hum");
                        }
                        if (hourlyObject.has("pop")) {
                            pop = hourlyObject.getInt("pop");
                        }
                        if (hourlyObject.has("pres")) {
                            pres = hourlyObject.getInt("pres");
                        }
                        if (hourlyObject.has("tmp")) {
                            tmp = hourlyObject.getInt("tmp");
                        }
                        if (hourlyObject.has("wind")) {
                            JSONObject windObject = hourlyObject.getJSONObject("wind");
                            dir = windObject.getString("dir");
                            sc = windObject.getString("sc");
                            spd = windObject.getInt("spd");
//                        System.out.println("dir: " + dir + ", sc: " + sc + ", spd: " + spd);
                        }
                        AreasHourlyForecast hourlyForecast = new AreasHourlyForecast(hum,
                                pop, pres, tmp, dir, sc, spd);
                        areasWeatherInfo.setHourlyForecast(hourlyForecast);
                    }
                }
                if(jsonObject.has("now")){              //实况天气
//                    Log.e(TAG, "包含" + "now");
                    JSONObject nowObject = jsonObject.getJSONObject("now");
                    String cond = "";
                    int fl = 0;
                    int tmp = 0;
                    int vis = 0;
                    String dir = "";
                    String sc = "";
                    int spd = 0;
                    if(nowObject.has("cond")){
                        JSONObject condObject = nowObject.getJSONObject("cond");
                        cond = condObject.getString("txt");
                    }
                    if(nowObject.has("fl")){
                        fl = nowObject.getInt("fl");
                    }
                    if(nowObject.has("tmp")){
                        tmp = nowObject.getInt("tmp");
                    }
                    if(nowObject.has("vis")){
                        vis = nowObject.getInt("vis");
                    }
                    if(nowObject.has("wind")){
                        JSONObject windObject = nowObject.getJSONObject("wind");
                        dir = windObject.getString("dir");
                        sc = windObject.getString("sc");
                        spd = windObject.getInt("spd");
//                        System.out.println("dir: " + dir + ", sc: " + sc + ", spd: " + spd);
                    }
                    AreasNow areasNow = new AreasNow(cond, fl, tmp, vis, dir, sc, spd);
                    areasWeatherInfo.setAreasNow(areasNow);
                }
                if(jsonObject.has("suggestion")){       //生活指数
                    JSONObject suggestionObject = jsonObject.getJSONObject("suggestion");
                    String comfBrf = "";
                    String cwBrf = "";
                    String drsgBrf = "";     //穿衣指数
                    String fluBrf = "";      //感冒指数
                    String sportBrf = "";    //运动指数
                    String travBrf = "";     //旅游指数
                    String uvBrf = "";       //紫外线指数
                    if(suggestionObject.has("comf")){
                        JSONObject comfObject = suggestionObject.getJSONObject("comf");
                        comfBrf = comfObject.getString("brf");
                    }
                    if(suggestionObject.has("cw")){
                        JSONObject cwObject = suggestionObject.getJSONObject("cw");
                        cwBrf = cwObject.getString("brf");
                    }
                    if(suggestionObject.has("drsg")){
                        JSONObject drsgObject = suggestionObject.getJSONObject("drsg");
                        drsgBrf = drsgObject.getString("brf");
                    }
                    if(suggestionObject.has("flu")){
                        JSONObject fluObject = suggestionObject.getJSONObject("flu");
                        fluBrf = fluObject.getString("brf");
                    }
                    if(suggestionObject.has("sport")){
                        JSONObject sportObject = suggestionObject.getJSONObject("sport");
                        sportBrf = sportObject.getString("brf");
                    }
                    if(suggestionObject.has("trav")){
                        JSONObject travObject = suggestionObject.getJSONObject("trav");
                        travBrf = travObject.getString("brf");
                    }
                    if(suggestionObject.has("uv")){
                        JSONObject uvObject = suggestionObject.getJSONObject("uv");
                        uvBrf = uvObject.getString("brf");
                    }
                    AreasSuggestions suggestions = new AreasSuggestions(comfBrf, cwBrf,
                            drsgBrf, fluBrf, sportBrf, travBrf, uvBrf);
                    areasWeatherInfo.setSuggestions(suggestions);
                }
                return areasWeatherInfo;
            }else {
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    //使用Google提供的开源库GSON解析json
    public static WeatherInfo parseJsonWithGSON(String jsonData){
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonObject object = parser.parse(jsonData).getAsJsonObject();
        JsonArray array = object.getAsJsonArray("HeWeather data service 3.0");
        Log.e(TAG, "array.size() : " + array.size());
        return gson.fromJson(array.get(0), WeatherInfo.class);
    }
    //使用FastJson解析json, 不知道为什么会产生nullPoint
    public static WeatherInfo parseJsonWithFastJson(String jsonData){
        if(jsonData == null)
            return null;
        com.alibaba.fastjson.JSONObject object = JSON.parseObject(jsonData);
        com.alibaba.fastjson.JSONArray array = object.getJSONArray("HeWeather data service 3.0");
//        Log.e(TAG, "array.size() : " + array.size());
        List<WeatherInfo> weatherInfo = JSON.parseArray(array.toString(), WeatherInfo.class);
        return weatherInfo.get(0);
    }
}
