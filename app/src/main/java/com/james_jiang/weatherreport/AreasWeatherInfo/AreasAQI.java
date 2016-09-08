package com.james_jiang.weatherreport.AreasWeatherInfo;

/**
 * Created by JC on 2016/8/10.
 */
public class AreasAQI {
    private int aqi;
    private int co;
    private int no2;
    private int o3;
    private int so2;
    private int pm10;
    private int pm25;
    private String qlty;
    public AreasAQI(int aqi, int co, int no2, int o3, int so2, int pm10, int pm25, String qlty){
        this.aqi = aqi;
        this.co = co;
        this.no2 = no2;
        this.o3 = o3;
        this.so2 = so2;
        this.pm10 = pm10;
        this.pm25 = pm25;
        this.qlty = qlty;
    }
    public void setAqi(int aqi){
        this.aqi = aqi;
    }
    public void setCo(int co){
        this.co = co;
    }
    public void setNo2(int no2){
        this.no2 = no2;
    }
    public void setO3(int o3){
        this.o3 = o3;
    }
    public void setSo2(int so2){
        this.so2 = so2;
    }
    public void setPm10(int pm10){
        this.pm10 = pm10;
    }
    public void setPm25(int pm25){
        this.pm25 = pm25;
    }
    public void setQlty(String qlty){
        this.qlty = qlty;
    }
    public int getAqi(){
        return aqi;
    }
    public int getCo(){
        return co;
    }
    public int getNo2(){
        return no2;
    }
    public int getO3(){
        return o3;
    }
    public int getSo2(){
        return so2;
    }
    public int getPm10(){
        return pm10;
    }
    public int getPm25(){
        return pm25;
    }
    public String getQlty(){
        return qlty;
    }
}
