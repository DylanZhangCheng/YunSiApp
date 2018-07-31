package com.lehua.tablet0306.activity.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lehua.tablet0306.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Czj
 * @Date: 2018/7/28 16:36
 * @Description:
 */

public class ProcessAdapter extends ArrayAdapter {
    private final int resourceId;

    private ArrayList<Integer> data;

    public ProcessAdapter(Context context, int textViewResourceId, List<String> subject) {
        super(context, textViewResourceId, subject);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        TextView tv_name = view.findViewById(R.id.tv_name);
        final RelativeLayout rl_process_line = view.findViewById(R.id.rl_process_line);

        tv_name.setText(getItem(position) + "：");

        //init data
        data = new ArrayList<>();
        for (int i = 0; i < getCount(); i++) {
            data.add(0);
        }

        //set listener
        for (int i = 0; i < rl_process_line.getChildCount(); i++) {
            final TextView tv = (TextView) rl_process_line.getChildAt(i);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //注意，序号是倒着排的
                    //cancel chosen item
                    int oldChosen = data.get(position);
                    if (oldChosen != 0) {
                        Drawable bg = getContext().getResources().getDrawable(R.drawable.process_line_bg);
                        rl_process_line.getChildAt(10 - oldChosen / 10).setBackground(bg);
                    }
                    //choose
                    int newChosen = Integer.valueOf(tv.getText().toString());
                    Drawable bg = getContext().getResources().getDrawable(R.drawable.process_line_chosen);
                    tv.setBackground(bg);
                    data.set(position, newChosen);
                }
            });
        }
        return view;
    }

    public List<Integer> getProcess() {
        return data;
    }
}
