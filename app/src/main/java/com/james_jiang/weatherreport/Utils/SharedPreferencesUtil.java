package com.james_jiang.weatherreport.Utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by john on 2015/11/19.
 * 通过SharedPreferences存取String/float/int类型的数据
 */
public class SharedPreferencesUtil {
    private final static String LOCAL_VERSION = "local_version";
    private SharedPreferences share ;
    private SharedPreferences.Editor editor;
    private static SharedPreferencesUtil myParaSaveRead;
    private SharedPreferencesUtil(Context context){//paraName：存储的文件名，在同一组存取中，文件名应该一致；
        share = context.getSharedPreferences("weather_info",
                Context.MODE_APPEND | Context.MODE_MULTI_PROCESS);    //先申明权限
        editor = share.edit();
    }
    public synchronized static SharedPreferencesUtil getInstance(Context context){
        if(myParaSaveRead == null){
            myParaSaveRead = new SharedPreferencesUtil(context);
        }
        return myParaSaveRead;
    }
    //更改版本号
    public void UpGradeLocalSharedPreferences(int CURRENT_VERSION){
        int version;
        try{
            version = share.getInt(LOCAL_VERSION, 0);
        }catch (Exception e){
            version = 0;
    }
        if(version == CURRENT_VERSION)
            return;
        if(version == 0) {
            //添加升级项
        }
        editor.putInt(LOCAL_VERSION, CURRENT_VERSION);
        editor.commit();
    }
    //keyString：参数的key值；valueString：参数的内容
    //存储String类型值
    public String MyRead(String keyString, String valueString){
        return share.getString(keyString, valueString);
    }
    public void MySave(String keyString, String valueString){
        editor.putString(keyString, valueString);
        editor.commit();
    }
    //存储float类型值,float 也可以换成Float
    public float MyRead(String keyString, float valueFloat){
        return share.getFloat(keyString, valueFloat);
    }
    public void MySave(String keyString, float valueFloat){
        editor.putFloat(keyString, valueFloat);
        editor.commit();
    }
    //储存int类型值
    public int MyRead(String keyString, int valueInteger){
        return share.getInt(keyString, valueInteger);
    }
    public void MySave(String keyString, int valueInteger){
        editor.putInt(keyString, valueInteger);
        editor.commit();
    }
    public boolean MyRead(String keyString, boolean valueBoolean){
        return share.getBoolean(keyString, valueBoolean);
    }
    public void MySave(String keyString, boolean valueBoolean){
        editor.putBoolean(keyString, valueBoolean);
        editor.commit();
    }
    //在文件改动之后需要提交
//    public void MyCommit(){
//        editor.commit();
//    }
    //移除单个,移除后一定得提交
    public void MyRemove(String keyString){
        editor.remove(keyString);
        editor.commit();
    }
    //全部清空,也得提交
    public void MyClear(){
        editor.clear();
        editor.commit();
    }
}
