package com.james_jiang.weatherreport.AreasWeatherInfo;

/**
 * Created by JC on 2016/8/10.
 */
public class AreasSuggestions {
    private String comfBrf;     //舒适度指数
    private String cwBrf;       //洗车指数
    private String drsgBrf;     //穿衣指数
    private String fluBrf;      //感冒指数
    private String sportBrf;    //运动指数
    private String travBrf;     //旅游指数
    private String uvBrf;       //紫外线指数

    public AreasSuggestions(String comfBrf, String cwBrf, String drsgBrf,
                            String fluBrf, String sportBrf, String travBrf, String uvBrf){
        this.comfBrf = comfBrf;
        this.cwBrf = cwBrf;
        this.drsgBrf = drsgBrf;
        this.fluBrf = fluBrf;
        this.sportBrf = sportBrf;
        this.travBrf = travBrf;
        this.uvBrf = uvBrf;
    }
    public String getComfBrf() {
        return comfBrf;
    }

    public void setComfBrf(String comfBrf) {
        this.comfBrf = comfBrf;
    }

    public String getCwBrf() {
        return cwBrf;
    }

    public void setCwBrf(String cwBrf) {
        this.cwBrf = cwBrf;
    }

    public String getDrsgBrf() {
        return drsgBrf;
    }

    public void setDrsgBrf(String drsgBrf) {
        this.drsgBrf = drsgBrf;
    }

    public String getFluBrf() {
        return fluBrf;
    }

    public void setFluBrf(String fluBrf) {
        this.fluBrf = fluBrf;
    }

    public String getSportBrf() {
        return sportBrf;
    }

    public void setSportBrf(String sportBrf) {
        this.sportBrf = sportBrf;
    }

    public String getTravBrf() {
        return travBrf;
    }

    public void setTravBrf(String travBrf) {
        this.travBrf = travBrf;
    }

    public String getUvBrf() {
        return uvBrf;
    }

    public void setUvBrf(String uvBrf) {
        this.uvBrf = uvBrf;
    }
}
