package com.lilong.hotfixdemo.application;

import android.app.Application;
import android.content.Context;

/**
 */

public class HotfixDemoApplication extends Application {

    private static HotfixDemoApplication sInstance;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        sInstance = this;
    }

    public static HotfixDemoApplication getInstance(){
        return sInstance;
    }
}
