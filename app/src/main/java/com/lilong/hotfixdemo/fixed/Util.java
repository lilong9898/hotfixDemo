package com.lilong.hotfixdemo.fixed;

import android.widget.Toast;

import com.lilong.hotfixdemo.application.DemoApplication;

/**
 * 被热修复的类
 */

public class Util {

    /**
     * 假设这个方法是坏的，热修复成功后会变成好的
     * */
    public static void showToast() {
        Toast.makeText(DemoApplication.getInstance(), "This is a bad method", Toast.LENGTH_SHORT).show();
    }
}
