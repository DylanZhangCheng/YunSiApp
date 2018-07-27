package com.lehua.tablet0306.application;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import com.lehua.tablet0306.utils.MethodUtils;
import com.vise.log.ViseLog;
import com.vise.log.inner.DefaultTree;

import java.io.File;
import java.util.Properties;

/**
 * Created by lehua on 2018/2/1.
 */

public class MyApplication extends Application {
    public static SharedPreferences sp = null;
    //    public static String Default_Ip = "http://sdev.whu.edu.cn/yunsi/";
    public static String Default_Ip = "http://zhuji.qrsyb.cn/";
    public static String Ip_Address = "http://zhuji.qrsyb.cn/";

    @Override
    public void onCreate() {
        super.onCreate();
        if (sp == null) {
            sp = getSharedPreferences("sp", Context.MODE_PRIVATE);
        }
        initViselog();
//        initIpConfig();
    }

    public void initIpConfig() {
        String configPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/yunsi_configuation.txt";
        File file = new File(configPath);
        if (file.exists()) {
            Properties pro = MethodUtils.loadConfig(configPath);
            if (pro.get("ip") != null) {
                ViseLog.d(pro.get("ip"));
                Ip_Address = (String) pro.get("ip");
            }
        } else {
            Ip_Address = Default_Ip;
            Properties prop = new Properties();
            prop.put("ip", Default_Ip);
            MethodUtils.saveConfig(configPath, prop);
        }
    }

    public void initViselog() {
        ViseLog.getLogConfig()
                .configAllowLog(true)//是否输出日志
                .configShowBorders(true)//是否排版显示
                .configTagPrefix("ViseLog")//设置标签前缀
                .configFormatTag("%d{HH:mm:ss:SSS} %t %c{-5}")//个性化设置标签，默认显示包名
                .configLevel(Log.VERBOSE);//设置日志最小输出级别，默认Log.VERBOSE
        ViseLog.plant(new DefaultTree());//添加打印日志信息到Logcat的树
    }

}
