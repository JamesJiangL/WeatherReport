package com.james_jiang.weatherreport.AreasWeatherInfo;

/**
 * Created by JC on 2016/8/10.
 */
public class AreasHourlyForecast {
    private int hum;
    private int pop;
    private int pres;
    private int tem;
    private String dir;
    private String sc;
    private int spd;
    public AreasHourlyForecast(int hum, int pop, int pres, int tem, String dir, String sc, int spd){
        this.hum = hum;
        this.pop = pop;
        this.pres = pres;
        this.tem = tem;
        this.dir = dir;
        this.sc = sc;
        this.spd = spd;
    }

    public int getHum() {
        return hum;
    }

    public void setHum(int hum) {
        this.hum = hum;
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

    public int getTem() {
        return tem;
    }

    public void setTem(int tem) {
        this.tem = tem;
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

    public int getSpd() {
        return spd;
    }

    public void setSpd(int spd) {
        this.spd = spd;
    }
}
