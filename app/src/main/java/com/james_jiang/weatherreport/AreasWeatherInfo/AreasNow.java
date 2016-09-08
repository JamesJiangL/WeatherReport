package com.james_jiang.weatherreport.AreasWeatherInfo;

/**
 * Created by JC on 2016/8/10.
 */
public class AreasNow {
    private String cond;        //当前天气情况
    private int fl;             //体感温度
    private int tmp;
    private int vis;
    private String dir;
    private String sc;
    private int spd;

    public AreasNow(String cond, int fl, int tmp, int vis, String dir, String sc, int spd){
        this.cond = cond;
        this.fl = fl;
        this.tmp = tmp;
        this.vis = vis;
        this.dir = dir;
        this.sc = sc;
        this.spd = spd;
    }
    public String getCond() {
        return cond;
    }

    public void setCond(String cond) {
        this.cond = cond;
    }

    public int getFl() {
        return fl;
    }

    public void setFl(int fl) {
        this.fl = fl;
    }

    public int getTmp() {
        return tmp;
    }

    public void setTmp(int tmp) {
        this.tmp = tmp;
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

    public int getSpd() {
        return spd;
    }

    public void setSpd(int spd) {
        this.spd = spd;
    }
}
