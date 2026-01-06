package com.example.administrator.sportmanager.admin;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

public class ActivityCollector {
    public static List<Activity> activities = new ArrayList<>();

//ActivityCollector 类就可以追踪所有打开的活动，并在需要时一次性关闭它们。

    public static void finishAll() {
        for (Activity activity : activities) {
            if (!activity.isFinishing()) {
                activity.finish();

            }

        }
        activities.clear();
    }    
}
