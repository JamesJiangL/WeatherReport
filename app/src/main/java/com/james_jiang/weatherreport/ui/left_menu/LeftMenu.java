package com.james_jiang.weatherreport.ui.left_menu;

/**
 * Created by JC on 2016/8/29.
 */
public class LeftMenu {
    private boolean isLocation;
    private String cityName;

    public boolean isLocation() {
        return isLocation;
    }

    public void setLocation(boolean location) {
        isLocation = location;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
}
