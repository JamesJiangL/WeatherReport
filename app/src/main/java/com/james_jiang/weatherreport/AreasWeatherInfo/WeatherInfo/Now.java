package com.james_jiang.weatherreport.AreasWeatherInfo.WeatherInfo;

import java.io.Serializable;

/**
 * Created by JC on 2016/8/13.
 */
public class Now implements Serializable{
    private static final long serialVersionUID = -6553914019193208294L;
    private Cond cond;
    private int fl;
    private int hum;
//    private float pcpn;
    private int pres;
    private int tmp;
    private int vis;
    private Wind wind;

    public Cond getCond() {
        return cond;
    }

    public void setCond(Cond cond) {
        this.cond = cond;
    }

    public int getFl() {
        return fl;
    }

    public void setFl(int fl) {
        this.fl = fl;
    }

    public int getHum() {
        return hum;
    }

    public void setHum(int hum) {
        this.hum = hum;
    }

//    public float getPcpn() {
//        return pcpn;
//    }

//    public void setPcpn(float pcpn) {
//        this.pcpn = pcpn;
//    }

    public int getPres() {
        return pres;
    }

    public void setPres(int pres) {
        this.pres = pres;
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

    public Wind getWind() {
        return wind;
    }

    public void setWind(Wind wind) {
        this.wind = wind;
    }

    public static class Cond implements Serializable{
        private static final long serialVersionUID = -5922969266655965739L;
        private transient int code;
        private String txt;

        public String getTxt() {
            return txt;
        }

        public void setTxt(String txt) {
            this.txt = txt;
        }
    }

    public static class Wind implements Serializable{
        private static final long serialVersionUID = 9123321617561019364L;
        private transient int deg;
        private String dir;
        private String sc;
        private int spd;

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
}
