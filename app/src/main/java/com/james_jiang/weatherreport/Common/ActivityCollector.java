package com.james_jiang.weatherreport.Common;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 2015/11/19.
 */
public class ActivityCollector {    //活动管理器
    public static List<Activity> activities = new ArrayList<Activity>();

    public static void addActivity(Activity activity){
		if(!activities.contains(activity)){
			activities.add(activity);
		}
    }
    public static void removeActivity(Activity activity){
        activities.remove(activity);
    }
    public static void finishOne(Activity activity){
        activities.remove(activity);
        activity.finish();
    }
    public static void finishAll(){
        for(Activity activity : activities){
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
    }
    public static Activity getTopActivity(){
        return activities.get(activities.size() - 1);
    }
    public static String getTopActivityName(){  //得到的是不包含包名的名字
        return activities.get(activities.size() - 1).getClass().getSimpleName();
    }
}
