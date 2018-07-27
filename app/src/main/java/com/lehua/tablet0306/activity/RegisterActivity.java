package com.lehua.tablet0306.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.lehua.tablet0306.R;
import com.lehua.tablet0306.bean.User;
import com.lehua.tablet0306.okhttp.HttpRequest;
import com.lehua.tablet0306.okhttp.URLConstant;
import com.lehua.tablet0306.utils.AreaUtil;
import com.lehua.tablet0306.utils.MethodUtils;
import com.lehua.tablet0306.utils.SpHelp;
import com.vise.log.ViseLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    TextView tv_back, tv_to_login;
    TextView tv_Province, tv_city, tv_area;
    EditText et_account, et_pwd, et_name, et_age, et_parent_num, et_school_name;
    Spinner sp_sex, sp_class;
    Button btn_register;

    private String[] provinces;
    private String[] cities;
    private String[] areas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        initView();
        initListener();
        initData();
    }

    private void initView() {
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_to_login = (TextView) findViewById(R.id.tv_to_login);
        tv_Province = (TextView) findViewById(R.id.tv_Province);
        tv_city = (TextView) findViewById(R.id.tv_city);
        tv_area = (TextView) findViewById(R.id.tv_area);

        et_account = (EditText) findViewById(R.id.et_account);
        et_pwd = (EditText) findViewById(R.id.et_pwd);
        et_account = (EditText) findViewById(R.id.et_account);
        et_name = (EditText) findViewById(R.id.et_name);
        et_age = (EditText) findViewById(R.id.et_age);
        et_parent_num = (EditText) findViewById(R.id.et_parent_num);
        et_school_name = (EditText) findViewById(R.id.et_school_name);

        sp_sex = (Spinner) findViewById(R.id.sp_sex);
        sp_class = (Spinner) findViewById(R.id.sp_class);

        btn_register = (Button) findViewById(R.id.btn_register);
    }

    private void initListener() {
        tv_back.setOnClickListener(this);
        tv_Province.setOnClickListener(this);
        tv_city.setOnClickListener(this);
        tv_area.setOnClickListener(this);
        btn_register.setOnClickListener(this);

        sp_sex.setOnItemSelectedListener(this);
        sp_class.setOnItemSelectedListener(this);
    }

    private void initData() {
        List<String> provinceList = AreaUtil.getProvinces(this);
        provinces = new String[provinceList.size()];
        provinceList.toArray(provinces);

        String str = tv_to_login.getText().toString();
        SpannableString ss = new SpannableString(str);
        int color = getResources().getColor(R.color.tomato);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(color);
        ss.setSpan(colorSpan, 5, ss.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        ss.setSpan(clickSpan, 5, ss.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        tv_to_login.setMovementMethod(LinkMovementMethod.getInstance());
        //setMovementMethod() 这个必须设置，否则点击无响应
        tv_to_login.setHighlightColor(Color.parseColor("#00ffffff"));

        tv_to_login.setText(ss);

        ArrayAdapter adapter1 = ArrayAdapter.createFromResource(this,
                R.array.sex_array, R.layout.custom_spiner_text_item);
        adapter1.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        sp_sex.setAdapter(adapter1);

        ArrayAdapter adapter2 = ArrayAdapter.createFromResource(this,
                R.array.class_array, R.layout.custom_spiner_text_item);
        adapter2.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        sp_class.setAdapter(adapter2);
    }

    private ClickableSpan clickSpan = new ClickableSpan() {
        @Override
        public void onClick(View widget) {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.tv_Province:
                showProvinceList();
                break;
            case R.id.tv_city:
                if (TextUtils.isEmpty(selectedProvince)) {
                    Toast.makeText(this, "请先选择省份", Toast.LENGTH_SHORT).show();
                    return;
                }
                showCityList();
                break;
            case R.id.tv_area:
                if (TextUtils.isEmpty(selectedProvince)) {
                    Toast.makeText(this, "请先选择省份", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(selectedCity)) {
                    Toast.makeText(this, "请先选择城市", Toast.LENGTH_SHORT).show();
                    return;
                }
                showAreaList();
                break;

            case R.id.btn_register:
//                if (checkInputLegal()) {
//                    attemptToRegister();
//                }
                Intent intent = new Intent(getApplicationContext(), RegisterQuestionActivity.class);  //同时跳转到“注册成功界面”
                //fix: 跳转至问卷
                startActivity(intent);
                break;
            default:
                break;
        }
    }
     /*  重点，重点，重点来了，这里将注册信息上传到服务器，
      同时，将注册信息又保存在本地数据库*/

    private void attemptToRegister() {
        JSONObject userInfo = new JSONObject();
        try {
            userInfo.put("account", account)
                    .put("pwd", pwd)
                    .put("name", name)
                    .put("sex", sex)
                    .put("age", age)
                    .put("school", school)
                    .put("schoolClass", schoolClass)
                    .put("parentNum", parentNum)
                    .put("province", province)
                    .put("city", city)
                    .put("area", area);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpRequest.post(URLConstant.URL_REGISTER, userInfo.toString(), new HttpRequest.HttpRequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                if (response.optString("error").equals("0")) {

                    MethodUtils.showToast(getApplicationContext(), "注册成功");

                    User user = new User();
                    user.setAccount(account);
                    user.setPwd(pwd);
                    user.setName(name);
                    user.setSex(sex);
                    user.setAge(Integer.valueOf(age));
                    user.setSchool(school);
                    user.setSchoolClass(schoolClass);
                    user.setParentNum(parentNum);
                    user.setProvince(province);
                    user.setCity(city);
                    user.setArea(area);

                    //将用户注册信息保存到本地数据库
                    SpHelp.saveObject(SpHelp.USER_ENTITY, user);
                    SpHelp.saveUserId(account);

                    Intent intent = new Intent(getApplicationContext(), RegisterQuestionActivity.class);  //同时跳转到“注册成功界面”
                    intent.putExtra("user", user);
                    //fix: 跳转至问卷
                    startActivity(intent);
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

    String account = "";
    String pwd = "";
    String name = "";
    String sex = "男";
    String age = "";
    String schoolClass = "初一 上学期";
    String parentNum = "";
    String province = "";
    String city = "";
    String area = "";
    String school = "";

    public boolean checkInputLegal() {
        account = et_account.getText().toString().trim();
        if (TextUtils.isEmpty(account)) {
            MethodUtils.showToast(getApplicationContext(), "账号不能为空");
            return false;
        }
        pwd = et_pwd.getText().toString().trim();
        if (TextUtils.isEmpty(pwd)) {
            MethodUtils.showToast(getApplicationContext(), "密码不能为空");
            return false;
        }

//        verifyCode = et_verify_code.getText().toString().trim();
//        if (TextUtils.isEmpty(verifyCode)) {
//            MethodUtils.showToast(getApplicationContext(), "验证码不能为空");
//            return false;
//        }

        name = et_name.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            MethodUtils.showToast(getApplicationContext(), "名字不能为空");
            return false;
        }

        age = et_age.getText().toString().trim();
        if (TextUtils.isEmpty(age)) {
            MethodUtils.showToast(getApplicationContext(), "年龄不能为空");
            return false;
        }

        parentNum = et_parent_num.getText().toString().trim();
        if (TextUtils.isEmpty(parentNum)) {
            MethodUtils.showToast(getApplicationContext(), "家长号码不能为空");
            return false;
        }

        province = tv_Province.getText().toString().trim();
        if (TextUtils.isEmpty(province)) {
            MethodUtils.showToast(getApplicationContext(), "省份不能为空");
            return false;
        }

        city = tv_city.getText().toString().trim();
        if (TextUtils.isEmpty(city)) {
            MethodUtils.showToast(getApplicationContext(), "城市不能为空");
            return false;
        }

        area = tv_area.getText().toString().trim();
        if (TextUtils.isEmpty(area)) {
            MethodUtils.showToast(getApplicationContext(), "区/县不能为空");
            return false;
        }

        school = et_school_name.getText().toString().trim();
        if (TextUtils.isEmpty(area)) {
            MethodUtils.showToast(getApplicationContext(), "学校不能为空");
            return false;
        }
        return true;
    }

    private String selectedProvince = "";
    private String selectedCity = "";
    private String selectedArea = "";

    public void showProvinceList() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(provinces, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tv_city.setText("");
                tv_area.setText("");
                selectedProvince = provinces[which];
                tv_Province.setText(selectedProvince);
                ViseLog.d(selectedProvince);
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    public void showCityList() {
        List<String> cityList = AreaUtil.getCities(this, selectedProvince);
        if (cityList != null && cityList.size() > 0) {
            cities = new String[cityList.size()];
            cityList.toArray(cities);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(cities, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tv_area.setText("");
                selectedCity = cities[which];
                tv_city.setText(selectedCity);
                ViseLog.d(selectedCity);
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    public void showAreaList() {
        List<String> areaList = AreaUtil.getAreas(this, selectedProvince, selectedCity);
        if (areaList != null && areaList.size() > 0) {
            areas = new String[areaList.size()];
            areaList.toArray(areas);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(areas, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedArea = areas[which];
                tv_area.setText(selectedArea);
                ViseLog.d(selectedArea);
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.sp_sex:
                String[] sexArray = getResources().getStringArray(R.array.sex_array);
                sex = sexArray[position];
                break;
            case R.id.sp_class:
                String[] classArray = getResources().getStringArray(R.array.class_array);
                schoolClass = classArray[position];
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
