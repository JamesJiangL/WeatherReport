package com.james_jiang.weatherreport.AreasWeatherInfo.WeatherInfo;

import java.io.Serializable;

/**
 * Created by JC on 2016/8/13.
 */
public class Suggestion implements Serializable{
    private static final long serialVersionUID = -7872602049659926852L;
    private Comf comf;
    private Cw cw;
    private Drsg drsg;
    private Flu flu;
    private Sport sport;
    private Trav trav;
    private Uv uv;

    public Comf getComf() {
        return comf;
    }

    public void setComf(Comf comf) {
        this.comf = comf;
    }

    public Cw getCw() {
        return cw;
    }

    public void setCw(Cw cw) {
        this.cw = cw;
    }

    public Drsg getDrsg() {
        return drsg;
    }

    public void setDrsg(Drsg drsg) {
        this.drsg = drsg;
    }

    public Flu getFlu() {
        return flu;
    }

    public void setFlu(Flu flu) {
        this.flu = flu;
    }

    public Sport getSport() {
        return sport;
    }

    public void setSport(Sport sport) {
        this.sport = sport;
    }

    public Uv getUv() {
        return uv;
    }

    public void setUv(Uv uv) {
        this.uv = uv;
    }

    public Trav getTrav() {
        return trav;
    }

    public void setTrav(Trav trav) {
        this.trav = trav;
    }

    public class Comf implements Serializable{
        private static final long serialVersionUID = -5609663973337285194L;
        private String brf;
        private transient String txt;

        public String getBrf() {
            return brf;
        }

        public void setBrf(String brf) {
            this.brf = brf;
        }
    }
    public class Cw implements Serializable{
        private static final long serialVersionUID = -60797547702734503L;
        private String brf;
        private transient String txt;

        public String getBrf() {
            return brf;
        }

        public void setBrf(String brf) {
            this.brf = brf;
        }
    }
    public class Drsg implements Serializable{
        private static final long serialVersionUID = -8666125754581062510L;
        private String brf;
        private transient String txt;

        public String getBrf() {
            return brf;
        }

        public void setBrf(String brf) {
            this.brf = brf;
        }
    }
    public class Flu implements Serializable{
        private static final long serialVersionUID = -438933199890123155L;
        private String brf;
        private transient String txt;

        public String getBrf() {
            return brf;
        }

        public void setBrf(String brf) {
            this.brf = brf;
        }
    }
    public class Sport implements Serializable{
        private static final long serialVersionUID = 4315705791222589067L;
        private String brf;
        private transient String txt;

        public String getBrf() {
            return brf;
        }

        public void setBrf(String brf) {
            this.brf = brf;
        }
    }
    public class Trav implements Serializable{
        private static final long serialVersionUID = 7075695656947756471L;
        private String brf;
        private transient String txt;

        public String getBrf() {
            return brf;
        }

        public void setBrf(String brf) {
            this.brf = brf;
        }
    }
    public class Uv implements Serializable{
        private static final long serialVersionUID = -9188291026324520300L;
        private String brf;
        private transient String txt;

        public String getBrf() {
            return brf;
        }

        public void setBrf(String brf) {
            this.brf = brf;
        }
    }
}
