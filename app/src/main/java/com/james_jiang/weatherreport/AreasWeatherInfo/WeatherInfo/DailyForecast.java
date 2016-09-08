package com.james_jiang.weatherreport.AreasWeatherInfo.WeatherInfo;

import java.io.Serializable;

/**
 * Created by JC on 2016/8/13.
 */
public class DailyForecast implements Serializable{
    private static final long serialVersionUID = 7464028478834360520L;
    //************************astro***********************//
    private Astro astro;
    public Astro getAstro() {
        return astro;
    }
    public void setAstro(Astro astro) {
        this.astro = astro;
    }

    public class Astro implements Serializable{
        private static final long serialVersionUID = 3202552578744150118L;
        private String sr;
        private String ss;

        public String getSr() {
            return sr;
        }

        public void setSr(String sr) {
            this.sr = sr;
        }

        public String getSs() {
            return ss;
        }

        public void setSs(String ss) {
            this.ss = ss;
        }
    }
    //************************astro***********************//

    //************************cond***********************//
    private Cond cond;
    public Cond getCond() {
        return cond;
    }

    public void setCond(Cond cond) {
        this.cond = cond;
    }
    public class Cond implements Serializable{
        private static final long serialVersionUID = 390922379347576412L;
        private transient int code_d;
        private transient int code_n;
        private String txt_d;
        private String txt_n;

        public void setCode_d(int code_d) {
            this.code_d = code_d;
        }

        public void setCode_n(int code_n) {
            this.code_n = code_n;
        }

        public String getTxt_d() {
            return txt_d;
        }

        public void setTxt_d(String txt_d) {
            this.txt_d = txt_d;
        }

        public String getTxt_n() {
            return txt_n;
        }

        public void setTxt_n(String txt_n) {
            this.txt_n = txt_n;
        }
    }
    //************************cond***********************//

    //************************单个***********************//
    private String date;
    private transient int hum;
//    private transient float pcpn;
    private transient int pop;
    private transient int pres;
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setHum(int hum) {
        this.hum = hum;
    }

//    public void set(float pcpn) {
//        this.pcpn = pcpn;
//    }

    public void setPop(int pop) {
        this.pop = pop;
    }

    public void setPres(int pres) {
        this.pres = pres;
    }
    //************************单个***********************//

    //************************tmp***********************//
    private Tmp tmp;
    public Tmp getTmp() {
        return tmp;
    }

    public void setTmp(Tmp tmp) {
        this.tmp = tmp;
    }
    public class Tmp implements Serializable{
        private static final long serialVersionUID = 6298163846973980443L;
        private int max;
        private int min;

        public int getMin() {
            return min;
        }

        public void setMin(int min) {
            this.min = min;
        }

        public int getMax() {
            return max;
        }

        public void setMax(int max) {
            this.max = max;
        }
    }
    //************************tmp***********************//

    private transient int vis;

    private transient Wind wind;

    public class Wind implements Serializable{
        private static final long serialVersionUID = 5334361739216318848L;
        private transient int deg;
        private transient String dir;
        private transient String sc;
        private transient int spd;
    }
}
