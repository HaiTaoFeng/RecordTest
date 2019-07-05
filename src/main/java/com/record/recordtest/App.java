package com.record.recordtest;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;

import com.record.recordtest.manager.ActivityManager;
import com.record.recordtest.util.CrashHandler;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public class App extends Application {

    private static App app;

    public static App getApp() {
        return app;
    }

    public static List<Activity> mActivitys = Collections.synchronizedList(new LinkedList<Activity>()); //维护Activity 的list

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        registerActivityListener();
        CrashHandler.getInstance().init(this);
    }

    private void registerActivityListener() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
                @Override
                public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                    //监听到 Activity创建事件 将该 Activity 加入list
                    ActivityManager.pushActivity(activity);
                }

                @Override
                public void onActivityStarted(Activity activity) {
                }

                @Override
                public void onActivityResumed(Activity activity) {
                }

                @Override
                public void onActivityPaused(Activity activity) {
                }

                @Override
                public void onActivityStopped(Activity activity) {
                }

                @Override
                public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                }

                @Override
                public void onActivityDestroyed(Activity activity) {
                    if (null == mActivitys || mActivitys.isEmpty()) {
                        return;
                    }
                    if (mActivitys.contains(activity)) {
                        // 监听到 Activity销毁事件 将该Activity 从list中移除
                        ActivityManager.popActivity(activity);
                    }
                }
            });
        }
    }


}
