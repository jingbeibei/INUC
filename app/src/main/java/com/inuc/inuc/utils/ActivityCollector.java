package com.inuc.inuc.utils;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 景贝贝 on 2016/7/24.
 */
public class ActivityCollector {
    public static List<Activity> activities=new ArrayList<Activity>();
    public static void addActivity(Activity activity){
        activities.add(activity);
    }
    public static void removeActivity(Activity activity){
        activities.remove(activity);
        activity.finish();
    }
    public static void finishAll(){
        for(Activity activity:activities){
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
    }
}
