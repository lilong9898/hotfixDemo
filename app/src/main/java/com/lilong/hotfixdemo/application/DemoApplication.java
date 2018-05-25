package com.lilong.hotfixdemo.application;

import android.app.Application;
import android.content.Context;

import com.lilong.hotfixdemo.hotfix.HotfixManager;

/**
 */

public class DemoApplication extends Application {

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
