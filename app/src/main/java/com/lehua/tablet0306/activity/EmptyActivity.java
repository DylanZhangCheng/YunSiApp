package com.lehua.tablet0306.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.lehua.tablet0306.utils.SpHelp;


public class EmptyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        if (SpHelp.getIsFirstOpenApplication()) {
            SpHelp.saveIsFirstOpenApplication(false);
            Intent intent = new Intent(getApplicationContext(), SplashActivityOne.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
    }
}
