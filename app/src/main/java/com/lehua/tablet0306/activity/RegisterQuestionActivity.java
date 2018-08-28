package com.lehua.tablet0306.activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.lehua.tablet0306.R;
import com.lehua.tablet0306.activity.Adapter.BookAdapter;
import com.lehua.tablet0306.activity.Adapter.ProcessAdapter;
import com.lehua.tablet0306.bean.User;
import com.lehua.tablet0306.okhttp.HttpRequest;
import com.lehua.tablet0306.okhttp.URLConstant;
import com.lehua.tablet0306.utils.ListViewUtil;
import com.lehua.tablet0306.utils.MethodUtils;
import com.lehua.tablet0306.utils.SpHelp;
import com.vise.log.ViseLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RegisterQuestionActivity extends AppCompatActivity implements View.OnClickListener {
    //TODO: 可以放到数据类里
    static final String[] primarySubjects = {"chinese", "math", "english", "comprehensive"};
    static final String[] middleSubjects = {"chinese", "math", "english", "physics", "chemistry", "biology", "politics", "history", "geography"};
    static final String[] primarySubjectsTags = {"语文", "数学", "英语", "综合"};
    static final String[] middleSubjectsTags = {"语文", "数学", "英语", "物理", "化学", "生物", "政治", "历史", "地理"};

    private String grade = "p"; // p表示小学 m表示初中
    private String account;

    private boolean service = false; //协议按钮是否选中

    TextView tv_back, tv_title;
    LinearLayout ll_cb_group, ll_rg_group;
    EditText et_hobby;
    Button btn_finish;
    RadioButton rb_service;
    ListView lv_book, lv_process;

    BookAdapter bookAdapter;
    ProcessAdapter processAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register_question);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        initData();
        initView();
        initList();
        bindListener();
    }

    private void initData() {
        User user = (User) getIntent().getSerializableExtra("user");
        account = user.getAccount();

        grade = user.getSchoolClass().contains("小学") ? "p" : "m";
    }

    private void initView() {
        tv_title = findViewById(R.id.tv_title);
        tv_back = findViewById(R.id.tv_back);
        ll_cb_group = findViewById(R.id.ll_cb_group);
        ll_rg_group = findViewById(R.id.ll_rg_group);
        et_hobby = findViewById(R.id.et_hobby);
        btn_finish = findViewById(R.id.btn_finish);
        rb_service = findViewById(R.id.rb_service);
        lv_book = findViewById(R.id.lv_book);
        lv_process = findViewById(R.id.lv_process);

        tv_title.setText(grade == "p" ? "小学" : "初中");
    }

    private void initList() {
        String[] subjects = grade.equals("p") ? primarySubjectsTags : middleSubjectsTags;

        List<String> list = Arrays.asList(subjects);
        bookAdapter = new BookAdapter(RegisterQuestionActivity.this, R.layout.book_item, list);
        lv_book.setAdapter(bookAdapter);
        ListViewUtil.setListViewHeightBasedOnChildren(lv_book);

        processAdapter = new ProcessAdapter(RegisterQuestionActivity.this, R.layout.process_item, list);
        lv_process.setAdapter(processAdapter);
        ListViewUtil.setListViewHeightBasedOnChildren(lv_process);
    }

    private void bindListener() {
        tv_back.setOnClickListener(this);
        btn_finish.setOnClickListener(this);
        rb_service.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.btn_finish:
                //TODO: get data and upload
                attemptToRegister();
                break;
            case R.id.rb_service:
                service = !service;
                rb_service.setChecked(service);
                break;
            default:
                break;
        }
    }

    private String getJson() {
        String[] subjects = grade.equals("p") ? primarySubjects : middleSubjects;

        JSONObject json = new JSONObject();
        JSONObject jsonSubject = new JSONObject();

        try {
            json.put("account", account);
            json.put("grade", grade);

            //book data
            List<String> books = bookAdapter.getBook();
            if (books.contains(null)) {
                MethodUtils.showToast(getApplicationContext(), "请完整选择教材版本！");
                return null;
            }
            //process data
            List<Integer> process = processAdapter.getProcess();
            if (process.contains(0)) {
                MethodUtils.showToast(getApplicationContext(), "请完整选择综合学习程度！");
                return null;
            }

            for (int i = 0; i < subjects.length; i++) {
                JSONObject sub = new JSONObject();
                sub.put("book", books.get(i));
                sub.put("process", process.get(i));
                jsonSubject.put(subjects[i], sub);
            }
            json.put("subject", jsonSubject);

            //hobby data
            JSONArray hobbies = new JSONArray();
            for (int i = 0; i < ll_cb_group.getChildCount(); i++) {
                CheckBox cb = (CheckBox) ll_cb_group.getChildAt(i);
                if (cb.isChecked()) {
                    hobbies.put(cb.getText().toString());
                }
            }
            if (!et_hobby.getText().toString().equals("")) {
                hobbies.put(et_hobby.getText().toString());
            }
            json.put("hobby", hobbies);

            //question data
            JSONArray jsonOther = new JSONArray();
            for (int i = 0; i < ll_rg_group.getChildCount(); i++) {
                //多选
                if (i == 4) {
                    JSONArray jsonArray = new JSONArray();
                    LinearLayout ll = (LinearLayout) ((LinearLayout) ll_rg_group.getChildAt(i)).getChildAt(1);
                    for (int j = 0; j < ll.getChildCount(); j++) {
                        CheckBox cb = (CheckBox) ll.getChildAt(j);
                        if (cb.isChecked()) {
                            jsonArray.put(j + 1);
                        }
                    }
                    jsonOther.put(jsonArray);
                } else {
                    //单选
                    RadioGroup rg = (RadioGroup) ((LinearLayout) ll_rg_group.getChildAt(i)).getChildAt(1);
                    int flag = 0;
                    for (int j = 0; j < rg.getChildCount(); j++) {
                        if (((RadioButton) rg.getChildAt(j)).isChecked()) {
                            flag = j + 1;
                            break;
                        }
                    }
                    if (flag == 0) {
                        MethodUtils.showToast(getApplicationContext(), "请完整填写问卷！");
                        return null;
                    }
                    jsonOther.put(flag);
                }
            }
            json.put("other", jsonOther);

            if (!service) {
                MethodUtils.showToast(getApplicationContext(), "请同意服务条款！");
                return null;
            }

            //return json string
            System.out.println(json.toString());
            return json.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void attemptToRegister() {
        String dataJson = getJson();
        if (dataJson == null) {
            return;
        }

        HttpRequest.post(URLConstant.URL_REGISTER_QUESTION, dataJson, new HttpRequest.HttpRequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                if (response.optString("error").equals("0")) {

                    MethodUtils.showToast(getApplicationContext(), "注册成功");

                    //TODO: 可以把数据保存在本地
                    Intent intent = new Intent(RegisterQuestionActivity.this, RadarActivity.class);  //同时跳转到“注册成功界面”
                    intent.putExtra("process", new ArrayList<>(processAdapter.getProcess()));
                    startActivity(intent);
                    finish();
                } else {
                    ViseLog.d("注册失败: " + response.optString("error_info"));
                    MethodUtils.showToast(getApplicationContext(), "注册失败: " + response.optString("error_info"));
                }
            }

            @Override
            public void onFailure() {
                MethodUtils.showToast(getApplicationContext(), "请求失败, 请稍后重试");
                ViseLog.d("请求失败, 请稍后重试");
            }
        });
    }
}
