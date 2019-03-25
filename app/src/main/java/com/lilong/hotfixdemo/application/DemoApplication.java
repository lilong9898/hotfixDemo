package com.lilong.hotfixdemo.application;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.lilong.hotfixdemo.hotfix.HotfixManager;

/**
 */

public class DemoApplication extends MultiDexApplication {

    private static DemoApplication sInstance;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        sInstance = this;
        HotfixManager.getInstance().init();
    }

    public static DemoApplication getInstance(){
        return sInstance;
    }
}
