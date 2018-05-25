package com.lilong.hotfixdemo.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.lilong.hotfixdemo.R;
import com.lilong.hotfixdemo.fixed.Util;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Util.showToast();
    }
}
