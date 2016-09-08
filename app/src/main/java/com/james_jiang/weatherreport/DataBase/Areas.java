package com.james_jiang.weatherreport.DataBase;

import java.io.Serializable;

/**
 * Created by JC on 2016/8/9.
 */
public class Areas implements Serializable{
    private String areaId;      //地区代号
    private String areaNameEN;      //地区英文名
    private String areaNameCH;     //地区中文名
    private String city;        //地区所属城市
    private String province;        //地区所属省
    public String getAreaId(){
        return areaId;
    }
    public String getAreaNameEN(){
        return areaNameEN;
    }
    public String getAreaNameCH(){
        return areaNameCH;
    }
    public String getCity(){
        return city;
    }
    public String getProvince(){
        return province;
    }
    public void setAreaId(String id){
        areaId = id;
    }
    public void setAreaNameEN(String nameEN){
        areaNameEN = nameEN;
    }
    public void setAreaNameCH(String nameCH){
        areaNameCH = nameCH;
    }
    public void setCity(String city){
        this.city = city;
    }
    public void setProvince(String province){
        this.province = province;
    }
}
