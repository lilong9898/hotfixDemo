package com.lilong.hotfixdemo.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.lilong.hotfixdemo.R;
import com.lilong.hotfixdemo.fixed.Util;

public class MainActivity extends AppCompatActivity {

    private Button mBtnCallMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtnCallMethod = (Button) findViewById(R.id.btnCallMethod);
        mBtnCallMethod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.showToast();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
