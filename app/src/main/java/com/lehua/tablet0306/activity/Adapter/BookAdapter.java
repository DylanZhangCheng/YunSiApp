package com.lehua.tablet0306.activity.Adapter;

import android.content.Context;
import android.opengl.ETC1;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.lehua.tablet0306.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Czj
 * @Date: 2018/7/28 16:35
 * @Description:
 */

public class BookAdapter extends ArrayAdapter {
    private final int resourceId;

    private ArrayList<String> data;

    public BookAdapter(Context context, int textViewResourceId, List<String> subject) {
        super(context, textViewResourceId, subject);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        TextView tv_name = view.findViewById(R.id.tv_name);
        RadioButton rb_default = view.findViewById(R.id.rb_default);
        RadioButton rb_other = view.findViewById(R.id.rb_other);
        final EditText et_other = view.findViewById(R.id.et_other);

        tv_name.setText(getItem(position) + "：");


        //init data
        data = new ArrayList<>();
        for (int i = 0; i < getCount(); i++) {
            data.add(null);
        }

        //set listener
        rb_default.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_other.setEnabled(false);
                data.set(position, "人教版");
            }
        });
        rb_other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_other.setEnabled(true);
                String str = et_other.getText().toString();
                data.set(position, str.equals("") ? null : str);
            }
        });
        et_other.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String str = et_other.getText().toString();
                data.set(position, str.equals("") ? null : str);
            }
        });
        return view;
    }

    public List<String> getBook() {
        return data;
    }


}
