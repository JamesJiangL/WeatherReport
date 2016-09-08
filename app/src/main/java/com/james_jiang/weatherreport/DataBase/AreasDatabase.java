package com.james_jiang.weatherreport.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.james_jiang.weatherreport.Utils.Http.HtmlParseListener;
import com.james_jiang.weatherreport.Utils.Http.HtmlUtil;
import com.james_jiang.weatherreport.Utils.Http.HttpCallBackListener;
import com.james_jiang.weatherreport.Utils.Http.HttpUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JC on 2016/8/9.
 */
public class AreasDatabase {
    private final static String TAG = "CountryAreasDatabase";
    private final static String DB_NAME = "weather_report.db";
    private final static int DB_VERSION = 1;
    private static AreasDatabase weatherDB;
    private SQLiteDatabase db;
    //私有化构造函数
    private AreasDatabase(Context context){
        AreasDatabaseHelper dbHelper = new AreasDatabaseHelper(context,
                DB_NAME, null, DB_VERSION);
        db = dbHelper.getWritableDatabase();
    }
    //得到 CountryAreasDatabase 单个实例
    public synchronized static AreasDatabase getInstance(Context context){
        if(weatherDB == null)
            weatherDB = new AreasDatabase(context);
        return weatherDB;
    }
    //将从网页抓取的数据存入数据库
    public void saveAreasInfo(Areas areas){
        if(areas != null){
            ContentValues values = new ContentValues();
            values.put("area_id", areas.getAreaId());
            values.put("area_name_en", areas.getAreaNameEN());
            values.put("area_name_ch", areas.getAreaNameCH());
            values.put("city", areas.getCity());
            values.put("province", areas.getProvince());
            db.insert("areasInfo", null, values);
        }else {
            Log.e(TAG, "areas is null !!!");
        }
    }
    //从数据库中读取地区信息
    public List<Areas> loadAreasInfo(){
        List<Areas> areasList = new ArrayList<>();
        Cursor cursor = db.query("areasInfo", null, null, null, null, null, null);
        if(cursor.moveToFirst()){
            do{
                Areas areas = new Areas();
                areas.setAreaId(cursor.getString(cursor.getColumnIndex("area_id")));
                areas.setAreaNameEN(cursor.getString(cursor.getColumnIndex("area_name_en")));
                areas.setAreaNameCH(cursor.getString(cursor.getColumnIndex("area_name_ch")));
                areas.setCity(cursor.getString(cursor.getColumnIndex("city")));
                areas.setProvince(cursor.getString(cursor.getColumnIndex("province")));
                areasList.add(areas);
            }while (cursor.moveToNext());
        }else {
            Log.e(TAG, "DataBase is empty !!!");
        }
        if(cursor != null)
            cursor.close();
        return areasList;
    }
    public SQLiteDatabase getDb(){
        return db;
    }
    public String getAreasId(String area_ch_name){
//        long t1 = SystemClock.currentThreadTimeMillis();
        String id;
        if(area_ch_name.contains("区")){
            int index = area_ch_name.indexOf("区");
            area_ch_name = area_ch_name.substring(0, index);
        }else if(area_ch_name.contains("市")){
            int index = area_ch_name.indexOf("市");
            area_ch_name = area_ch_name.substring(0, index);
        }
        Cursor cursor = db.query("areasInfo", new String[]{"area_id", "area_name_ch"},
                "area_name_ch LIKE ?", new String[]{"%" + area_ch_name + "%"}, null, null, null);
        if(cursor != null && cursor.getCount() != 0){
            cursor.moveToFirst();
            id = cursor.getString(cursor.getColumnIndex("area_id"));
            Log.e(TAG, "name_en = " + id);
            cursor.close();
            Log.e(TAG, "查到了数据");
//            long t2 = SystemClock.currentThreadTimeMillis();
//            Log.e(TAG, "时间差 ：" + (t2 - t1));
        }else {
            Log.e(TAG, "没有查询到数据");
            return null;
        }
        if (cursor != null) {
            cursor.close();
        }
        return id;
    }
    public String getCityName(String cityId){
        String cityName;
        Cursor cursor = db.query("areasInfo", new String[]{"area_id", "area_name_ch"},
                "area_id=?", new String[]{cityId}, null, null, null);
        if(cursor != null && cursor.getCount() != 0){
            cursor.moveToFirst();
            cityName = cursor.getString(cursor.getColumnIndex("area_name_ch"));
        }else {
            Log.e(TAG, "没有查询到数据");
            return null;
        }
        if (cursor != null) {
            cursor.close();
        }
        return cityName;
    }
    //查询城市列表
    public List<Areas> loadAreasInfo(String name){
        List<Areas> list = new ArrayList<>();
        Cursor cursor = db.query("areasInfo", null,
                "area_name_ch LIKE ?", new String[]{"%" + name + "%"}, null, null, null);
        if(cursor.moveToFirst()){
            do{
                Areas areas = new Areas();
                areas.setAreaId(cursor.getString(cursor.getColumnIndex("area_id")));
                areas.setAreaNameEN(cursor.getString(cursor.getColumnIndex("area_name_en")));
                areas.setAreaNameCH(cursor.getString(cursor.getColumnIndex("area_name_ch")));
                areas.setCity(cursor.getString(cursor.getColumnIndex("city")));
                areas.setProvince(cursor.getString(cursor.getColumnIndex("province")));
                list.add(areas);
            }while (cursor.moveToNext());
        }else {
            Log.e(TAG, "没有查询到数据");
            return null;
        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }
    //查询地区表是否为空
    public boolean isDataBaseEmpty(){
        Cursor cursor = db.query("areasInfo", null, null, null, null, null, null);
        if(cursor != null && cursor.getCount() != 0){
            Log.e(TAG, "数据库表不为空");
            cursor.close();
            return false;
        }else {
            Log.e(TAG, "数据库表为空");
            if (cursor != null) {
                cursor.close();
            }
            return true;
        }
    }
    //抓取网络城市列表，然后写入数据库，不需要使用了
    public void getDataWriteDB(){
        HttpUtil.sendHttpRequest("http://www.heweather.com/documents/cn-city-list",
                new HttpCallBackListener() {
                    @Override
                    public void onFinish(String response) {
                        Log.e(TAG, "成功反馈数据");
                        HtmlUtil.parseHtmlWithJSoup(response, new HtmlParseListener() {
                            @Override
                            public void onFinish(List<Areas> areasList) {
                                int size = areasList.size();
                                Log.e(TAG, "size = " + size);
                                for(int i = 0; i < size; i++){
                                    saveAreasInfo(areasList.get(i));
                                }
                            }
                            @Override
                            public void onError(Exception e) {
                                Log.e(TAG, "解析数据出错: " + e.getMessage());
                            }
                        });
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e(TAG, "未得到反馈数据: " + e.getMessage());
                    }
                });
    }
}
