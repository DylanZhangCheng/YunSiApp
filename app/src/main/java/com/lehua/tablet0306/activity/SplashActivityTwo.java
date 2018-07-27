package com.lehua.tablet0306.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.lehua.tablet0306.R;


public class SplashActivityTwo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_two);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        Handler handler = new Handler();
        handler.postDelayed(autoJumpTask, 2000);
    }

    private Runnable autoJumpTask = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(SplashActivityTwo.this, LoginActivity.class);
            startActivity(intent);
        }
    };
}
