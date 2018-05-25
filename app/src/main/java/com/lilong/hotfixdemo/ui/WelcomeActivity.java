package com.lilong.hotfixdemo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.lilong.hotfixdemo.R;
import com.lilong.hotfixdemo.hotfix.HotfixManager;

/**
 */

public class WelcomeActivity extends AppCompatActivity {

    private Button mBtnWithoutHotfix;
    private Button mBtnWithHotfix;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        mBtnWithoutHotfix = (Button) findViewById(R.id.btnWithoutHotfix);
        mBtnWithHotfix = (Button) findViewById(R.id.btnWithHotfix);
        mBtnWithoutHotfix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        mBtnWithHotfix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HotfixManager.getInstance().hotfix();
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
