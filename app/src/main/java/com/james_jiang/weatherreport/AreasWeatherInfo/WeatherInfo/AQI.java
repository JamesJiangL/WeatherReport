package com.james_jiang.weatherreport.AreasWeatherInfo.WeatherInfo;

import java.io.Serializable;

/**
 * Created by JC on 2016/8/13.
 */
public class AQI implements Serializable{
    private static final long serialVersionUID = -5799546019627459932L;
    private City city;

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public class City implements Serializable{
        private static final long serialVersionUID = -9136277912772404667L;
        private int aqi;
        private transient int co;
        private transient int no2;
        private transient int o3;
        private transient int so2;
        private int pm10;
        private int pm25;
        private String qlty;
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
    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder.append("aqi: ");
        builder.append(String.valueOf(city.aqi));
        builder.append(", pm10: ");
        builder.append(String.valueOf(city.pm10));
        builder.append(", pm25: ");
        builder.append(String.valueOf(city.pm25));
        builder.append(", qlty: ");
        builder.append(String.valueOf(city.qlty));
        return builder.toString();
    }
}
