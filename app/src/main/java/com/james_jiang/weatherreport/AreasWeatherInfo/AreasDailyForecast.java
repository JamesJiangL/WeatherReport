package com.james_jiang.weatherreport.AreasWeatherInfo;

/**
 * Created by JC on 2016/8/10.
 */
public class AreasDailyForecast {
    private String date;            //预报日期
    private String sunRise;         //日出时间
    private String sunSet;          //日落时间
    private String cond;            //白条天气情况
    private String conn;            //夜晚天气情况
    private int hum;                //相对湿度
    private float pcpn;             //降水量
    private int pop;              //降水概率
    private int pres;               //大气压
    private int max;                //最高温度
    private int min;                //最低温度
    private int vis;                //能见度 km
    private String dir;                //风向
    private String sc;               //风力
    private String spd;             //风速 kmph

    public AreasDailyForecast(String date, String sunRise, String sunSet,
                              String cond, String conn, int max, int min){
        this.date = date;
        this.sunRise = sunRise;
        this.sunSet = sunSet;
        this.cond = cond;
        this.conn = conn;
        this.max = max;
        this.min = min;
    }
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSunRise() {
        return sunRise;
    }

    public void setSunRise(String sunRise) {
        this.sunRise = sunRise;
    }

    public String getSunSet() {
        return sunSet;
    }

    public void setSunSet(String sunSet) {
        this.sunSet = sunSet;
    }

    public String getCond() {
        return cond;
    }

    public void setCond(String cond) {
        this.cond = cond;
    }

    public String getConn() {
        return conn;
    }

    public void setConn(String conn) {
        this.conn = conn;
    }

    public int getHum() {
        return hum;
    }

    public void setHum(int hum) {
        this.hum = hum;
    }

    public float getPcpn() {
        return pcpn;
    }

    public void setPcpn(float pcpn) {
        this.pcpn = pcpn;
    }

    public int getPop() {
        return pop;
    }

    public void setPop(int pop) {
        this.pop = pop;
    }

    public int getPres() {
        return pres;
    }

    public void setPres(int pres) {
        this.pres = pres;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getVis() {
        return vis;
    }

    public void setVis(int vis) {
        this.vis = vis;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getSc() {
        return sc;
    }

    public void setSc(String sc) {
        this.sc = sc;
    }

    public String getSpd() {
        return spd;
    }

    public void setSpd(String spd) {
        this.spd = spd;
    }
}
