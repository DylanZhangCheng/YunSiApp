package com.lehua.tablet0306.activity;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lehua.tablet0306.R;
import com.lehua.tablet0306.bean.User;
import com.lehua.tablet0306.okhttp.HttpRequest;
import com.lehua.tablet0306.okhttp.URLConstant;
import com.lehua.tablet0306.utils.MethodUtils;
import com.lehua.tablet0306.utils.PushAdapter;
import com.lehua.tablet0306.utils.PushMessage;
import com.lehua.tablet0306.utils.SpHelp;
import com.vise.log.ViseLog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

public class PushListActivity extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout ll_back;
    private TextView tv_my_collect;
    private ListView pushList;           //专家建议ListView
    private TextView tv_pre, tv_1, tv_2, tv_3, tv_last, tv_ellipsis, tv_next, tv_jump;
    private EditText et_target_page;

    private PushAdapter pushAdapter;
    private ArrayList<PushMessage> messageList;
    private int page = 1;

    private boolean isEmpty = true;        //获取当期页面后，会返回一个 数据has_next，代表获取该页面后是否还有下一个页面
    private static final boolean FLAG_HISTORY = true;
    private static final boolean FLAG_FAVORITE = false;
    private boolean flag = FLAG_HISTORY;    //代表当前页面是在历史记录页面还是 收藏界面
    private int itemCount, pageCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_push);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        initView();
        initListener();
        taskGetPush();
    }

    private void initView() {
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        tv_my_collect = (TextView) findViewById(R.id.tv_my_collect);
        pushList = (ListView) findViewById(R.id.list);

        tv_pre = (TextView) findViewById(R.id.tv_pre);
        tv_1 = (TextView) findViewById(R.id.tv_1);
        tv_2 = (TextView) findViewById(R.id.tv_2);
        tv_3 = (TextView) findViewById(R.id.tv_3);
        tv_last = (TextView) findViewById(R.id.tv_last);
        tv_next = (TextView) findViewById(R.id.tv_next);
        tv_jump = (TextView) findViewById(R.id.tv_jump);
        tv_ellipsis = (TextView) findViewById(R.id.tv_ellipsis);

        et_target_page = (EditText) findViewById(R.id.et_target_page);

    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initListener() {

        ll_back.setOnClickListener(this);
        tv_my_collect.setOnClickListener(this);
        pushList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PushMessage msg = (PushMessage) pushAdapter.getItem(position);
                Intent intent = new Intent(PushListActivity.this, PushDetailActivity.class);
                intent.putExtra("msg", msg);
                startActivity(intent);
            }
        });

        tv_pre.setOnClickListener(this);
        tv_1.setOnClickListener(this);
        tv_2.setOnClickListener(this);
        tv_3.setOnClickListener(this);
        tv_last.setOnClickListener(this);
        tv_next.setOnClickListener(this);
        tv_jump.setOnClickListener(this);
    }

    private void initData() {
        messageList = new ArrayList<>();
        pushAdapter = new PushAdapter(messageList, PushListActivity.this);
        pushList.setAdapter(pushAdapter);
        initPushHistory();
    }

    private void initPushHistory() {
        flag = FLAG_HISTORY;
        tv_my_collect.setText(R.string.my_collect);   //我的收藏
        page = 1;
        getPushMessage(page);
    }


    public void initPushFavorite() {
        flag = FLAG_FAVORITE;
        tv_my_collect.setText(R.string.my_history);   //历史消息
        page = 1;
        getPushMessage(page);
    }


    /**
     * @param page 查看第几页的历史消息
     */
    private void getPushMessage(int page) {
        messageList.clear();
        pushAdapter.notifyDataSetChanged();

        Map<String, Object> params = new HashMap<>();
        User user = (User) SpHelp.getObject(SpHelp.USER_ENTITY);
        params.put("account", user.getAccount());
        params.put("page", page);
        params.put("limit", 5);
        HttpRequest.get(
                flag ? URLConstant.URL_PUSH_HISTORY : URLConstant.URL_FAVORITE_HISTORY,
                null, params, new HttpRequest.HttpRequestCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        String error = response.optString("error");
                        if (error.equals("0")) {
                            isEmpty = !response.optJSONObject("data").optBoolean("has_next");
                            JSONArray list = response.optJSONObject("data").optJSONArray("data");
                            itemCount = response.optJSONObject("data").optInt("itemCount");
                            ViseLog.d("itemCount = " + itemCount);
                            pageCount = (int) Math.ceil(itemCount / 5.0);
                            ViseLog.d("pageCount = " + pageCount);
                            if (pageCount == 1) {
                                tv_1.setVisibility(View.VISIBLE);
                                tv_2.setVisibility(View.GONE);
                                tv_3.setVisibility(View.GONE);
                                tv_ellipsis.setVisibility(View.GONE);
                                tv_last.setVisibility(View.GONE);
                            } else if (pageCount == 2) {
                                tv_1.setVisibility(View.VISIBLE);
                                tv_2.setVisibility(View.VISIBLE);
                                tv_3.setVisibility(View.GONE);
                                tv_ellipsis.setVisibility(View.GONE);
                                tv_last.setVisibility(View.GONE);
                            } else if (pageCount == 3) {
                                tv_1.setVisibility(View.VISIBLE);
                                tv_2.setVisibility(View.VISIBLE);
                                tv_3.setVisibility(View.VISIBLE);
                                tv_ellipsis.setVisibility(View.GONE);
                                tv_last.setVisibility(View.GONE);
                            } else if (pageCount == 4) {
                                tv_1.setVisibility(View.VISIBLE);
                                tv_2.setVisibility(View.VISIBLE);
                                tv_3.setVisibility(View.VISIBLE);
                                tv_ellipsis.setVisibility(View.GONE);
                                tv_last.setText("4");
                            } else {
                                tv_1.setVisibility(View.VISIBLE);
                                tv_2.setVisibility(View.VISIBLE);
                                tv_3.setVisibility(View.VISIBLE);
                                tv_ellipsis.setVisibility(View.VISIBLE);
                                tv_last.setVisibility(View.VISIBLE);
                                tv_last.setText(pageCount + "");
                            }
                            for (int i = 0; i < list.length(); i++) {
                                PushMessage pushMessage = new PushMessage();
                                JSONObject item = list.optJSONObject(i);
                                pushMessage.setId(item.optString("id"));
                                pushMessage.setPhoto(item.optString("photo"));
                                pushMessage.setBrief(item.optString("brief"));
                                pushMessage.setTitle(item.optString("title"));
                                pushMessage.setDate(item.optString("date"));
                                pushMessage.setCollected(item.optBoolean("favorite") ? 1 : -1);
                                messageList.add(pushMessage);
                            }
                            pushAdapter.notifyDataSetChanged();
                        } else {
                            MethodUtils.showToast(getApplicationContext(), response.optString("error_info"));
                        }
                    }

                    @Override
                    public void onFailure() {
                        MethodUtils.showToast(getApplicationContext(), "请求失败, 请稍后重试");
                    }
                });
    }


    @Override
    public void onBackPressed() {
        if (flag == FLAG_HISTORY) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
        } else {
            initPushHistory();
        }
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK && !flag) {
//            initPushHistory();
//            return true;
//        } else
//            return super.onKeyDown(keyCode, event);
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_back:
                if (flag == FLAG_FAVORITE) {
                    initPushHistory();
                } else {
                    finish();
                }
                break;
            case R.id.tv_my_collect:
                if (flag) {
                    initPushFavorite();
                } else {
                    initPushHistory();
                }
                break;
            case R.id.tv_pre:
                page--;
                if (page == 0) {
                    page = 1;
                    MethodUtils.showToast(getApplicationContext(), "已到最前页");
                } else {
                    getPushMessage(page);
                }
                break;

            case R.id.tv_next:
                if (!isEmpty) {
                    page++;
                    getPushMessage(page);
                } else {
                    MethodUtils.showToast(getApplicationContext(), "已到最后一页");
                }
                break;
            case R.id.tv_1:
                if (flag) {
                    initPushHistory();
                } else {
                    initPushFavorite();
                }
                break;
            case R.id.tv_2:
                getPushMessage(2);
                break;
            case R.id.tv_3:
                getPushMessage(3);
                break;
            case R.id.tv_last:
                getPushMessage(pageCount);
                break;

            case R.id.tv_jump:
                String jumpPager = et_target_page.getText().toString().trim();
                if (jumpPager.equals("")) {
                    Toast.makeText(this, "页数不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    int pager = Integer.valueOf(jumpPager);
                    if (pager >= 1 && pager <= pageCount) {
                        getPushMessage(pager);
                    } else {
                        Toast.makeText(this, "请输入正确的页数", Toast.LENGTH_SHORT).show();
                    }

                }

                break;
        }
    }

    /**
     * 定时获取推送消息,通过广播通知
     */
    private void taskGetPush() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (MethodUtils.isWifi(getApplicationContext())
                        || MethodUtils.is3G(getApplicationContext())) {
                    pushReceive();
                }
            }
        }, 1000, 30 * 1000);// 一分钟检查一次推送
    }

    private AtomicInteger pushCount = new AtomicInteger(0);
    private Timer timer;

    private void pushReceive() {
        Map<String, Object> params = new HashMap<>();
        params.put("account", SpHelp.getUserId());
        HttpRequest.get(URLConstant.URL_PUSH, null, params, new HttpRequest.HttpRequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                if (response.optString("error").equals("0")) {
                    JSONArray list = response.optJSONArray("data");

                    ArrayList<PushMessage> pushMessages = new ArrayList<>();
                    for (int i = 0; i < list.length(); i++) {
                        PushMessage pushMessage = new PushMessage();
                        JSONObject tem = list.optJSONObject(i);
                        pushMessage.setId(tem.optString("id"));
                        pushMessage.setTitle(tem.optString("title"));
                        pushMessage.setBrief(tem.optString("brief"));
                        pushMessages.add(pushMessage);
                    }

                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    for (PushMessage pushMessage : pushMessages) {
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(PushListActivity.this);
                        builder.setContentTitle(pushMessage.getTitle());
                        builder.setContentText(pushMessage.getBrief());
                        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.noti_big));
                        builder.setSmallIcon(R.drawable.noti_small);
                        builder.setTicker("如医新消息");
                        builder.setWhen(System.currentTimeMillis());
                        builder.setAutoCancel(true);
                        builder.setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.ring));

                        Intent intent = new Intent(PushListActivity.this, PushDetailActivity.class);
                        intent.putExtra("msg", pushMessage);
                        PendingIntent pt = PendingIntent.getActivity(PushListActivity.this, pushCount.getAndDecrement(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        builder.setContentIntent(pt);
                        notificationManager.notify(pushCount.getAndDecrement(), builder.build());
                    }
                }
            }

            @Override
            public void onFailure() {
                ViseLog.d("获取失败了");
            }
        });
    }
}
