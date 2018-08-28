package com.lehua.tablet0306.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.lehua.tablet0306.R;
import com.lehua.tablet0306.utils.RadarView;

import java.util.ArrayList;

public class RadarChangeActivity extends AppCompatActivity implements View.OnClickListener{
    TextView rg_back;
    RadarView radar;
    Button btn_next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_radar_change);
        getSupportActionBar().hide();
        initView();
        initListener();
        initRadar();
    }

    private void initView() {
        rg_back = (TextView) findViewById(R.id.rg_back);
        btn_next = findViewById(R.id.btn_next);

    }

    private void initListener() {
        rg_back.setOnClickListener(this);
        btn_next.setOnClickListener(this);
    }

    private void initRadar() {
        radar = findViewById(R.id.radar);

        ArrayList<Integer> process = getIntent().getIntegerArrayListExtra("process");
        String[] dataTag = process.size() == 4 ? RegisterQuestionActivity.primarySubjectsTags
                : RegisterQuestionActivity.middleSubjectsTags;

        radar.setTitles(dataTag);

        ArrayList<Float> data = new ArrayList<>();
        for (Integer i : process) {
            data.add(Float.parseFloat(i + ""));
        }
        radar.setData(data);
        radar.setMaxValue(100);
        radar.setLableCount(6);

        radar.setValuePaintColor(Color.rgb(68, 151, 211));
        radar.setStrokeWidth(5);
        radar.setInnerAlpha(166);
        radar.setCircleRadius(1);

        radar.setMainPaintColor(Color.GRAY);

        radar.setTextPaintTextSize(32);
        radar.setTextPaintColor(Color.rgb(68, 151, 211));

        radar.invalidate();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rg_back:
                finish();
                break;
            case R.id.btn_next:
                finish();
                break;
            default:
                break;
        }
    }

}
