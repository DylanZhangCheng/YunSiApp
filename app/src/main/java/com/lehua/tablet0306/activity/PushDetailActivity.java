package com.lehua.tablet0306.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lehua.tablet0306.R;
import com.lehua.tablet0306.okhttp.HttpRequest;
import com.lehua.tablet0306.okhttp.URLConstant;
import com.lehua.tablet0306.utils.MethodUtils;
import com.lehua.tablet0306.utils.PushMessage;
import com.lehua.tablet0306.utils.SpHelp;
import com.mob.MobSDK;
import com.vise.log.ViseLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.sharesdk.onekeyshare.OnekeyShare;

public class PushDetailActivity extends AppCompatActivity {

    private TextView tv_back, tv_operate;
    private View optionView;
    private PopupWindow optionWindow;
    private AlertDialog deleteDialog;
    LinearLayout ll_collect, ll_delete, ll_share;
    TextView tv_collect, tv_delete, tv_share;

    private FrameLayout frame_webView;
    private WebView webView;
    private PushMessage pushMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_push_detial);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        initView();
        initPopubWindow();
    }

    private void initView() {
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_operate = (TextView) findViewById(R.id.tv_operate);
        tv_operate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (webView.getProgress() == 100)
                    optionWindow.showAsDropDown(view, -3, 0);
                else
                    MethodUtils.showToast(getApplicationContext(), "没有数据");
            }
        });

        frame_webView = (FrameLayout) findViewById(R.id.frame_webView);

        webView = new WebView(PushDetailActivity.this);
        webView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        WebSettings webSettings = webView.getSettings();
        //设置 webView的缓存模式
        if (MethodUtils.is3G(PushDetailActivity.this) || MethodUtils.isWifi(PushDetailActivity.this)) {
            webSettings.setCacheMode(WebSettings.LOAD_NORMAL);
        } else {
            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }

        //设置AppCache缓存
        String appCachePath = getFilesDir() + "/appCache";
        webSettings.setAppCachePath(appCachePath);
        webSettings.setAppCacheEnabled(true);

        //打开JavaScript
        webSettings.setJavaScriptEnabled(true);
        //打开 DomStorage 缓存
        webSettings.setDomStorageEnabled(true);
        //打开 Databases 缓存
        webSettings.setDatabaseEnabled(true);

        webSettings.setDefaultTextEncodingName("UTF-8");

        //设置不用系统浏览器打开,直接显示在当前Webview
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                MethodUtils.showToast(PushDetailActivity.this, "加载进度为：" + newProgress + "%");
            }
        });

        frame_webView.addView(webView);
    }

    private void initPopubWindow() {
        optionView = this.getLayoutInflater().inflate(R.layout.bracelet_advice_operate, null);
        ll_collect = (LinearLayout) optionView.findViewById(R.id.ll_collect);
        ll_delete = (LinearLayout) optionView.findViewById(R.id.ll_delete);
        ll_share = (LinearLayout) optionView.findViewById(R.id.ll_share);

        tv_collect = (TextView) optionView.findViewById(R.id.tv_collect);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels / 3;//宽度
        optionWindow = new PopupWindow(optionView, width, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        optionWindow.setOutsideTouchable(true);
        optionWindow.setBackgroundDrawable(new BitmapDrawable());

        ll_collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> parmas = new HashMap<>();
                parmas.put("uid", SpHelp.getUserId());
                parmas.put("mid", pushMessage.getId());
                HttpRequest.get(URLConstant.URL_FAVORITE, null, parmas, new HttpRequest.HttpRequestCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        ViseLog.d(response);
                        if (response.optString("error").equals("0")) {
                            if (pushMessage.getCollected() == -1) {
                                pushMessage.setCollected(1);
                                tv_collect.setText("取消收藏");
                                MethodUtils.showToast(getApplicationContext(), "收藏成功");
                            } else {
                                pushMessage.setCollected(-1);
                                tv_collect.setText("收藏");
                                MethodUtils.showToast(getApplicationContext(), "取消收藏成功");
                            }
                        } else {
                            MethodUtils.showToast(getApplicationContext(), "操作失败:　" + response.optString("error_info"));
                        }
                    }

                    @Override
                    public void onFailure() {
                        MethodUtils.showToast(getApplicationContext(), "请求失败, 请稍后重试");
                    }
                });

                if (null != optionWindow)
                    optionWindow.dismiss();
            }
        });

        ll_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteDialog();
            }
        });


        MobSDK.init(this);
        ll_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShare();
            }
        });
    }

    private void initData() {
        pushMessage = (PushMessage) getIntent().getSerializableExtra("msg");
        if (pushMessage.getCollected() == 1)
            tv_collect.setText("取消收藏");
        if (!TextUtils.isEmpty(pushMessage.getId())) {
            Map<String, Object> params = new HashMap<>();
            params.put("id", pushMessage.getId());

            ViseLog.d("pushMessage.getId() :" + pushMessage.getId());

            HttpRequest.get(URLConstant.URL_PUSH_DETAIL, null, params, new HttpRequest.HttpRequestCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    if (response.optString("error").equals("0")) {
                        String htmlText = response.optString("data");
                        ViseLog.d(htmlText);
                        webView.loadDataWithBaseURL(null, htmlText, "text/html", "utf-8", null);
                    }
                }

                @Override
                public void onFailure() {

                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PushDetailActivity.this);
        deleteDialog = builder.setMessage("                   删除后不可恢复")
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        JSONObject params = new JSONObject();
                        try {
                            params.put("uid", SpHelp.getUserId())
                                    .put("mid", pushMessage.getId());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        HttpRequest.post(URLConstant.URL_DELETE_HISTORY, params.toString(), new HttpRequest.HttpRequestCallback() {
                            @Override
                            public void onSuccess(JSONObject response) {
                                if (response.optString("error").equals("0")) {
                                    MethodUtils.showToast(getApplicationContext(), "删除成功");
                                    deleteDialog.dismiss();
                                    optionWindow.dismiss();
                                    finish();
                                } else {
                                    MethodUtils.showToast(getApplicationContext(), "删除失败: " + response.optString("error_info"));
                                }
                            }

                            @Override
                            public void onFailure() {
                                MethodUtils.showToast(getApplicationContext(), "请求失败, 请稍后重试");
                            }
                        });
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteDialog.dismiss();
                        optionWindow.dismiss();
                    }
                }).create();

        deleteDialog.show();
    }

    private void showShare() {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        oks.setTitle("hhhhhhhhh");

        oks.setTitleUrl("http://baidu.com");

        oks.setText("智能私教");

        oks.show(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webView.clearHistory();
            ((ViewGroup) webView.getParent()).removeView(webView);
            webView.destroy();
            webView = null;
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }
}
