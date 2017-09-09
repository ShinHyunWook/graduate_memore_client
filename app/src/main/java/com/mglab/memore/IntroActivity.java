package com.mglab.memore;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by 신현욱 on 2017-03-11.
 */

public class IntroActivity extends AppCompatActivity {

    private Handler mHandler;
    private Runnable mRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        mRunnable = new Runnable() {
            @Override
            public void run() {
                SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(IntroActivity.this);
                String result = sharedPreferencesManager.getLogin();
                if (result == null || result.length() == 0) {
                    Intent intent = new Intent(IntroActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent("com.mglab.memore.MemoreService");
                    intent.setPackage(getPackageName());
//                    startService(intent);
                    intent = new Intent(IntroActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        mHandler = new Handler();
        mHandler.postDelayed(mRunnable, 2500);
    }
}
