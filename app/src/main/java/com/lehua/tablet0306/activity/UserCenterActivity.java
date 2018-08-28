package com.lehua.tablet0306.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.lehua.tablet0306.utils.CircleImageView;
import com.lehua.tablet0306.utils.ListViewUtil;
import com.lehua.tablet0306.utils.MethodUtils;
import com.lehua.tablet0306.utils.SpHelp;
import com.lehua.tablet0306.utils.UserImg;
import com.vise.log.ViseLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class UserCenterActivity extends AppCompatActivity implements View.OnClickListener {
    //TODO: 可以放到数据类里
    static final String[] primarySubjects = {"chinese", "math", "english", "comprehensive"};
    static final String[] middleSubjects = {"chinese", "math", "english", "physics", "chemistry", "biology", "politics", "history", "geography"};
    static final String[] primarySubjectsTags = {"语文", "数学", "英语", "综合"};
    static final String[] middleSubjectsTags = {"语文", "数学", "英语", "物理", "化学", "生物", "政治", "历史", "地理"};

    private String grade = "p"; // p表示小学 m表示初中
    private String account;
    private TextView ct_name;
    private TextView ct_age;
    private TextView ct_class;

    private CircleImageView center_image;
    private Bitmap mBitmap;
    protected static final int CHOOSE_PICTURE = 0;
    protected static final int TAKE_PICTURE = 1;
    protected static Uri tempUri;
    private static final int CROP_SMALL_PICTURE = 2;

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
        setContentView(R.layout.activity_user_center);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();


        initView();
        initData();
        initList();
        bindListener();
    }

    private void initData() {

        User user = (User) SpHelp.getObject(SpHelp.USER_ENTITY);
        account = user.getAccount();
        grade = user.getSchoolClass().contains("小学") ? "p" : "m";

        ct_name.append(user.getName());
        ct_age.append(String.valueOf(user.getAge()));
        ct_class.append(user.getSchoolClass());

        tv_title.setText(grade == "p" ? "小学" : "初中");

        UserImg userImg = new UserImg();
        userImg.loadUserImg(user.getAccount(),center_image);

        showUserInfo();
    }

    private void initView() {
        tv_title = findViewById(R.id.tv_title);
        tv_back = findViewById(R.id.tv_back);
        ll_cb_group = findViewById(R.id.ll_cb_group);
        ll_rg_group = findViewById(R.id.ll_rg_group);
        et_hobby = findViewById(R.id.et_hobby);
        btn_finish = findViewById(R.id.btn_finish);

        lv_book = findViewById(R.id.lv_book);
        lv_process = findViewById(R.id.lv_process);

        ct_name = (TextView) findViewById(R.id.ct_name);
        ct_age = (TextView) findViewById(R.id.ct_age);
        ct_class = (TextView) findViewById(R.id.ct_class);

        center_image = (CircleImageView) findViewById(R.id.center_image);

//        time_for_homework = findViewById(R.id.rg_q2);
//        time_for_training_class = findViewById(R.id.rg_q6);
//        voluntary = findViewById(R.id.rg_q7);
    }

    private void initList() {
        String[] subjects = grade.equals("p") ? primarySubjectsTags : middleSubjectsTags;

        List<String> list = Arrays.asList(subjects);
        bookAdapter = new BookAdapter(UserCenterActivity.this, R.layout.book_item, list);
        lv_book.setAdapter(bookAdapter);
        ListViewUtil.setListViewHeightBasedOnChildren(lv_book);

        processAdapter = new ProcessAdapter(UserCenterActivity.this, R.layout.process_item, list);
        lv_process.setAdapter(processAdapter);
        ListViewUtil.setListViewHeightBasedOnChildren(lv_process);
    }

    private void bindListener() {
        tv_back.setOnClickListener(this);
        btn_finish.setOnClickListener(this);
        center_image.setOnClickListener(this);
       // rb_service.setOnClickListener(this);
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
            case R.id.center_image:
                showChoosePicDialog();
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


            //return json string
            System.out.println(json.toString());
            return json.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 显示修改图片的对话框
     */
    protected void showChoosePicDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserCenterActivity.this);
        builder.setTitle("上传头像");
        String[] items = { "选择本地照片", "拍照" };
        builder.setNegativeButton("取消", null);
        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case CHOOSE_PICTURE: // 选择本地照片
                        Intent openAlbumIntent = new Intent(
                                Intent.ACTION_GET_CONTENT);
                        openAlbumIntent.setType("image/*");
                        //用startActivityForResult方法，待会儿重写onActivityResult()方法，拿到图片做裁剪操作
                        startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
                        break;
                    case TAKE_PICTURE: // 拍照
                        Intent openCameraIntent = new Intent(
                                MediaStore.ACTION_IMAGE_CAPTURE);
                        tempUri = Uri.fromFile(new File(Environment
                                .getExternalStorageDirectory(), "temp_image.jpg"));
                        // 将拍照所得的相片保存到SD卡根目录
                        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
                        startActivityForResult(openCameraIntent, TAKE_PICTURE);
                        break;
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == MainActivity.RESULT_OK) {
            switch (requestCode) {
                case TAKE_PICTURE:
                    cutImage(tempUri); // 对图片进行裁剪处理
                    break;
                case CHOOSE_PICTURE:
                    cutImage(data.getData()); // 对图片进行裁剪处理
                    break;
                case CROP_SMALL_PICTURE:
                    if (data != null) {
                        setImageToView(data); // 让刚才选择裁剪得到的图片显示在界面上
                    }
                    break;
            }
        }
    }

    /**
     * 裁剪图片
     */
    protected void cutImage(Uri uri) {
        if (uri == null) {
            Log.i("alanjet", "The uri is not exist.");
        }
        tempUri = uri;
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("circleCrop", true);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);

        startActivityForResult(intent, CROP_SMALL_PICTURE);
    }
    /**
     * 保存裁剪之后的图片数据
     */
    protected void setImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            mBitmap = extras.getParcelable("data");//裁剪后的图片，Bitmap
            center_image.setImageBitmap(mBitmap);//裁剪后在activity即时显示图片
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();
            byte[] buffer = stream.toByteArray();
            //加密成base64字符
            String user_img = Base64.encodeToString(buffer, 0, buffer.length,Base64.DEFAULT);

                JSONObject object = new JSONObject();
                object.put("account", account);
                object.put("user_img",user_img);

            HttpRequest.post(URLConstant.URL_USER_IMG,object.toString(),new HttpRequest.HttpRequestCallback() {
                @Override
                public void onSuccess(JSONObject response){
                    if(response.optString("error").equals("ok")){
                        MethodUtils.showToast(getApplicationContext(), "头像上传成功");
                    }
                }
                @Override
                public void onFailure() {
                    MethodUtils.showToast(getApplicationContext(), "头像上传失败，请重新上传");
                }
              });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 加载个人信息
     */
    private void showUserInfo(){
        JSONObject json = new JSONObject();
        try {
            json.put("account", account);
            json.put("grade",grade);

            HttpRequest.post(URLConstant.URL_INFO, json.toString(), new HttpRequest.HttpRequestCallback() {
                @Override
                public void onSuccess(JSONObject response) {
//                    JSONObject biology = response.optJSONObject("biology");
//                    JSONObject chemistry = response.optJSONObject("chemistry");
//                    JSONObject chinese = response.optJSONObject("chinese");
//                    JSONObject geography = response.optJSONObject("geography");
//                    JSONObject english = response.optJSONObject("english");
//                    JSONObject history = response.optJSONObject("history");
//                    JSONObject math = response.optJSONObject("math");
//                    JSONObject physics = response.optJSONObject("physics");
//                    JSONObject politics = response.optJSONObject("politics");
//
//                    String biology_score = biology.optString("score");
//                    String biology_version = biology.optString("version");

//                    if("m" == response.optString("grade")){
//
//                       if(biology_version.contains("人教版")){
//                           RadioButton one = (RadioButton) ((RadioGroup) findViewById(R.id.rb_book)).getChildAt(1);
//                           one.setChecked(true);
//                       }
//
//                    }

                    String rg_0 = response.optString("hobby");
                    String rg_1 = response.optString("learningburden");
                    String rg_2 = response.optString("timeforhomework");
                    String rg_3 = response.optString("trainingclass");
                    String rg_4 = response.optString("classtype");
                    String rg_5 = response.optString("trainingclasstype");
                    String rg_6 = response.optString("timefortrainingclass");
                    String rg_7 = response.optString("voluntary");
                    ViseLog.d(response);

                    if(rg_0.contains("音乐")){
                        CheckBox one = findViewById(R.id.rg_q0_1);
                        one.setChecked(true);
                    }
                    if(rg_0.contains("舞蹈")){
                        CheckBox two = findViewById(R.id.rg_q0_2);
                        two.setChecked(true);
                    }
                    if(rg_0.contains("文艺")){
                        CheckBox three = findViewById(R.id.rg_q0_3);
                        three.setChecked(true);
                    }
                    if(rg_0.contains("科技")){
                        CheckBox four = findViewById(R.id.rg_q0_4);
                        four.setChecked(true);
                    }
                    if(rg_0.contains("武术")){
                        CheckBox five = findViewById(R.id.rg_q0_5);
                        five.setChecked(true);
                    }
                    if(rg_0.contains("美术")){
                        CheckBox six = findViewById(R.id.rg_q0_6);
                        six.setChecked(true);}
                    if(rg_0.contains("体育")){
                        CheckBox seven = findViewById(R.id.rg_q0_7);
                        seven.setChecked(true);}

                    if(rg_1.contains("很重")){
                        RadioButton one = findViewById(R.id.rg_q1_1);
                        one.setChecked(true);
                    }else if(rg_1.contains("适合")){
                        RadioButton two = findViewById(R.id.rg_q1_2);
                        two.setChecked(true);
                    }else if(rg_1.contains("轻松")){RadioButton three = findViewById(R.id.rg_q1_3);
                        three.setChecked(true);}

                    if(rg_2.contains("一小时内")){
                        RadioButton one = findViewById(R.id.rg_q2_1);
                        one.setChecked(true);
                    }else if(rg_2.contains("一至两小时")){
                        RadioButton two = findViewById(R.id.rg_q2_2);
                        two.setChecked(true);
                    }else if(rg_2.contains("两小时以上")){RadioButton three = findViewById(R.id.rg_q2_3);
                        three.setChecked(true);}

                    if(rg_3.contains("有但需要交费")){
                        RadioButton one = findViewById(R.id.rg_q3_1);
                        one.setChecked(true);
                    }else if(rg_3.contains("有无需交费")){
                        RadioButton two = findViewById(R.id.rg_q3_2);
                        two.setChecked(true);
                    }else if(rg_3.contains("没有")){RadioButton three = findViewById(R.id.rg_q3_3);
                        three.setChecked(true);}

                    if(rg_4.contains("重点班")){
                        RadioButton one = findViewById(R.id.rg_q4_1);
                        one.setChecked(true);
                    }else if(rg_4.contains("普通班")){
                        RadioButton two = findViewById(R.id.rg_q4_2);
                        two.setChecked(true);
                    }else if(rg_4.contains("实验班")){RadioButton three = findViewById(R.id.rg_q4_3);
                        three.setChecked(true);}

                    if(rg_5.contains("语文")){
                        CheckBox one = findViewById(R.id.rg_q5_1);
                        one.setChecked(true);
                    }
                    if(rg_5.contains("数学")){
                        CheckBox two = findViewById(R.id.rg_q5_2);
                        two.setChecked(true);
                    }
                    if(rg_5.contains("英语")){
                        CheckBox three = findViewById(R.id.rg_q5_3);
                        three.setChecked(true);
                    }
                    if(rg_5.contains("历史")){
                        CheckBox four = findViewById(R.id.rg_q5_4);
                        four.setChecked(true);
                    }
                    if(rg_5.contains("物理")){
                        CheckBox five = findViewById(R.id.rg_q5_5);
                        five.setChecked(true);
                    }
                    if(rg_5.contains("化学")){
                        CheckBox six = findViewById(R.id.rg_q5_6);
                        six.setChecked(true);}

                    if(rg_6.contains("一小时内")){
                        RadioButton one = findViewById(R.id.rg_q6_1);
                        one.setChecked(true);
                    }else if(rg_6.contains("一至两小时")){
                        RadioButton two = findViewById(R.id.rg_q6_2);
                        two.setChecked(true);
                    }else if(rg_6.contains("两小时以上")){RadioButton three = findViewById(R.id.rg_q6_3);
                        three.setChecked(true);}

                    if(rg_7.contains("是")){
                            RadioButton yes = findViewById(R.id.rg_q7_1);
                            yes.setChecked(true); }
                    else if(rg_7.contains("否")){RadioButton no = findViewById(R.id.rg_q7_2);
                            no.setChecked(false);}
                }

                @Override
                public void onFailure() {
                    MethodUtils.showToast(getApplicationContext(), "请求失败, 请稍后重试");
                    ViseLog.d("请求失败, 请稍后重试");
                }
            });

        }catch (JSONException e) {
        e.printStackTrace(); }
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

                    MethodUtils.showToast(getApplicationContext(), "个人信息修改成功");

                    //TODO: 可以把数据保存在本地
                    Intent intent = new Intent(UserCenterActivity.this, RadarChangeActivity.class);
                    intent.putExtra("process", new ArrayList<>(processAdapter.getProcess()));
                    startActivity(intent);
                    finish();
                } else {
                    ViseLog.d("个人信息修改失败: " + response.optString("error_info"));
                    MethodUtils.showToast(getApplicationContext(), "个人信息修改失败: " + response.optString("error_info"));
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

