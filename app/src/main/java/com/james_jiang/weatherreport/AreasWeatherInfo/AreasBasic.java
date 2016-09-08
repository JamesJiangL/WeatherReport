package com.james_jiang.weatherreport.AreasWeatherInfo;

/**
 * Created by JC on 2016/8/10.
 */
public class AreasBasic {
    private String city;    //城市名
    private String cnty;    //国家
    private String id;      //id
    private float lat;      //纬度
    private float lon;      //经度
    private String locDate; //当地日期
    private String utcDate; //国际时间
    public AreasBasic(String city, String cnty, String id, float lat,
                      float lon, String locDate, String utcDate){
        this.city = city;
        this.cnty = cnty;
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.locDate = locDate;
        this.utcDate = utcDate;
    }
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCnty() {
        return cnty;
    }

    public void setCnty(String cnty) {
        this.cnty = cnty;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLon() {
        return lon;
    }

    public void setLon(float lon) {
        this.lon = lon;
    }

    public String getLocDate() {
        return locDate;
    }

    public void setLocDate(String locDate) {
        this.locDate = locDate;
    }

    public String getUtcDate() {
        return utcDate;
    }

    public void setUtcDate(String utcDate) {
        this.utcDate = utcDate;
    }
}
