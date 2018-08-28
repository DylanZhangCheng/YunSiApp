package com.lehua.tablet0306.activity;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.lehua.tablet0306.R;
import com.lehua.tablet0306.bean.User;
import com.lehua.tablet0306.okhttp.HttpRequest;
import com.lehua.tablet0306.okhttp.URLConstant;
import com.lehua.tablet0306.utils.MethodUtils;
import com.lehua.tablet0306.utils.NbButton;
import com.lehua.tablet0306.utils.SpHelp;
import com.vise.log.ViseLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout ll_back;
    TextView tv_to_register;
    EditText et_account, et_pwd;
    RadioButton rb_auto_login, rb_save_pwd;
    Button btn_login;

    private boolean autoLogin, savePwd;
    private static final int MIN_DELAY_TIME= 500;  // 两次点击间隔不能少于1000ms
    private static long lastClickTime;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_login);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        initView();


        initListener();
        initData();
        initRadioButton();
    }

    private void initView() {
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        tv_to_register = (TextView) findViewById(R.id.tv_to_register);
        et_account = (EditText) findViewById(R.id.et_account);
        et_pwd = (EditText) findViewById(R.id.et_pwd);
        btn_login = (Button) findViewById(R.id.btn_login);

        rb_auto_login = findViewById(R.id.rb_auto_login);
        rb_save_pwd = findViewById(R.id.rb_save_pwd);


    }

    private void initListener() {
        ll_back.setOnClickListener(this);
        rb_auto_login.setOnClickListener(this);
        rb_save_pwd.setOnClickListener(this);
        btn_login.setOnClickListener(this);
    }

    private void initData() {
        String str = tv_to_register.getText().toString();
        SpannableString ss = new SpannableString(str);
        int color = getResources().getColor(R.color.tomato);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(color);
        ss.setSpan(colorSpan, 6, ss.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        ss.setSpan(clickSpan, 6, ss.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        tv_to_register.setMovementMethod(LinkMovementMethod.getInstance());
        //setMovementMethod() 这个必须设置，否则点击无响应
        tv_to_register.setHighlightColor(Color.parseColor("#00ffffff"));

        tv_to_register.setText(ss);


    }

    private void initRadioButton() {
        //TODO: here should read config from SP
        autoLogin = savePwd = true;
        updateRadioButton();
    }

    private void updateRadioButton() {
        rb_auto_login.setChecked(autoLogin);
        rb_save_pwd.setChecked(savePwd);
    }

    private ClickableSpan clickSpan = new ClickableSpan() {
        @Override
        public void onClick(View widget) {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_back:
                finish();
                break;
            case R.id.btn_login:
                if (checkInputLegal()) {
                    if (MethodUtils.is3G(getApplicationContext()) || MethodUtils.isWifi(getApplicationContext())) {
                        MethodUtils.showToast(getApplicationContext(), "正在登陆...");
                        if(isFastClick()){MethodUtils.showToast(getApplicationContext(), "正在登陆...");}
                                attemptLogin();
                    } else {
                        MethodUtils.showToast(getApplicationContext(), "请检查网络是否正常!");
                    }
                }
                break;
            case R.id.rb_auto_login:
                //TODO: maybe here are some logic
                autoLogin = !autoLogin;
                updateRadioButton();
                break;
            case R.id.rb_save_pwd:
                savePwd = !savePwd;
                updateRadioButton();
                break;
            default:
                break;
        }
    }

    public void refresh() {
        onCreate(null);
    }
    private void attemptLogin() {

        String account = et_account.getText().toString().trim();
        String pwd = et_pwd.getText().toString().trim();

        try {

            JSONObject object = new JSONObject();
            object.put("account", account);
            object.put("pwd", pwd);
            HttpRequest.post(URLConstant.URL_LOGIN, object.toString(), new HttpRequest.HttpRequestCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    if (response.optString("error").equals("0")) {
                        ViseLog.d("验证成功");
                        MethodUtils.showToast(getApplicationContext(), "验证成功");
                        JSONObject data = response.optJSONObject("data");
                        User user = new User();
                        user.setAccount(data.optString("account"));
                        user.setName(data.optString("name"));
                        user.setSex(data.optString("sex"));
                        user.setArea(data.optString("area"));
                        user.setAge(data.optInt("age"));
                        user.setParentNum(data.optString("parentNum"));
                        user.setProvince(data.optString("province"));
                        user.setCity(data.optString("city"));
                        user.setArea(data.optString("area"));
                        user.setSchool(data.optString("school"));
                        user.setSchoolClass(data.optString("schoolClass"));


                        SpHelp.saveObject(SpHelp.USER_ENTITY, user);
                        SpHelp.saveUserId(data.optString("account"));

                        if (savePwd) {
                            //TODO: save pwd code
                        }

                        if (autoLogin) {
                            //TODO: auto login code
                        }

                        Intent intent = new Intent(getApplicationContext(), PushListActivity.class);
                        startActivity(intent);

                    } else {
                        MethodUtils.showToast(getApplicationContext(), "验证失败: " + response.optString("error_info"));
                    }
                }

                @Override
                public void onFailure() {
                    MethodUtils.showToast(getApplicationContext(), "请求失败, 请稍后重试");
                    ViseLog.d("请求失败, 请稍后重试");
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public boolean checkInputLegal() {
        String account = et_account.getText().toString().trim();
        if (TextUtils.isEmpty(account)) {
            MethodUtils.showToast(getApplicationContext(), "账号不能为空");
            return false;
        }
        if (!Pattern.matches("^1[3|4|5|8][0-9]\\d{8}$", account)) {
            MethodUtils.showToast(getApplicationContext(), "账号不符合电话号码格式，请输入正确的手机号码");
            return false;
        }
        String pwd = et_pwd.getText().toString().trim();
        if (TextUtils.isEmpty(pwd)) {
            MethodUtils.showToast(getApplicationContext(), "密码不能为空");
            return false;
        }
        return true;
    }

    public static boolean isFastClick() {
        boolean flag = true;
        long currentClickTime = System.currentTimeMillis();
        if ((currentClickTime - lastClickTime) >= MIN_DELAY_TIME) {
            flag = false;
        }
        lastClickTime = currentClickTime;
        return flag;
    }

}
