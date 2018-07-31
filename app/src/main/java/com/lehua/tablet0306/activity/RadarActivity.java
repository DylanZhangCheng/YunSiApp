package com.lehua.tablet0306.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.lehua.tablet0306.R;
import com.lehua.tablet0306.utils.RadarView;

import java.util.ArrayList;

public class RadarActivity extends AppCompatActivity {

    RadarView radar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radar);
        getSupportActionBar().hide();

        initRadar();
        Button btn_next = findViewById(R.id.btn_next);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RadarActivity.this, LoginActivity.class));
            }
        });
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
}
