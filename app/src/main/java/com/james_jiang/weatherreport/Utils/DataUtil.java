package com.james_jiang.weatherreport.Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by JC on 2016/8/11.
 */
public class DataUtil {
    private DataUtil(){
        throw new AssertionError();
    }
    public static String getWeekDate(Date date){
        DateFormat format=new SimpleDateFormat("MM dd");
        String time=format.format(date);
        String[] strings = time.split(" ");
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        StringBuilder builder = new StringBuilder();
        builder.append(strings[0]);
        builder.append("月");
        builder.append(strings[1]);
        builder.append("日，");
        builder.append(weekDays[w]);
        return builder.toString();
    }
    public static String getWeekDate(String date){
        String[] strings = date.split("-");
        StringBuilder builder = new StringBuilder();
        builder.append(strings[1]);
        builder.append("/");
        builder.append(strings[2]);
        return builder.toString();
    }
}
