package com.record.recordtest.manager;


import android.app.Activity;
import com.record.recordtest.App;


public class ActivityManager {

    // 退出应用程序
    public  static void appExit() {
        try {
            finishAllActivity();
        } catch (Exception e) {
        }
    }

    //添加一个activity到管理里
    public static void pushActivity(Activity activity) {
        App.mActivitys.add(activity);
    }

    //删除一个activity在管理里
    public static void popActivity(Activity activity) {
        App.mActivitys.remove(activity);
    }

    //获取当前Activity（栈中最后一个压入的）
    public static Activity currentActivity() {
        if (App.mActivitys == null||App.mActivitys.isEmpty()) {
            return null;
        }
        Activity activity = App.mActivitys.get(App.mActivitys.size()-1);
        return activity;
    }

    // 结束当前Activity（栈中最后一个压入的）
    public static void finishCurrentActivity() {
        if (App.mActivitys == null||App.mActivitys.isEmpty()) {
            return;
        }
        Activity activity = App.mActivitys.get(App.mActivitys.size()-1);
        finishActivity(activity);
    }

    //结束指定的Activity
    public static void finishActivity(Activity activity) {
        if (App.mActivitys == null||App.mActivitys.isEmpty()) {
            return;
        }
        if (activity != null) {
            App.mActivitys.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    //结束指定类名的Activity
    public static void finishActivity(Class<?> cls) {
        if (App.mActivitys == null||App.mActivitys.isEmpty()) {
            return;
        }
        for (Activity activity : App.mActivitys) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }

    //按照指定类名找到activity
    public static Activity findActivity(Class<?> cls) {
        Activity targetActivity = null;
        if (App.mActivitys != null) {
            for (Activity activity : App.mActivitys) {
                if (activity.getClass().equals(cls)) {
                    targetActivity = activity;
                    break;
                }
            }
        }
        return targetActivity;
    }

    //获取当前最顶部activity的实例
    public Activity getTopActivity() {
        Activity mBaseActivity = null;
        synchronized (App.mActivitys) {
            final int size = App.mActivitys.size() - 1;
            if (size < 0) {
                return null;
            }
            mBaseActivity = App.mActivitys.get(size);
        }
        return mBaseActivity;
    }

    //获取当前最顶部的acitivity 名字
    public String getTopActivityName() {
        Activity mBaseActivity = null;
        synchronized (App.mActivitys) {
            final int size = App.mActivitys.size() - 1;
            if (size < 0) {
                return null;
            }
            mBaseActivity = App.mActivitys.get(size);
        }
        return mBaseActivity.getClass().getName();
    }

    //结束所有Activity
    public static void finishAllActivity() {
        if (App.mActivitys == null || App.mActivitys.size() == 0) {
            return;
        }
        for (Activity activity : App.mActivitys) {
            activity.finish();
        }
        App.mActivitys.clear();
    }

}
