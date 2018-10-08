package com.example.wuzhongcheng.floatwindow;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

public class MyApplication extends Application implements Application.ActivityLifecycleCallbacks{

    private int mActivityNum;
    Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(this);
        mContext = getApplicationContext();
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        mActivityNum += 1;
        MyWindowManager.updateWindowStatus(mContext,true);
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        mActivityNum -= 1;
        if (mActivityNum == 0) {
            MyWindowManager.updateWindowStatus(mContext,false);
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
